package com.kartashov.postgis.hibernate;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Properties extends HashMap<String, Object> {

    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return this;
    }

    @JsonAnySetter
    public Object put(String key, Object value) {
        return super.put(key, value);
    }
}
