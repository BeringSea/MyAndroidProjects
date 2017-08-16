package ict.edu.rs.weather;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by David on 2016-11-23.
 */

public abstract class DataCaching {

    private Context context; // privatno poljene moze da se koristi u izvedenim klasam zato smo napravli geter i umesto public stavljamo da je protected na taj nacin niko spolja nece moci da pristupi polju osim izvedenih klasa

    protected Context getContext() {
        return context;
    }

    public DataCaching(Context context){
        this.context = context;
    }
    public abstract ArrayList<Forecast> readForecast(long locationId) throws Exception; // deklarisemo da ova metoda moze da baca razne izuzetke ali ih ne obradjujemo
    public abstract void writeForecast(long locationId,ArrayList<Forecast> forecasts) throws Exception;
}
