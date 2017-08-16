package ict.edu.rs.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by David on 2016-11-23.
 */

public class DbWeather extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "Weather.db";
    public static final int DATABASE_VERSION = 1;

    // pravimo polje tipa SQLiteDatabase koje cemo pozivati u metodi Open, stavili smo seter da bi neko spolja mogao da pravi upite kakve zeli ali i da koristi nese metode
    private SQLiteDatabase sqlDb = null;

    public SQLiteDatabase getSqlDb() {
        return sqlDb;
    }
    // definisemo konstante kojima cemo da pravimo tebelu, izbegavamo stringove zbog moguce greske u pisanju
    // posto je moguce imati vise tabela za svaku tabelu je pozeljno napraviti staticku unutrasnju klasu u kojoj se definisu konstante za tu tabelu, u toj unutrasnjoj klasi definisemo samo konstante (nema polja ni metoda)

    public static class ForecastTable{
        public static final String TABLE_NAME = "Forecast";
        public static final String ID = "ID";
        public static final String DATE = "Date";
        public static final String HIGH = "High";
        public static final String LOW = "Low";
        public static final String DESCRIPTION = "Description";
        public static final String ICON_ID = "IconId";
        public static final String LOCATION_ID = "LocationId";

        // sledeca konstanta je create upit za bazu

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE '"+TABLE_NAME+"' (\n" +
                        "\t'"+ID+"'\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "\t'"+DATE+"'\tTEXT NOT NULL,\n" +
                        "\t'"+HIGH+"'\tREAL NOT NULL,\n" +
                        "\t'"+LOW+"'\tREAL NOT NULL,\n" +
                        "\t'"+DESCRIPTION+"'\tTEXT NOT NULL,\n" +
                        "\t'"+ICON_ID+"'\tTEXT NOT NULL,\n" +
                        "\t'"+LOCATION_ID+"'\tINTEGER NOT NULL\n" +
                        ");";

        // pravimo konstantu za brisanje baze kako bi se koristila u funkciji onUpgrade()

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS" + TABLE_NAME;
    }

    public DbWeather(Context context) { // imamo samo jedan parametar umesto 3 kojasu genericka zato sto database morada ima neki kontekst, tj mora da bude u kontekstu nekog aktivitija aplikacije
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ForecastTable.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ForecastTable.SQL_DELETE_ENTRIES);
        // ponovo izvrsavamo upit za kreiranje baze
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    // mogli bismo upite da realizujemo u MainActivty (opis na developer.android.com pa u DEVELOP), ali mi cemo sve uptite koji su nam potrebni da radimo u ovoj klasi

    // pravimo posebnu metodu Open

    public boolean open(){
        this.sqlDb = this.getReadableDatabase();
        return this.sqlDb.isOpen();
    }

    // pravimo metodu za zatvaranje Close

    public void close(){
        this.sqlDb.close();
    }

    // metoda za citanje podataka, preko locationId znamo tacno za koji grad hocemo prognozu iz kesirane baze podataka

    public ArrayList<Forecast> readForecast(long locationId){
        ArrayList<Forecast> forecasts = new ArrayList<Forecast>();
        // upit smo pisali na nebezbedan nacin sa nadovezivanjem parametara zato sto ne postoji u nasem slucaju opsasnost od sql injection-a
        Cursor cursor = sqlDb.rawQuery("SELECT * FROM "+ForecastTable.TABLE_NAME+"\n" +
                "WHERE " + ForecastTable.LOCATION_ID + " = " + locationId + ";",null); // rawQuery koristi se samo za select upite
        while (cursor.moveToNext()){
            Forecast forecast = new Forecast();
            forecast.setDate(cursor.getString(cursor.getColumnIndex(ForecastTable.DATE)));
            forecast.setHigh(cursor.getDouble(cursor.getColumnIndex(ForecastTable.HIGH)));
            forecast.setLow(cursor.getDouble(cursor.getColumnIndex(ForecastTable.LOW)));
            forecast.setDescription(cursor.getString(cursor.getColumnIndex(ForecastTable.DESCRIPTION)));
            forecast.setIconId(cursor.getString(cursor.getColumnIndex(ForecastTable.ICON_ID)));
            forecasts.add(forecast);
        }
        return forecasts;
    }

    // metoda za unos podataka, u njoj kao primer koristimo bezbednu verijantu koja onemogucuje sql injection to se radi sa metodom insert()

    public int writeForecast(long locationId, ArrayList<Forecast> forecasts){
        // pravimo upit za brisanje zastarelih prognoza za datu lokaciju tako da se na istoj lokaciji posle ovog upita upisuju samo nove prognoze
        sqlDb.execSQL("DELETE FROM "+ForecastTable.TABLE_NAME+"\n" +
                "WHERE "+ForecastTable.LOCATION_ID+" = " + locationId + ";");
        int count = 0;
        for(Forecast forecast : forecasts){
            ContentValues values = new ContentValues();
            values.put(ForecastTable.DATE,forecast.getDate());
            values.put(ForecastTable.HIGH,forecast.getHigh());
            values.put(ForecastTable.LOW,forecast.getLow());
            values.put(ForecastTable.DESCRIPTION,forecast.getDescription());
            values.put(ForecastTable.ICON_ID,forecast.getIconId());
            values.put(ForecastTable.LOCATION_ID,locationId);
            count += sqlDb.insert(ForecastTable.TABLE_NAME,null,values);
        }
        return count;
    }
}
