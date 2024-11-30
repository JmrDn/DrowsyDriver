package com.example.drowsydriver.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.drowsydriver.R;
import com.example.drowsydriver.adapter.DriverListAdapter;
import com.example.drowsydriver.model.DriverListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class AddDriverFragment extends Fragment {


    AppCompatButton addDriverBtn;
    RecyclerView recyclerView;
    DriverListAdapter myAdapter;
    ArrayList<DriverListModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_add_driver, container, false);
        initWidgets(view);
        setUpRecyclerView();

        addDriverBtn.setOnClickListener(v->showAddDriverDialog());
        return  view;
    }

    private void setUpRecyclerView() {
        list = new ArrayList<>();
        myAdapter = new DriverListAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);

        FirebaseFirestore.getInstance().collection("drivers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                list.clear();

                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                                    String fullName = queryDocumentSnapshot.getString("fullName");
                                    String plateNum = queryDocumentSnapshot.getString("plateNumber");
                                    String emergencyContact = queryDocumentSnapshot.getString("emergencyContact");
                                    String id = queryDocumentSnapshot.getString("id");

                                    list.add(new DriverListModel(fullName,plateNum,emergencyContact, id));
                                }

                                if(myAdapter != null){
                                    myAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.d("TAG", "Query is null");
                            }
                        } else {
                            Log.d("TAG", "Failed to fetch drivers");
                        }
                    }
                });
    }

    private void initWidgets(View view) {
        addDriverBtn = view.findViewById(R.id.addDriver_Button);
        recyclerView = view.findViewById(R.id.recyclerview);
    }

    private void showAddDriverDialog() {
        Dialog alertDialog = new Dialog(getContext());

        alertDialog.setCancelable(false);
        alertDialog.setContentView(R.layout.add_driver_layout);
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        alertDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg_with_radius));
        alertDialog.show();

        AppCompatButton confirmBtn = alertDialog.findViewById(R.id.confirm_button);
        AppCompatButton cancelBtn = alertDialog.findViewById(R.id.cancel_button);
        AppCompatEditText fullNameET = alertDialog.findViewById(R.id.fullname_Edittext);
        AppCompatEditText plateNumberET = alertDialog.findViewById(R.id.plateNumber_Edittext);
        AppCompatEditText emergencyContactET = alertDialog.findViewById(R.id.emergencyContact_Edittext);


        confirmBtn.setOnClickListener(v->{
            String fullName = fullNameET.getText().toString();
            String plateNumber = plateNumberET.getText().toString();
            String emergencyContact = emergencyContactET.getText().toString();

            if(fullName.isEmpty()){
                fullNameET.setError("Field is empty");
            } else if (plateNumber.isEmpty()){
                plateNumberET.setError("Field is empty");
            } else if (emergencyContact.isEmpty()){
                emergencyContactET.setError("Field is empty");
            } else {
                addDriver(fullName, plateNumber, emergencyContact, fullNameET, plateNumberET, emergencyContactET, alertDialog);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });
    }

    private void addDriver(String fullName, String plateNumber, String emergencyContact, AppCompatEditText fullNameET,
    AppCompatEditText plateNumberET,
    AppCompatEditText emergencyContactET, Dialog alertDialog) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = firestore.collection("drivers");

        DocumentReference documentReference = collectionReference.document();

        String documentId = documentReference.getId();

        HashMap<String, String>driversInfo = new HashMap<>();

        driversInfo.put("id", documentId);
        driversInfo.put("fullName", fullName);
        driversInfo.put("plateNumber", plateNumber);
        driversInfo.put("emergencyContact", emergencyContact);

        collectionReference.add(driversInfo)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            setUpRecyclerView();
                            fullNameET.setText("");
                            emergencyContactET.setText("");
                            plateNumberET.setText("");
                            Toast.makeText(getContext(), "Successfully added", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        } else{
                            Toast.makeText(getContext(), "Failed to add driver", Toast.LENGTH_LONG).show();
                            Log.d("TAG", "Failed to add driver", task.getException());
                        }

                    }
                });
    }

}