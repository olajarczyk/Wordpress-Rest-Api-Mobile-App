package com.example.podgorze_krakow;

public class WordpressEvents {
    private final String title;
   private String content;



    public WordpressEvents(String title, String content )  {
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
