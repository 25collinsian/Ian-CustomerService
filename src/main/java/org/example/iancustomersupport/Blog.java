package org.example.iancustomersupport;

import java.time.LocalDate;

public class Blog {
    private String title;
    private LocalDate date;
    private String body;
    private Image image;

    public Blog() {
        super();
    }

    public Blog(String title, String body, Image image) {
        this.title = title;
        setDate();
        this.body = body;
        this.image = image;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean hasImage() {
        return image.getName().length() > 0 && image.getContents().length > 0;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", body='" + body + '\'' +
                ", image=" + image +
                '}';
    }
}
