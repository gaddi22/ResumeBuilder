package com.swoopsoft.resumebuilder.data;

public class RecLetter {
    String contact, email, phone, letter;

     public RecLetter(){
         contact = "";
         email = "";
         phone = "";
         letter = "";
     }

    public RecLetter(String contact, String email, String phone, String letter){
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.letter = letter;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLetter() {
        return letter;
    }
}
