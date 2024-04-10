package org.example.iancustomersupport;

public class Attachment {
    public  String name;
    public byte[] content;


    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
