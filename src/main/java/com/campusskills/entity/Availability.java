package com.campusskills.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

    @Column(nullable = false)
    private String startTime; // e.g. "18:00"

    @Column(nullable = false)
    private String endTime;   // e.g. "19:00"

    @Column(nullable = false)
    private String mode;      // ONLINE, OFFLINE, BOTH

    public Availability() {}

    public Availability(Long id, Long userId, String dayOfWeek, String startTime, String endTime, String mode) {
        this.id = id;
        this.userId = userId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mode = mode;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static AvailabilityBuilder builder() {
        return new AvailabilityBuilder();
    }

    public static class AvailabilityBuilder {
        private Long id;
        private Long userId;
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String mode;

        public AvailabilityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AvailabilityBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AvailabilityBuilder dayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public AvailabilityBuilder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public AvailabilityBuilder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public AvailabilityBuilder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Availability build() {
            return new Availability(this.id, this.userId, this.dayOfWeek, this.startTime, this.endTime, this.mode);
        }
    }

}
