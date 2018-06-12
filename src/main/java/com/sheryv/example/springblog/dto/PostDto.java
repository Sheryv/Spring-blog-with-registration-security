package com.sheryv.example.springblog.dto;

import com.sheryv.example.springblog.Constants;

import javax.validation.constraints.Size;

public class PostDto {


    @Size(min = 3, max = 300, message = Constants.INCORRECT_LENGTH_MSG)
    private String title;

    @Size(min = 3, max = 500, message = Constants.INCORRECT_LENGTH_MSG)
    private String summary;

    @Size(min = 3, max = 5000, message = Constants.INCORRECT_LENGTH_MSG)
    private String content;


    private long id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
