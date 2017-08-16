package ict.edu.rs.weather;

/**
 * Created by m.spasojevic on 4/20/2016.
 */
public class Forecast {
    private String date;
    private double high;
    private double low;
    private String description;
    private String iconId;

    public Forecast() {
    }

    public Forecast(String date, double high, double low, String description, String iconId) {
        this.iconId = iconId;
        this.setDate(date);
        this.setHigh(high);
        this.setLow(low);
        this.setDescription(description);
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public String getDescription() {
        return description;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }
    public String getIconId() {
        return iconId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "date='" + date + '\'' +
                ", high=" + high +
                ", low=" + low +
                ", description='" + description + '\'' +
                '}';
    }

//    private static Forecast[] makeSampleForecasts() {
//        return new Forecast[]{
//                new Forecast("23.4", 22, 15, "Sunny", iconId),
//                new Forecast("24.4", 20, 13, "Sunny", iconId),
//                new Forecast("25.4", 18, 14, "Sunny", iconId),
//                new Forecast("26.4", 18, 11, "Cloudy", iconId),
//                new Forecast("27.4", 17, 11, "Rain", iconId),
//                new Forecast("28.4", 15, 10, "Rain", iconId),
//                new Forecast("29.4", 18, 11, "Sunny", iconId),
//                new Forecast("30.4", 21, 14, "Sunny", iconId),
//                new Forecast("1.5", 25, 13, "Sunny", iconId),
//                new Forecast("2.5", 22, 11, "Rain", iconId),
//                new Forecast("3.5", 20, 14, "Sunny", iconId),
//                new Forecast("4.5", 22, 15, "Sunny", iconId),
//                new Forecast("5.5", 20, 14, "Cloudy", iconId),
//                new Forecast("6.5", 20, 14, "Heavy Rain", iconId),
//                new Forecast("7.5", 20, 11, "Sunny", iconId),
//                new Forecast("8.5", 19, 12, "Sunny", iconId),
//                new Forecast("9.5", 13, 8, "Rain", iconId),
//                new Forecast("10.5", 11, 5, "Rain", iconId),
//                new Forecast("11.5", 14, 7, "Sunny", iconId),
//        };
//    }
}
