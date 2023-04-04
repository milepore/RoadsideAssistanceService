package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class Assistant implements Comparable {
    String name;
    Geolocation location;

    static AtomicInteger unnamedIndex = new AtomicInteger(0);

    public Assistant(String name) {
        this.name = name;
    }

    public Assistant() {
        this.name = "Unnamed" + Integer.toString(unnamedIndex.incrementAndGet());
    }

    public String getName() {
        return name;
    }

    public void setLocation(Geolocation location) {
        this.location = location;
    }

    public Geolocation getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Assistant: " + name;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Assistant) {
            return name.compareTo(((Assistant)o).getName());
        }
        return 0;
    }
}
