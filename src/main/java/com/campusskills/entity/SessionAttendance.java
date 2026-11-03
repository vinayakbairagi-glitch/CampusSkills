package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_attendance")
public class SessionAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupSessionId;

    @Column(nullable = false)
    private Long userId;

    private Boolean checkedIn = false;

    private LocalDateTime checkedInAt;

    private Boolean markedByAdmin = false;

    public SessionAttendance() {}

    public SessionAttendance(Long id, Long groupSessionId, Long userId, Boolean checkedIn, LocalDateTime checkedInAt, Boolean markedByAdmin) {
        this.id = id;
        this.groupSessionId = groupSessionId;
        this.userId = userId;
        this.checkedIn = checkedIn;
        this.checkedInAt = checkedInAt;
        this.markedByAdmin = markedByAdmin;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupSessionId() {
        return this.groupSessionId;
    }

    public void setGroupSessionId(Long groupSessionId) {
        this.groupSessionId = groupSessionId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getCheckedIn() {
        return this.checkedIn;
    }

    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public LocalDateTime getCheckedInAt() {
        return this.checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public Boolean getMarkedByAdmin() {
        return this.markedByAdmin;
    }

    public void setMarkedByAdmin(Boolean markedByAdmin) {
        this.markedByAdmin = markedByAdmin;
    }

    public static SessionAttendanceBuilder builder() {
        return new SessionAttendanceBuilder();
    }

    public static class SessionAttendanceBuilder {
        private Long id;
        private Long groupSessionId;
        private Long userId;
        private Boolean checkedIn = false;
        private LocalDateTime checkedInAt;
        private Boolean markedByAdmin = false;

        public SessionAttendanceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SessionAttendanceBuilder groupSessionId(Long groupSessionId) {
            this.groupSessionId = groupSessionId;
            return this;
        }

        public SessionAttendanceBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public SessionAttendanceBuilder checkedIn(Boolean checkedIn) {
            this.checkedIn = checkedIn;
            return this;
        }

        public SessionAttendanceBuilder checkedInAt(LocalDateTime checkedInAt) {
            this.checkedInAt = checkedInAt;
            return this;
        }

        public SessionAttendanceBuilder markedByAdmin(Boolean markedByAdmin) {
            this.markedByAdmin = markedByAdmin;
            return this;
        }

        public SessionAttendance build() {
            return new SessionAttendance(this.id, this.groupSessionId, this.userId, this.checkedIn, this.checkedInAt, this.markedByAdmin);
        }
    }

}
