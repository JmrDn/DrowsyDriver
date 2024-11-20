package com.example.drowsydriver.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtil {
    Context context;
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public static final String PREF_NAME = "user_Details";

    // Save data in SharedPreferences
    public StorageUtil(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void  setFirstVisit(boolean isFirstVisit){
        editor.putBoolean("isFirstVisit", isFirstVisit);
        editor.apply();
    }

    public boolean getIsFirstVisit(){
        return sharedPreferences.getBoolean("isFirstVisit", true);
    }
}
