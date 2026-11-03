package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class GroupResourceCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Resource type is required (LINK, YOUTUBE, PDF, DOCUMENT)")
    private String resourceType;

    @NotBlank(message = "URL or File Path is required")
    private String urlOrPath;

    public GroupResourceCreateRequest() {}

    public GroupResourceCreateRequest(String title, String resourceType, String urlOrPath) {
        this.title = title;
        this.resourceType = resourceType;
        this.urlOrPath = urlOrPath;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUrlOrPath() {
        return this.urlOrPath;
    }

    public void setUrlOrPath(String urlOrPath) {
        this.urlOrPath = urlOrPath;
    }

}
