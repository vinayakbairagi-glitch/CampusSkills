package com.campusskills.dto;

import java.util.List;

public class AvailabilityRequest {
    private List<Slot> slots;

    public static class Slot {
        private String dayOfWeek; // MONDAY, TUESDAY...
        private String startTime; // "17:00"
        private String endTime;   // "19:00"
        private String mode;      // ONLINE, OFFLINE, BOTH

        public Slot() {}

        public Slot(String dayOfWeek, String startTime, String endTime, String mode) {
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
            this.mode = mode;
        }

        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
    }

    public AvailabilityRequest() {}

    public AvailabilityRequest(List<Slot> slots) {
        this.slots = slots;
    }

    public List<Slot> getSlots() {
        return this.slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }
}
