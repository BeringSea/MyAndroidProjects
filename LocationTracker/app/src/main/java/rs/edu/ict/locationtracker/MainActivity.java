package rs.edu.ict.locationtracker;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    // pravimo kontrolno polje koje je u javi podrazumevano false
    private boolean googleConnected;
    private TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txtLocation = (TextView) this.findViewById(R.id.txtLocation);// dohvatamo tekstulano polje iz content_main
        // ovaj kod smo kopirali sa https://developer.android.com/training/location/retrieve-current.html
        // da bi servis radio u build.gradle morali smo da dodamo - compile 'com.google.android.gms:play-services:8.4.0'
        // promenljivu mGoogleApiClient deklarisali smo van metode zato sto cemo je koristiti na vise mesta

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    // moramo obezbediti da se klijent konektuje i diskonektuje to radimo sa metodama onStart i onStop
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnGetLocation_Click(View view) {
        // za ove metode moramo implementirati odredjene dozvole
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient); // greske se posle dodavanja u manifest fajlu resevaju sa alt + enter dodaje se provera za dohvatanje lokacija u run time, tj dok aplikacija radi

        txtLocation.setText(location.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        googleConnected = true;
        // ovde treba da registrujemo metodu tj listener koji ce da reaguje na LocationServices dogadjaj
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder settingsRequest = new LocationSettingsRequest.Builder();
        settingsRequest.addLocationRequest(request);

        // posto metoda duze traje koristimo PendingResult kome prosledjujemo tip koji ocekujemo
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, settingsRequest.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                // ovde pitamo korisnika da ukljuci neka podesavanja
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this,0);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // kod ispod je dobar u situaciji kada sve desava dok je ziv aktiviti, tj kada ne zelimo da apk radi posle zatvaranja aktivitija

//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                txtLocation.setText(location.toString());
//            }
//        });

        // ovde LocationServices - u saljemo PendingIntent

        Intent intent = new Intent(this,LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent,0);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, pendingIntent);
        // metoda koja bi se implemetrirala na dugme stop ukoliko bismo imali dugmice za startovanje i stopiranje servisa
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        googleConnected = false;
    }


    // moramo obezbediti da se klijent konektuje i diskonektuje to radimo sa metodama onStart i onStop
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void btnShowMap_Click(View view) {
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
}
