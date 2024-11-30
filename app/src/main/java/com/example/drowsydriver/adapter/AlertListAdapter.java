package com.example.drowsydriver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drowsydriver.R;
import com.example.drowsydriver.model.AlertListModel;

import java.util.ArrayList;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.MyViewHolder> {
    Context context;
    ArrayList<AlertListModel> list;

    public AlertListAdapter(Context context, ArrayList<AlertListModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public AlertListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_history_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertListAdapter.MyViewHolder holder, int position) {
        AlertListModel model = list.get(position);
        holder.fullName.setText(model.getFullName());
        holder.plateNum.setText(model.getPlateNum());
        holder.dateAndTime.setText(model.getDateAndTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullName,
                plateNum,
                dateAndTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName_Textview);
            plateNum = itemView.findViewById(R.id.plateNumber_Textview);
            dateAndTime = itemView.findViewById(R.id.dateAndTime_TextView);
        }
    }
}