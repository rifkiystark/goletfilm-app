package com.rifkiystark.goletfilm;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref{

    private static final String KEY_REMINDER = "key_reminder";
    private static final String SP_REMINDER = "sp_reminder";


    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPref(Context context){
        sp = context.getSharedPreferences(SP_REMINDER, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void editReminnder(boolean value){
        spEditor.putBoolean(KEY_REMINDER, value);
        spEditor.commit();
    }

    public Boolean reminderActivated(){
        return sp.getBoolean(KEY_REMINDER, true);
    }
}
