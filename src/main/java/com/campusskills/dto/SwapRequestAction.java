package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class SwapRequestAction {
    @NotBlank(message = "Action is required (ACCEPT, REJECT, CANCEL)")
    private String action; // ACCEPT, REJECT, CANCEL

    public SwapRequestAction() {}

    public SwapRequestAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
