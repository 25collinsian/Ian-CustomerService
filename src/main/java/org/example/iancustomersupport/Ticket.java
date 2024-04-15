package org.example.iancustomersupport;

import java.time.LocalDate;

public class Ticket {
    private String title;
    private LocalDate date;
    private String body;
    private Attachment attachment;

    public Ticket() {
        super();
    }

    public Ticket(String title, String body, Attachment attachment) {
        this.title = title;
        setDate();
        this.body = body;
        this.attachment = attachment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate() {
        this.date = LocalDate.now();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public boolean hasAttachment() {
        return attachment.getName().length() > 0 && attachment.getContents().length > 0;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", body='" + body + '\'' +
                ", image=" + attachment +
                '}';
    }
}
