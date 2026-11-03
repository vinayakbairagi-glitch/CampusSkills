package com.campusskills.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class RatingRequest {
    @NotNull(message = "Rating is required (1-5)")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    private String comment;
    private String context; // SESSION, PROJECT
    private Long ratedUserId; // if context is PROJECT

    public RatingRequest() {}

    public RatingRequest(Integer rating, String comment, String context, Long ratedUserId) {
        this.rating = rating;
        this.comment = comment;
        this.context = context;
        this.ratedUserId = ratedUserId;
    }

    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Long getRatedUserId() {
        return this.ratedUserId;
    }

    public void setRatedUserId(Long ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

}
