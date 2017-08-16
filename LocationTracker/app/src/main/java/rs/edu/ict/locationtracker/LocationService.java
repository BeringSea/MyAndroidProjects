package rs.edu.ict.locationtracker;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by David on 2016-11-29.
 */

public class LocationService extends IntentService {
//Servisi moraju da se prijave kroz manifest fajl

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocationService() {
        super("LocationService");
    }

    // upis u eksterni storage
    // metoda koja proverava da li je eksterni storage dostupan
    // kopirano sa https://developer.android.com/training/basics/data-storage/files.html

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // u ovoj niti ne mozemo da korsitmo gui zato korsitimo log strukturu da bismo proverii da li nam servis radi
        //Log.v("Location", "Test");
        // dohvatanje podataka iz lokacije
        LocationResult locationResult = LocationResult.extractResult(intent);
        if (locationResult != null) {
            Location location = locationResult.getLastLocation();
            if(location != null && isExternalStorageWritable()){
                File file = Environment.getExternalStoragePublicDirectory("");//dohvatamo root file
                file = new File(file,"LocationTracker");
                file.mkdir();
                file = new File(file,"track1.txt");
                FileOutputStream out = null;
                OutputStreamWriter writer = null;
                try {
                    out = new FileOutputStream(file,true); // true je drugi paramater za append bez njega bi svaki novi upis brisao stari
                    writer = new OutputStreamWriter(out);
                    writer.write(String.format("%f, %f\n",location.getLatitude(),location.getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if(writer != null){
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.v("Location", location.toString()); // ostavili smo logovanje kao vid provere
            }
        }
    }
}

