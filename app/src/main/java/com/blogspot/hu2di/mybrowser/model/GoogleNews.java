package com.blogspot.hu2di.mybrowser.model;

/**
 * Created by PhungVanQuang on 3/14/2017.
 */

public class GoogleNews {

    private String newsCategory;
    private String newsTitle;
    private String link;
    private String description;
    private String publishDate;

    public GoogleNews(String newsCategory, String newsTitle, String link, String description, String publishDate) {
        this.newsCategory = newsCategory;
        this.newsTitle = newsTitle;
        this.link = link;
        this.description = description;
        this.publishDate = publishDate;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
