package com.example.drowsydriver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.drowsydriver.fragments.AboutFragment;
import com.example.drowsydriver.fragments.AddDriverFragment;
import com.example.drowsydriver.fragments.AlertHistoryFragment;
import com.example.drowsydriver.fragments.GuideAppFragment;
import com.example.drowsydriver.fragments.HomeFragment;
import com.example.drowsydriver.model.DriverListModel;
import com.example.drowsydriver.utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private final  String value = "Home";
    Ringtone ringtone;
    DriverListModel currentDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initWidgets();
        setUpCurrentDriver();
        setUpDrawer();
        setUpDefaultNavigation();
        setUpAlarm();
        createNotification();


    }

    private void setUpCurrentDriver() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("currentlyMonitoring");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String fullname = snapshot.child("fullName").getValue().toString();
                    String id = snapshot.child("id").getValue().toString();
                    String  plateNum = snapshot.child("plateNum").getValue().toString();
                    currentDriver = new DriverListModel(fullname,plateNum, "sa", id);


                } else {
                    Log.d("TAG", "Failed to fetch currently monitoring");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "Failed to fetch currently monitoring");
            }
        });

    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("Drowsy",
                    "Drowsy", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 1000, 200, 340});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    private void getNotify() {
        String alertMessage = "Driver is Drowsy";
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Drowsy")
                .setContentTitle("Drowsy Driver")
                .setSmallIcon(R.drawable.drowsy_driver_logo_ic)
                .setAutoCancel(true)
                .setContentText(alertMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(0, builder.build());
    }
    private void playRingtone() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, soundUri);
        ringtone.play();
        getNotify();
    }

    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void setUpAlarm() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("drowsy");


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean isDrowsy = (boolean) snapshot.child("isDrowsy").getValue();

                    if(isDrowsy){
                        playRingtone();
                        setDriverAlertLog();
                    } else {

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


    private void setDriverAlertLog() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("alertHistory");

        String key = db.push().getKey();

        HashMap<String, Object> driverLog = new HashMap<>();
        driverLog.put("id", currentDriver.getId());
        driverLog.put("plateNum", currentDriver.getPlateNum());
        driverLog.put("fullName", currentDriver.getFullName());
        driverLog.put("date", DateAndTimeUtils.getDateWithWordFormat());
        driverLog.put("time", DateAndTimeUtils.getTimeWithAMAndPM());
        driverLog.put("timeStamp", Timestamp.now());
        driverLog.put("key", key);


        db.child(key).setValue(driverLog).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("TAG", "Drowsiness of driver successfully saved");
                } else {
                    Log.d("TAG", "Drowsiness of driver failed to save");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void setUpDefaultNavigation() {
        Fragment selectedFragment = null;
        if (value.equals("Home")){

            selectedFragment = new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
    }
    private void setUpDrawer() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void initWidgets() {
        drawerLayout = findViewById(R.id.HomePage_Drawer);
        navigationView = findViewById(R.id.HomePage_Nav_View);
        toolbar = findViewById(R.id.toolbar);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        int itemId = item.getItemId();


        Menu menu = navigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false);
        }
        item.setChecked(true);

        if (itemId == R.id.home){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new HomeFragment();
        }
        else if (itemId == R.id.about){

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new AboutFragment();
        }
        else if (itemId == R.id.guideApp){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new GuideAppFragment();
        }
        else if (itemId == R.id.addDriver){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new AddDriverFragment();
        }
        else if (itemId == R.id.alertHistory){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    drawerLayout.closeDrawer(GravityCompat.START);

                }
            }, 300);

            selectedFragment = new AlertHistoryFragment();
        }




        if (selectedFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).addToBackStack(null).commit();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone();
    }
}