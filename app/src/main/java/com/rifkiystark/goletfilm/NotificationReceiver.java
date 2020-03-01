package com.rifkiystark.goletfilm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.rifkiystark.goletfilm.activity.MainActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

import static android.support.constraint.Constraints.TAG;

public class NotificationReceiver extends BroadcastReceiver {

    private static String EXTRA_TYPE = "1";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getStringExtra(EXTRA_TYPE).equals("reminder")){
            showNotification(context, "I'm Missing You :(", "Daily Reminder");
            Log.d(TAG, "onReceive: Reminder");
        }else{
            getData(context);
        }
    }

    public void setReminder(Context context){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(EXTRA_TYPE,"reminder");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        int requestCode = 2;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        Log.d(TAG, "setReminder: Activated");
    }

    public void setReleaseDate(Context context){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(EXTRA_TYPE,"release");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        int requestCode = 1;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        Log.d(TAG, "setReleaseDate: Activated");
    }

    void showNotification(Context context, String messageBody, String title){
        String channelId = "channelId";
        String channelName = "channelName";
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(messageBody)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);

            notificationBuilder.setChannelId(channelId);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = notificationBuilder.build();

        if (mNotificationManager != null) {
            mNotificationManager.notify(0, notification);
        }
    }

    public void getData(final Context context) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat waktuformat = new SimpleDateFormat("dd");
        final String waktu = waktuformat.format(calendar.getTime());
        String API = BuildConfig.TMDB_API_KEY;
        String url = "https://api.themoviedb.org/3/movie/upcoming?api_key="+API+"&language=en-US";

        AsyncHttpClient get_films = new AsyncHttpClient();
        get_films.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                int jml_data;
                try{
                    JSONObject responseObject = new JSONObject(result);
                    //Log.d(TAG, "onSuccess: " + result);
                    jml_data = responseObject.getJSONArray("results").length();
                    //Log.d(TAG, "onSuccess: " + jml_data);
                    if(jml_data > 0){
                        for (int i = 0;i < jml_data;i++){
                            String title = responseObject.getJSONArray("results").getJSONObject(i).getString("original_title");
                            String release = responseObject.getJSONArray("results").getJSONObject(i).getString("release_date");

                            if(!release.equals("")){
                                String[] spliter = release.split("-");
                                Log.d(TAG, "onSuccess: " + spliter[2] + " " + waktu);
                                if(spliter[2].equals(waktu)){
                                    showNotification(context,title, "Released Today");
                                }
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public void cancelAlarm(Context context, int type) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, type, intent, 0);
        pendingIntent.cancel();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
