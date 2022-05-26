package com_services;

import com.sun.istack.internal.Nullable;

public class Email extends Message {

    private String subject;
    private String[] attachments; // URLs

    public Email(int id, String sender, String recipient, String subject, String content, @Nullable String[] attachments) {
        super(id, sender, recipient, content);
        this.subject = subject;
        if (attachments != null) {
            this.attachments = attachments;
        }
    }

    // Getters
    public String getSubject() { return subject; }
    public String[] getAttachments() { return attachments; }

}
