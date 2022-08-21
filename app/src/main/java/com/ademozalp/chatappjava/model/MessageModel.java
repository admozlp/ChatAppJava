package com.ademozalp.chatappjava.model;

import java.util.Date;

public class MessageModel {

    public String sender;
    public String reciever;
    public String message;

    public MessageModel(String sender, String reciever, String message){
        this.sender =sender;
        this.reciever = reciever;
        this.message = message;
    }
}
