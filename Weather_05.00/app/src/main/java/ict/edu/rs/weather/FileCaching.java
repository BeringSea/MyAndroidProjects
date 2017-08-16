package ict.edu.rs.weather;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by David on 2016-11-23.
 */

public class FileCaching extends DataCaching {

    public FileCaching(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Forecast> readForecast(long locationId) throws Exception {
        File file = new File(this.getContext().getCacheDir(),
                Long.toString(locationId) + ".tmp");
        ArrayList<Forecast> forecasts = new ArrayList<Forecast>();
        if (file.exists()) {
            FileInputStream stream = new FileInputStream(file);
            ForecastsFormatter formatter = new ForecastsFormatter();
            forecasts = formatter.Read(stream);
        }
        return forecasts;
    }

    @Override
    public void writeForecast(long locationId, ArrayList<Forecast> forecasts) throws Exception {
        File file = new File(this.getContext().getCacheDir(),
                Long.toString(locationId) + ".tmp");
        // pravljenje strima za upis
        FileOutputStream stream = new FileOutputStream(file);
        // upis i citanje iz fajla radimo preko klase ForecastsFormatter
        ForecastsFormatter forecastsFormatter = new ForecastsFormatter();
        forecastsFormatter.Write(stream, forecasts);
        stream.close();
    }
}
