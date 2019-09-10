package com.mets.rassdasshboard.app.models;

import java.util.List;

public class OrgsUnits {
    String item_name;

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public List<orgUnit_> getEntities() {
        return entities;
    }

    public void setEntities(List<orgUnit_> sampls) {
        this.entities = entities;
    }

    List<orgUnit_> entities;
}
