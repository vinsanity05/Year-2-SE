package com_services;

public class Message {
    // Both the base for an email and the class that represents an sms
    private int id;
    private String sender, recipient;
    private String content; // Done as one string

    public Message(int id, String sender, String recipient, String content) {
        this.id = id;
        this.sender = sender; this.recipient = recipient; this.content = content;
    }

    // Getters
    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
}
