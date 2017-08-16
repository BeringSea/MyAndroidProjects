package ict.edu.rs.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BootCompletedReceiver extends BroadcastReceiver {
    public BootCompletedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            // provera kojom se omogucujemo da akcija bude pokrenuta samo sa ACTION_BOOT_COMPLETED a ne sa nekim drugim intentom
            //Toast.makeText(context,"Sistem je butovan!",Toast.LENGTH_LONG).show();
            // ovde ponovo setujemo tajmer za alarm kada se uredjaj butuje

            // i ovde moramo da dodamo proveru da li su notifikacije ukljucene ili iskljucene kako bi se u zavisnosti od toga sistem ponasao posle butovanja
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean notificationsEnabled = sharedPreferences.getBoolean("notifications", true);

            // u onCreate metodi realizujemo setovanje tajmera tj. inicializujemo AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            // sada ovaj inten "umotavamo" u PendingIntent cije smo polje naprvili na pocetku
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
            if(notificationsEnabled){
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 2000, 5000, alarmIntent);
            }
            // ne mora else deo
        }
    }
}
