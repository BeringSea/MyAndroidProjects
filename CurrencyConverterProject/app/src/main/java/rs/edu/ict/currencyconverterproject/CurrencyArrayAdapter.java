package rs.edu.ict.currencyconverterproject;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by David on 2017-01-24.
 */

public class CurrencyArrayAdapter extends ArrayAdapter<Currency> {
    private MainActivity mainActivity;
    private final ArrayList<Currency> currencies;
    boolean isSpinner;



//    private double kolicnik = 1;
//    public void setKolicnik(double kolicnik) {
//        this.kolicnik = kolicnik;
//    }

    public CurrencyArrayAdapter(MainActivity mainActivity, ArrayList<Currency> currencies, boolean isSpinner) {
        super(mainActivity, R.layout.currency_item, currencies);
        this.mainActivity = mainActivity;
        this.currencies = currencies;
        this.isSpinner = isSpinner;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mainActivity.getLayoutInflater().inflate(R.layout.currency_item, null);
        }


        ImageView imgFlag = (ImageView) convertView.findViewById(R.id.imgFlag);
        TextView txtCurrencyName = (TextView) convertView.findViewById(R.id.txtCurrencyName);
        TextView txtCurrencyValue = (TextView) convertView.findViewById(R.id.txtCurrencyValue);


        Currency currency = currencies.get(position);

        Picasso.with(mainActivity).load(String.format("http://caches.space/flags/%s.png",currency.getCountryCode().toLowerCase())).into(imgFlag);
        txtCurrencyName.setText(currency.getCurrencyName());
//        txtCurrencyValue.setText(String.format("%.2f",currency.getCurrencyValue() / kolicnik));
        if(isSpinner){
            txtCurrencyValue.setVisibility(View.GONE);
        }
        else{
            txtCurrencyValue.setText(String.format("%.2f",currency.getCurrencyValue()));
        }

        return convertView;
    }

}
