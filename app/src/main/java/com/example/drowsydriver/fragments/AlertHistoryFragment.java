package com.example.drowsydriver.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drowsydriver.R;
import com.example.drowsydriver.adapter.AlertListAdapter;
import com.example.drowsydriver.model.AlertListModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class AlertHistoryFragment extends Fragment {

    RecyclerView recyclerView;
    AlertListAdapter adapter;
    ArrayList<AlertListModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_alert_history, container, false);

        initWidgets(view);
        setUpRecyclerView();
        return  view;
    }

    private void setUpRecyclerView() {
        list = new ArrayList<>();
        adapter = new AlertListAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("alertHistory");

// Query to fetch data sorted by timestamp
        Query query = databaseReference.orderByChild("timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String fullName = dataSnapshot.child("fullName").getValue().toString();
                        String id = dataSnapshot.child("id").getValue().toString();
                        String plateNum  = dataSnapshot.child("plateNum").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
                        String time = dataSnapshot.child("time").getValue().toString();

                        list.add(new AlertListModel(fullName,date + ", " + time, plateNum, id));
                    }
                    Collections.reverse(list);
                    if(adapter != null){
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("TAG", "Alert history does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch Alert history");
            }
        });
    }

    private void initWidgets(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
    }
}