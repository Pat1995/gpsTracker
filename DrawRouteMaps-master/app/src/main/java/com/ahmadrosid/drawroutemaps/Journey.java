package com.ahmadrosid.drawroutemaps;

/**
 * Created by pat95 on 20.08.2017.
 */

public class Journey {

    private String id;
    private String name;
    private String dateJourney;

    public Journey(String id, String name, String dateJourney) {
        this.id = id;
        this.name = name;
        this.dateJourney = dateJourney;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateJourney() {
        return dateJourney;
    }

    @Override
    public String toString() {
        return id + ". " + dateJourney;
    }

}
