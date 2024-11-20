package com.example.drowsydriver.fragments;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.drowsydriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {

    ImageView safeCircle;
    LottieAnimationView drowsyCircle;
    TextView message;
    Ringtone ringtone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        initWidgets(view);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("drowsy");
        drowsyCircle.setVisibility(View.GONE);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean isDrowsy = (boolean) snapshot.child("isDrowsy").getValue();

                    if(isDrowsy){
                        drowsyCircle.setVisibility(View.VISIBLE);
                        safeCircle.setVisibility(View.GONE);
                        message.setText(R.string.driverIsDrowsy);
                        playRingtone();
                    } else {
                        drowsyCircle.setVisibility(View.GONE);
                        safeCircle.setVisibility(View.VISIBLE);
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
        return view;
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
        drowsyCircle = view.findViewById(R.id.drowsyCircle_LottieView);
        safeCircle = view.findViewById(R.id.safeCircle_ImageView);
        message = view.findViewById(R.id.text);
    }
}