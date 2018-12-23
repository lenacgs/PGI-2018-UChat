package com.pgi.uchat;


public class DuvidasObject {
    private String textUser;
    private String textBoot;
    private String date;

    public DuvidasObject(String textUser, String textBoot, String date){
        this.textUser = textUser;
        this.textBoot = textBoot;
        this.date = date;
    }

    public DuvidasObject(String textUser, String date){
        this.textUser = textUser;
        this.textBoot = "";
        this.date = date;
    }

    public String getTextUser(){
        return textUser;
    }

    public String getTextBoot(){
        return textBoot;
    }

    public String getDate(){
        return date;
    }
}