package ict.edu.rs.weather;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by m.spasojevic on 4/27/2016.
 */
class ForecastArrayAdapter extends ArrayAdapter<Forecast> {
    private MainActivity mainActivity;
    private final ArrayList<Forecast> forecasts;

    public ForecastArrayAdapter(MainActivity mainActivity, ArrayList<Forecast> forecasts) {
        super(mainActivity, R.layout.forecast_item, forecasts);
        this.mainActivity = mainActivity;
        this.forecasts = forecasts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mainActivity.getLayoutInflater().inflate(R.layout.forecast_item, null);
        }

        TextView txtForecastDate = (TextView) convertView.findViewById(R.id.txtForecastDate);
        TextView txtForecastDescription = (TextView) convertView.findViewById(R.id.txtForecastDescription);
        TextView txtForecastHigh = (TextView) convertView.findViewById(R.id.txtForecastHigh);
        TextView txtForecastLow = (TextView) convertView.findViewById(R.id.txtForecastLow);
        ImageView imgForecastIcon = (ImageView) convertView.findViewById(R.id.imgForecastIcon);

        Forecast forecast = forecasts.get(position);

        txtForecastDate.setText(forecast.getDate());
        txtForecastDescription.setText(forecast.getDescription());
        txtForecastHigh.setText(Double.toString(forecast.getHigh()));
        txtForecastLow.setText(Double.toString(forecast.getLow()));
        imgForecastIcon.setImageResource(getImageResourceId(forecast.getIconId()));

        return convertView;
    }

    private int getImageResourceId(String iconId) {
        switch (iconId) {
            case "01d":
                return R.drawable.fi01d;
            case "02d":
                return R.drawable.fi02d;
            case "03d":
                return R.drawable.fi03d;
            case "04d":
                return R.drawable.fi04d;
            case "09d":
                return R.drawable.fi09d;
            case "10d":
                return R.drawable.fi10d;
            case "11d":
                return R.drawable.fi11d;
            case "13d":
                return R.drawable.fi13d;
            case "50d":
                return R.drawable.fi50d;
        }
        return  R.drawable.fi01d;
    }
}
