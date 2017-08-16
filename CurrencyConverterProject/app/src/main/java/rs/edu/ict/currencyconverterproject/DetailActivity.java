package rs.edu.ict.currencyconverterproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String MESSAGE = "message";
    private MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Currency currency = (Currency) intent.getSerializableExtra(MESSAGE);
        ImageView flagImage = (ImageView)findViewById(R.id.imgFlagDetails);
        Picasso.with(this).load(String.format("http://caches.space/flags/%s.png",currency.getCountryCode().toLowerCase())).into(flagImage);
        TextView countryName = (TextView)findViewById(R.id.txtNameDetails);
        countryName.setText(currency.getCountryName());
        TextView countryValue = (TextView)findViewById(R.id.txtValueDetails);
        countryValue.setText(String.format("%.4f",currency.getCurrencyValue()));
        TextView countryCode = (TextView)findViewById(R.id.txtCodeDetails);
        countryCode.setText(currency.getCountryCode());
    }
}
