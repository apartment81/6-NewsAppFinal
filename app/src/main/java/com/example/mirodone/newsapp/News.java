package com.example.mirodone.newsapp;

import java.util.Date;

public class News {

    private String newsTitle;
    private String newsSection;
    private String newsAuthor;
    private Date newsDate;
    private String newsUrl;

    public News(String newsTitle, String newsSection, String newsAuthor, Date newsDate, String newsUrl) {
        this.newsTitle = newsTitle;
        this.newsSection = newsSection;
        this.newsAuthor = newsAuthor;
        this.newsDate = newsDate;
        this.newsUrl = newsUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsSection() {
        return newsSection;
    }

    public String getNewsAuthor() {
        return newsAuthor;
    }

    public Date getNewsDate() {
        return newsDate;
    }

    public String getNewsUrl() {
        return newsUrl;
    }
}
