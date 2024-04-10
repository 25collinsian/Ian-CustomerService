package org.example.iancustomersupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ticket {
    private String customerName;
    private String subject;
    private String body;
    private Map<String, byte[]> attachments;

    public Ticket(String customerName, String subject, String body) {
        this.customerName = customerName;
        this.subject = subject;
        this.body = body;
        this.attachments = new HashMap<>();
    }

    // Default constructor
    public Ticket() {
        this.attachments = new HashMap<>();
    }

    // Getters and setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addAttachment(String fileName, byte[] fileContent) {
        attachments.put(fileName, fileContent);
    }

    public int getNumberOfAttachments() {
        return attachments.size();
    }

    public byte[] getAttachment(int index) {
        List<byte[]> attachmentList = new ArrayList<>(attachments.values());
        if (index >= 0 && index < attachmentList.size()) {
            return attachmentList.get(index);
        }
        return null;
    }

    public Map<String, byte[]> getAllAttachments() {
        return attachments;
    }
}
