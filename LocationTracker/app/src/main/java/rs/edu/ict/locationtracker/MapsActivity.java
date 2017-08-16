package rs.edu.ict.locationtracker;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline polyline;
    // pravimo listu u koju ce se upisivati tacke
    private ArrayList<LatLng> points = new ArrayList<LatLng>();
    private boolean stopTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    // pravimo metodu koja ce na koja ce prekinuti pracenje
    @Override
    protected void onPause() {
        super.onPause();
        this.stopTracking = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.stopTracking = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // ovde dodajemo poliliniju na mapu da bismo videli putanju
        PolylineOptions options = new PolylineOptions();
//        options.add(new LatLng(44,20));
//        options.add(new LatLng(40,20));
//        options.add(new LatLng(-20,-20));
        options.width(3);
        options.color(Color.RED);
        polyline = mMap.addPolyline(options);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(44, 20)));

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    while (!stopTracking) {
                        Thread.sleep(3000);
                        publishProgress();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                // ovde osvezavamo listu
                Toast.makeText(MapsActivity.this, "Test", Toast.LENGTH_SHORT).show();
                // izmestamo citanje u drugu metodu koju pozivamo na ovom mestu
                readPoints(); // moramo citanje izvrsiti u ovom tredu
                polyline.setPoints(points);
            }
        }.execute();
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void readPoints() {
        if (isExternalStorageReadable()) {
            File file = Environment.getExternalStoragePublicDirectory("");//dohvatamo root file
            file = new File(file, "LocationTracker");
            file.mkdir();
            file = new File(file, "track1.txt");
            FileInputStream out = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null; // pravimo omotac za InputStreamReader
            try {
                out = new FileInputStream(file); // ovde ne postoji true kao drugi paramater za append bez njega bi svaki novi upis brisao stari
                reader = new InputStreamReader(out);
                bufferedReader = new BufferedReader(reader);
                String s;
                points.clear();
                while ((s = bufferedReader.readLine()) != null) {
                    // u javi za parsiranje koristi se metoda split koja kao parametar ocekuje reg. izraz
                    String[] data = s.split(", ");
                    //data.toString();
                    // parsiramo rezultat
                    double lat = Double.parseDouble(data[0]);
                    double lng = Double.parseDouble(data[1]);
                    points.add(new LatLng(lat,lng));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}