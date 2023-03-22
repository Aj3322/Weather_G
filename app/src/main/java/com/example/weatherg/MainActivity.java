package com.example.weatherg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ProgressBar pb_loader;
    RelativeLayout home_layout;
    ImageView home_back_ground;
    TextView city_name, txt_show_temp, conditionI, rc_heading;
    LinearLayout L_layout;
    EditText edt_city;
    ImageView search, icon_show;
    RecyclerView recyclerView_weather;
    ArrayList<WeatherRVModel> list;
    Adaptor adaptor;
    LocationManager locationManager;
    int PERMISSION_CODE = 1;
    String cityName;

WeatherRVModel model=new WeatherRVModel();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        pb_loader = findViewById(R.id.pb_loader);
        home_layout = findViewById(R.id.home_layout);
        home_back_ground = findViewById(R.id.home_back_ground);
        city_name = findViewById(R.id.city_name);
        txt_show_temp = findViewById(R.id.txt_show_temp);
        conditionI = findViewById(R.id.condition);
        rc_heading = findViewById(R.id.rc_Heading);
        L_layout = findViewById(R.id.L_Layout);
        edt_city = findViewById(R.id.edt_city);
        search = findViewById(R.id.search);
        icon_show = findViewById(R.id.icon_show);
        recyclerView_weather=findViewById(R.id.Recycle_weather);

        list = new ArrayList<>();
        adaptor = new Adaptor(this, list);

        recyclerView_weather.setAdapter(adaptor);


//        recyclerView_weather.scrollToPosition(getPosition());

        conditionI.setText(model.getTime());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName=getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edt_city.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please inter the City Name", Toast.LENGTH_SHORT).show();
                }else {
                    city_name.setText(cityName);
                    getWeatherInfo(city);

                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please give the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName ="Not Found";
        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> address =gcd.getFromLocation(latitude,longitude,10);
            for (Address adr:address){
                if (adr!=null){
                    String city = adr.getLocality();
                    if (city!=null){
                        cityName=city;
                    }else {
                        Log.d("city","city_error");
                        Toast.makeText(this, "CITY NOT FOUND", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

      return cityName;

    }

    private void getWeatherInfo(String cityName) {
        String url_api= "https://api.weatherapi.com/v1/forecast.json?key=7c70ca830ff54641aae43002232203&q="+cityName+"&aqi=no";
        city_name.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url_api, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                list.clear();
                try {
                    String temp = response.getJSONObject("current").getString("temp_c");
                    txt_show_temp.setText(temp+ "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition =response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon =response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    conditionI.setText(condition);
                    Picasso.get().load("https:"+conditionIcon).into(icon_show);
                    if (isDay==1){
                        Picasso.get().load(R.drawable.img).into(home_back_ground);
                    }else {
                        Picasso.get().load(R.drawable.img_1).into(home_back_ground);
                    }

                   JSONObject forecastObj =response.getJSONObject("forecast");
                   JSONObject forecastO  =forecastObj.getJSONArray("forecastday").getJSONObject(0);
                   JSONArray hourArray = forecastO.getJSONArray("hour");

                   for (int i=0;i<hourArray.length() ;i++){
                       JSONObject hourOj = hourArray.getJSONObject(i);

                       String time =hourOj.getString("time");
                       String temperature =hourOj.getString("temp_c");
                       String img =hourOj.getJSONObject("condition").getString("icon");
                       String windSpeedO =hourOj.getString("wind_kph");

                       list.add(new WeatherRVModel(time,temperature,img,windSpeedO));
                   }
                   adaptor.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                pb_loader.setVisibility(View.GONE);
                home_layout.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid City", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}