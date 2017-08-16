package ict.edu.rs.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }
    private static int id = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context,"Alarm!", Toast.LENGTH_SHORT).show();
        // objavljivanje notifikacije ova verzija konkretno podrzave ranije verzije andoida, ne pravimo objekat nego postoji fektori metoda  kojoj se prosedjuje kontekst
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // pravimo notifikacju
        Intent myIntent = new Intent(context, MainActivity.class);
        PendingIntent myPendingIntent = PendingIntent.getActivity(context, 0, myIntent, 0);
        Notification notification = new NotificationCompat.Builder(context) // srednja 3 parametra su obavezna, ovde se jos mogu dodati boja za led sijalicu ...
                .setTicker("Ticker")
                .setContentTitle("ContentTitle")
                .setContentText("ContentText")
                .setSmallIcon(R.drawable.fi01d)
                .addAction(R.drawable.fi01d,"Akcija 1",myPendingIntent)
                .setContentIntent(myPendingIntent).build();

        notificationManager.notify(id++, notification);
    }
}
