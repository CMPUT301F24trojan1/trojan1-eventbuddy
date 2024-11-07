package com.example.trojanplanner.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Date;

import java.util.Date;
import com.example.trojanplanner.model.Event;

public class ConcreteEvent extends Event {

    public ConcreteEvent(String name, String description, String facility, Date startDateTime, Date endDateTime) {
        super(name, description, facility, startDateTime, endDateTime, 0, 100L, 100L); // Default values for the last three parameters
    }
}

