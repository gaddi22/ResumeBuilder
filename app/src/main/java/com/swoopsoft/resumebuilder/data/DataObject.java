package com.swoopsoft.resumebuilder.data;

public class DataObject {
    public String type;
    public Object value;

    public DataObject( String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
