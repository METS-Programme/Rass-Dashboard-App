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

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    String item_id;

    public List<referenceLabTests> getSampls() {
        return sampls;
    }

    public void setSampls(List<referenceLabTests> sampls) {
        this.sampls = sampls;
    }

    List<referenceLabTests> sampls;
}
