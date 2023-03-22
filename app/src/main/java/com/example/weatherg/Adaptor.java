package com.example.weatherg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Adaptor extends RecyclerView.Adapter<Adaptor.ViewHolder> {

    Context context;
    ArrayList<WeatherRVModel> list;

    public Adaptor(Context context, ArrayList<WeatherRVModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Adaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item_show,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptor.ViewHolder holder, int position) {

        WeatherRVModel model= list.get(position);


        Picasso.get().load("https:"+model.getIcon()).into(holder.imgCondition);
        holder.tempShow.setText(model.getTemp() + "°C");
        holder.windSpeed.setText(model.getWindSpeed() + " Km/h");

        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
            try {
                Date t = input.parse(model.getTime());
                holder.timeShow.setText(output.format(t));
            } catch (ParseException e) {
                e.printStackTrace();
            }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tempShow,timeShow,windSpeed;
        ImageView imgCondition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tempShow= itemView.findViewById(R.id.c_temp_show);
            timeShow= itemView.findViewById(R.id.c_time_show);
            imgCondition=itemView.findViewById(R.id.img_condition);
            windSpeed = itemView.findViewById(R.id.c_wind_speed);



        }
    }
}
