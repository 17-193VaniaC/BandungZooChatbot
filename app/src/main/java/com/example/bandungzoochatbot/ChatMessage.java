package com.example.bandungzoochatbot;

import java.util.ListIterator;

public class ChatMessage {

    private Boolean isMine, isLocation;
    private String context;

    public ChatMessage(boolean isMine, String context, boolean isLocation){
        this.isMine = isMine;
        this.context = context;
        this.isLocation = isLocation;
    }

    public void setMine(boolean mine){
        isMine = mine;
    }

    public void setContext(String text){
        context = text;
    }

    public boolean isMine(){
        return isMine;
    }

    public Boolean getLocation() { return isLocation; }

    public void setLocation(Boolean location) {isLocation = location;}

    public String getContext(){
        return context;
    }

}
