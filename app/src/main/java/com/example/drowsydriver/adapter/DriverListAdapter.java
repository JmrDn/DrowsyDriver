package com.example.drowsydriver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drowsydriver.R;
import com.example.drowsydriver.model.DriverListModel;

import java.util.ArrayList;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.MyViewHolder> {
    Context context;
    ArrayList<DriverListModel> list;

    public DriverListAdapter(Context context, ArrayList<DriverListModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public DriverListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drivers_list_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverListAdapter.MyViewHolder holder, int position) {
        DriverListModel model = list.get(position);
        holder.fullName.setText(model.getFullName());
        holder.plateNum.setText(model.getPlateNum());
        holder.emergencyContact.setText(model.getEmergencyContact());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullName,
                plateNum,
                emergencyContact;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName_Textview);
            plateNum = itemView.findViewById(R.id.plateNumber_Textview);
            emergencyContact = itemView.findViewById(R.id.emergencyContact_Textview);
        }
    }
}