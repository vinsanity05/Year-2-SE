package com_services;

public class CommunicationService {
    // Contains both an email and sms service so that its easy to call them
    private EmailService emailService;
    private SmsService smsService;

    public CommunicationService() {
        emailService = new EmailService();
        smsService = new SmsService();
    }

    public void sendEmail(String sender, String recipient, String subject, String content, String[] attachments) {
        emailService.sendEmail(sender, recipient, subject, content, attachments);
    }

    public void sendSms(String sender, String recipient, String content) {
        smsService.sendSms(sender, recipient, content);
    }

    public String getEmail(int id) {
        return emailService.getEmail(id);
    }

    public String getSms(int id) {
        return smsService.getSms(id);
    }
}
