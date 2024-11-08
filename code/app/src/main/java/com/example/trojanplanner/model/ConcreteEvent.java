package com.example.trojanplanner.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Date;

import java.util.Date;
import com.example.trojanplanner.model.Event;

public class ConcreteEvent extends Event implements Serializable {
    public ConcreteEvent(String name, String description, float price, String facility, Date startDateTime, Date endDateTime) {
        super(name, description, price, facility, startDateTime, endDateTime, 0, 100L, 100L);
    }
}

