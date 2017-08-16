package ict.edu.rs.weather;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by David on 2016-11-23.
 */

public class DatabaseCaching extends DataCaching {

    public DatabaseCaching(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Forecast> readForecast(long locationId) throws Exception {
        DbWeather dbWeather = new DbWeather(this.getContext()); // dohvatamo pretected clan iz klase DataCaching
        try{
            dbWeather.open();
            return dbWeather.readForecast(locationId);
        }
        finally {
            dbWeather.close();
        }

    }

    @Override
    public void writeForecast(long locationId, ArrayList<Forecast> forecasts) throws Exception {
        DbWeather dbWeather = new DbWeather(this.getContext());
        try{
            dbWeather.open();
            dbWeather.writeForecast(locationId,forecasts);
        }
        finally {
            dbWeather.close();
        }

    }
}
