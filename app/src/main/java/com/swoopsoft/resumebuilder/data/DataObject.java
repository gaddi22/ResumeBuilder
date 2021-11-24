package com.swoopsoft.resumebuilder.data;

public class DataObject {
    public String type;
    public Object value;    //imgurl, text, or RecLetter

    public DataObject( String type, String value){
        this.type = type;
        this.value = value;
    }

    public DataObject(){
        type = "None";
        value = "None";
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
