package org.example;

public class Geolocation {
    private double longitude;
    private double latitude;

    public Geolocation(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double[] toDoubleArray() {
        return new double[] {latitude, longitude};
    }

    public double distance(Geolocation location) {
        double x = this.latitude - location.latitude;
        double y = this.longitude - location.longitude;
        return Math.sqrt(x*x + y*y);
    }

}
