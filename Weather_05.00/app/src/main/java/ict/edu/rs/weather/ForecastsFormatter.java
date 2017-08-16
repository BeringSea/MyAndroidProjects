package ict.edu.rs.weather;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by m.spasojevic on 5/18/2016.
 */
public class ForecastsFormatter {
    // klasa ima dve metode Read i Write
    public ArrayList<Forecast> Read(FileInputStream stream) {
        ArrayList<Forecast> forecasts = null;
        try {
            DataInputStream dataStream = new DataInputStream(stream);
            forecasts = new ArrayList<Forecast>();
            int n = dataStream.readInt();
            for (int i = 0; i < n; i++) {
                // redosled cidanja mora biti isti kao redosled pisanja zato smo prvo procitali int int n = dataStream.readInt();
                Forecast forecast = new Forecast();
                forecast.setDate(dataStream.readUTF());
                forecast.setDescription(dataStream.readUTF());
                forecast.setIconId(dataStream.readUTF());
                forecast.setHigh(dataStream.readDouble());
                forecast.setLow(dataStream.readDouble());
                forecasts.add(forecast);
            }
        }
        catch (Exception e) {

        }
        return forecasts;
    }

    public void Write(FileOutputStream stream,
                      ArrayList<Forecast> forecasts) {
        try {
            DataOutputStream dataStream = new DataOutputStream(stream);
            dataStream.writeInt(forecasts.size());// moramo da upisemo velicinu niza da bismo posle znali sta da procitamo
            for (Forecast forecast : forecasts) { // foreach petlja
                dataStream.writeUTF(forecast.getDate()); // korisitmo ovu metodu zato sto ne znamo koliko ce strig biti dugacak a ova metoda to resava
                dataStream.writeUTF(forecast.getDescription());
                dataStream.writeUTF(forecast.getIconId());
                dataStream.writeDouble(forecast.getHigh());
                dataStream.writeDouble(forecast.getLow());
            }
        }
        catch (Exception e) {
        }

    }
}
