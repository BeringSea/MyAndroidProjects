package ict.edu.rs.weather;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    public static final String OWM_LIST = "list";
    public static final String CITY = "city";
    public static final String CITY_ID = "id";
    private ListView lwForecasts;
    private ArrayAdapter<Forecast> forecastsAdapter;
    private AboutDialog aboutDialog;
    private final ArrayList<Forecast> forecasts = new ArrayList<Forecast>();

    private DataCaching dataCaching;

    // pravimo referecu za AlamManager kao polje klase zato sto ce nam trebati i van metode onCreate
    private AlarmManager alarmManager;

    // i drugu referencu za PendingIntent pravimo kao polje klase
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aboutDialog = new AboutDialog(this);

        lwForecasts = (ListView) findViewById(R.id.lwForecasts);

        forecastsAdapter = new ForecastArrayAdapter(this, forecasts);

        lwForecasts.setAdapter(forecastsAdapter);

        lwForecasts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,
                        forecasts.get(position).toString(),
                        Toast.LENGTH_LONG).show();
            }
        });

        dataCaching = new DatabaseCaching(this); // mesto odluke na kome se odlucuje da li ce se raditi kesiranje u fajl ili u bazu, mi radimo sad sa bazom, ukoliko zelimo da radimo sa fajlom samo promenimo DatabaseCaching u FileCaching

        // dodajemo deo da li su notifikacije ukljucene ili iskljucene
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean notificationEnabled = sharedPreferences.getBoolean("notifications",true);

        // u onCreate metodi realizujemo setovanje tajmera tj. inicializujemo AlarmManager
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        // sada ovaj inten "umotavamo" u PendingIntent cije smo polje naprvili na pocetku
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if(notificationEnabled){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 2000, 5000, alarmIntent);
        }
        else{
            alarmManager.cancel(alarmIntent);
        }

        // zaustavljanje alarma, pravi se novi objekat ali istog sadrzaja kao prethodni, ovo prakthicno znaci da cancel mozemo da uradimo na nekom drugom mestu nap. u nekom drugom activity - ju
//        intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarmManager.cancel(alarmIntent);

        updateForecasts();
    }

    private void updateForecasts() {
        final ProgressDialog progressDialog = ProgressDialog.show(
                this, "Updating Forecasts", "Updating forecasts...",
                true, false
        );
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        double latitude = preferences.getFloat("LocationLatitude", 0);
        double longitude = preferences.getFloat("LocationLongitude", 0);
        String unit = preferences.getString("units_list", "c");
        new AsyncTask<Double, Void, Void>() {
            @Override
            protected Void doInBackground(Double... params) {
                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;
                try {
                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.encodedPath("http://api.openweathermap.org/data/2.5/forecast/daily");
                    //uriBuilder.appendQueryParameter("q", "Belgrade");
                    uriBuilder.appendQueryParameter("lat", params[0].toString());
                    uriBuilder.appendQueryParameter("lon", params[1].toString());
                    uriBuilder.appendQueryParameter("mode", "json");
                    uriBuilder.appendQueryParameter("units", "metric");
                    uriBuilder.appendQueryParameter("cnt", "10");
                    uriBuilder.appendQueryParameter("APPID", "25c40537bb071fff8c2efbde9148487c");
                    URL url = new URL(uriBuilder.build().toString());

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null)
                        sb.append(line + "\n");
                    Log.v("JSON", sb.toString());
                    forecasts.addAll(parseJsonForecasts(sb.toString()));

                    if (forecasts.size() > 0) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        long cityid = preferences.getLong(SettingsActivity.CITY_ID, -1);
                        if (cityid != -1) {
                            dataCaching.writeForecast(cityid, forecasts);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                    // citanje podadaka iz kes fajla radi se samo kad se udje u catch blok ondosno u slucaju kada nama konekcije
                    try {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        long cityId = preferences.getLong(SettingsActivity.CITY_ID, -1);
                        if (cityId != -1) {
                            forecasts.clear();
                            ArrayList<Forecast> newForecasts = dataCaching.readForecast(cityId);
                            forecasts.addAll(newForecasts);
                        }
                    }
                    catch (Exception ex) {

                    }
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                    if (bufferedReader != null)
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            Log.e("Exception", e.toString());
                        }
                    progressDialog.dismiss();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                forecastsAdapter.notifyDataSetChanged();
            }
        }.execute(latitude, longitude);

    }

    private ArrayList<Forecast> parseJsonForecasts(String s) throws JSONException {
        JSONObject jsonRoot = new JSONObject(s);
        JSONObject jsonCity = jsonRoot.getJSONObject(CITY);
        long cityId = jsonCity.getLong(CITY_ID);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SettingsActivity.CITY_ID, cityId);
        editor.commit();
        JSONArray jsonForecasts = jsonRoot.getJSONArray(OWM_LIST);
        ArrayList<Forecast> forecasts = new ArrayList<Forecast>();
        for (int i = 0; i < jsonForecasts.length(); i++) {
            JSONObject jsonForecast = jsonForecasts.getJSONObject(i);
            Forecast forecast = new Forecast();

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(jsonForecast.getLong("dt") * 1000);
            String date = DateFormat.format("dd-MM", cal).toString();
            forecast.setDate(date);

            JSONObject jsonTemp = jsonForecast.getJSONObject("temp");
            forecast.setHigh(jsonTemp.getDouble("max"));
            forecast.setLow(jsonTemp.getDouble("min"));
            JSONObject jsonWeather = jsonForecast.getJSONArray("weather").getJSONObject(0);
            forecast.setDescription(jsonWeather.getString("main"));
            forecast.setIconId(jsonWeather.getString("icon"));
            forecasts.add(forecast);
        }
        return forecasts;
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            this.aboutDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
