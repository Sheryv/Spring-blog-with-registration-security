package com.sheryv.example.springblog.dto;

import com.sheryv.example.springblog.Constants;

import javax.validation.constraints.Size;

public class CommentDto {

    private long id;

    @Size(min = 3, max = 1000, message = Constants.INCORRECT_LENGTH_MSG)
    private String content;

    public CommentDto(long postId) {
        this.id = postId;
    }

    public CommentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
