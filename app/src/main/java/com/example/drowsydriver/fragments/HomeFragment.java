package com.example.drowsydriver.fragments;

import android.app.Dialog;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.drowsydriver.R;
import com.example.drowsydriver.model.DriverListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    TextView message;
    Ringtone ringtone;
    RelativeLayout safeLayout;
    RelativeLayout drowsyLayout;
    AppCompatButton setMonitoringBtn;
    TextView fullNameTV;
    TextView plateNumTV;
    private String fullName = "";
    private String driverId = "";
    private String plateNum = "";
    ArrayList<DriverListModel> item;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        initWidgets(view);
        setUpItem();
        setCurrentlyMonitoring();
        setUpDrowsy();


        setMonitoringBtn.setOnClickListener(v-> showSetMonitoringDialog());
        return view;
    }

    private void setCurrentlyMonitoring() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("currentlyMonitoring");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String fullName =  snapshot.child("fullName").getValue().toString();
                    String plateNum =  snapshot.child("plateNum").getValue().toString();

                    fullNameTV.setText(fullName);
                    plateNumTV.setText(plateNum);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch currently monitoring");

            }
        });
    }

    private void setUpDrowsy() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("drowsy");

        drowsyLayout.setVisibility(View.GONE);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean isDrowsy = (boolean) snapshot.child("isDrowsy").getValue();

                    if(isDrowsy){
                        drowsyLayout.setVisibility(View.VISIBLE);
                        safeLayout.setVisibility(View.GONE);
                        message.setText(R.string.driverIsDrowsy);
                        playRingtone();
                    } else {
                        drowsyLayout.setVisibility(View.GONE);
                        safeLayout.setVisibility(View.VISIBLE);
                        message.setText(R.string.driverIsSafe);
                        stopRingtone();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetchh db" + error.getMessage());
            }
        });
    }

    private void setUpItem() {
        item = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("drivers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                item.clear();

                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                                    String fullName = queryDocumentSnapshot.getString("fullName");
                                    String plateNum = queryDocumentSnapshot.getString("plateNumber");
                                    String emergencyContact = queryDocumentSnapshot.getString("emergencyContact");
                                    String id = queryDocumentSnapshot.getString("id");
                                   item.add(new DriverListModel(fullName, plateNum, emergencyContact,id));
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

    private void showSetMonitoringDialog() {
        Dialog setUpMonitoringDialog = new Dialog(getContext());

        setUpMonitoringDialog.setCancelable(false);
        setUpMonitoringDialog.setContentView(R.layout.setup_monitoring_dialog);
        setUpMonitoringDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        setUpMonitoringDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg_with_radius));
        setUpMonitoringDialog.show();

        AppCompatButton confirmBtn = setUpMonitoringDialog.findViewById(R.id.confirm_button);
        AppCompatButton cancelBtn = setUpMonitoringDialog.findViewById(R.id.cancel_button);
        AutoCompleteTextView selectDriverATV = setUpMonitoringDialog.findViewById(R.id.autocompleteTxt);
        ArrayAdapter<DriverListModel> arrayAdapter = new ArrayAdapter<DriverListModel>(
                getContext(),
                R.layout.drivers_list,
                item
        ) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.drivers_list, parent, false);
                }

                DriverListModel driver = getItem(position);

                TextView nameView = convertView.findViewById(R.id.textView);


                nameView.setText(driver.getFullName());

                return convertView;
            }
        };
        selectDriverATV.setAdapter(arrayAdapter);
        selectDriverATV.setAdapter(arrayAdapter);


        selectDriverATV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DriverListModel selectedDriver = (DriverListModel) parent.getItemAtPosition(position);
                String selectedDriverId = selectedDriver.getId(); // Get the driver's ID
                String selectedDriverName = selectedDriver.getFullName(); // Get the driver's name
                String selectedPlateNum = selectedDriver.getPlateNum();

                fullName = selectedDriverName;
                driverId = selectedDriverId;
                plateNum = selectedPlateNum;
            
            }
        });
        confirmBtn.setOnClickListener(v-> setUpDriverMonitoring(setUpMonitoringDialog, selectDriverATV));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUpMonitoringDialog.dismiss();

            }
        });
    }

    private void setUpDriverMonitoring(Dialog setUpMonitoringDialog, AutoCompleteTextView selectDriverATV) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("currentlyMonitoring");

        if(!driverId.isEmpty()){
            db.child("fullName").setValue(fullName);
            db.child("id").setValue(driverId);
            db.child("plateNum").setValue(plateNum);
            Toast.makeText(getContext(), "Succesfully set", Toast.LENGTH_LONG).show();
            setUpMonitoringDialog.dismiss();
            driverId = "";
            fullName = "";
            plateNum = "";
        }
    }

    private void playRingtone() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getContext(), soundUri);
        ringtone.play();
    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void initWidgets(View view) {

        setMonitoringBtn = view.findViewById(R.id.setMonitoring_Button);
        message = view.findViewById(R.id.text);
        safeLayout = view.findViewById(R.id.safeLayout);
        drowsyLayout = view.findViewById(R.id.drowsyLayout);
        plateNumTV = view.findViewById(R.id.plateNumber_Textview);
        fullNameTV = view.findViewById(R.id.fullName_Textview);
    }
}