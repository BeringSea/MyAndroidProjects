package rs.edu.ict.currencyconverterproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Currency> currencies = new ArrayList<Currency>();
//    private ArrayList<Currency> currenciesSpinner = new ArrayList<Currency>();
    private ArrayAdapter<Currency> currencyAdapter;
    private ArrayAdapter<Currency> currencySpinnerAdapter;

    private ListView lwCurrencies;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Spinner spinnerAll;
    private EditText edtValue;
    private TextView txtResult;
    private double kolicnik;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        currencyAdapter = new CurrencyArrayAdapter(this, currencies,false);
        currencySpinnerAdapter = new CurrencyArrayAdapter(this, currencies, true);

        edtValue = (EditText) findViewById(R.id.edtValue);
        txtResult = (TextView) findViewById(R.id.txtResult);
        lwCurrencies = (ListView) findViewById(R.id.lwCurrencies);
        spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        spinnerTo = (Spinner) findViewById(R.id.spinnerTo);
        spinnerAll = (Spinner) findViewById(R.id.spinnerAll);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        lwCurrencies.setAdapter(currencyAdapter);
        spinnerFrom.setAdapter(currencySpinnerAdapter);
        spinnerTo.setAdapter(currencySpinnerAdapter);
        spinnerAll.setAdapter(currencySpinnerAdapter);

        lwCurrencies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Currency currency = currencies.get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.MESSAGE,currency);
                startActivity(intent);
            }
        });




//        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                Currency currency = currencies.get(pos);
//                Toast.makeText(MainActivity.this,currency.toString(),Toast.LENGTH_SHORT).show();
//                edtValue = (EditText) findViewById(R.id.edtValue);
//                String prenos = Double.toString(currency.getCurrencyValue());
//                edtValue.setText(prenos);
//            }
//
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        spinnerAll.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Currency currency = currencies.get(pos);
                kolicnik = currency.getCurrencyValue();
                for(Currency currencyForeach : currencies){
                    double currencyValue = currencyForeach.getCurrencyValue();
                    currencyForeach.setCurrencyValue(currencyValue / kolicnik);
                }
//                ((CurrencyArrayAdapter)currencyAdapter).setKolicnik(kolicnik);
                currencyAdapter.notifyDataSetChanged();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        updateCurrency();
    }

    private void updateCurrency() {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                HttpURLConnection httpURLConnection = null;
                try {
                    Uri.Builder uriBuilder = new Uri.Builder();
                    uriBuilder.encodedPath("http://www.mycurrency.net/service/rates");
//                    uriBuilder.encodedPath("https://openexchangerates.org/api/latest.json");
//                    uriBuilder.appendQueryParameter("app_id","7ef8b56ecc9b4fdeaf49609ed4a8fd41");

                    URL url = new URL(uriBuilder.build().toString());
                    //URL url = new URL("http://www.mycurrency.net/service/rates");

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    Log.i("JSON", sb.toString());
                    currencies.clear();
                    currencies.addAll(parseJsonCurrency(sb.toString()));
                } catch (Exception e) {
                    Log.e("HttpError", e.toString());
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                currencyAdapter.notifyDataSetChanged();
                currencySpinnerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private ArrayList<Currency> parseJsonCurrency(String s) throws JSONException {
        ArrayList<Currency> currencies = new ArrayList<Currency>();
        //JSONObject jsonRoot = new JSONObject(s);
        //JSONObject jsonCurrency = jsonRoot.getJSONObject("rates");
        JSONArray jsonCurrencies = new JSONArray(s);
        //Iterator<String> strings = jsonCurrency.keys();
//        while (strings.hasNext()) {
//            String stringCurrency = strings.next();
//            String valueStr = jsonCurrency.getString(stringCurrency);
//
//            if ( jsonCurrency.get(valueStr) instanceof JSONObject ) {
//                Currency currency = new Currency();
//                currency.setCurrencyName(valueStr);
//                currencies.add(currency);
//            }
//        }


        for (int i = 0; i < jsonCurrencies.length(); i++) {

            JSONObject jsonCurrency = jsonCurrencies.getJSONObject(i);
            Currency currency = new Currency();
            currency.setCountryCode(jsonCurrency.getString("code"));
            currency.setCurrencyName(jsonCurrency.getString("currency_code"));
            currency.setCurrencyValue(jsonCurrency.getDouble("rate"));
            currency.setCountryName(jsonCurrency.getString("name"));
            currencies.add(currency);
//            currenciesSpinner.add(currency);
        }
        return currencies;
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

    public void btnConvertClick(View view) {
        double edtValueText = Double.valueOf(edtValue.getText().toString());
        Currency valueFromObj = (Currency) spinnerFrom.getSelectedItem();
        double valueFrom = valueFromObj.getCurrencyValue();
        Currency valueToObj = (Currency) spinnerTo.getSelectedItem();
        double valueTo = valueToObj.getCurrencyValue();

        double result = edtValueText * valueFrom / valueTo;

        txtResult.setText(String.format("%.2f",result));
    }

    public void btnResetClick(View view) {
        edtValue.setText("1");
        txtResult.setText("0.00");
    }
}
