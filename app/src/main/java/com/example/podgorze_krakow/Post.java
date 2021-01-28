package com.example.podgorze_krakow;

public class Post {
    private final String title;
    private final String content;

    Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}