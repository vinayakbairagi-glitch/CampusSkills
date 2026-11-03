package com.campusskills.controller;

import com.campusskills.dto.StudyNoteRequest;
import com.campusskills.entity.StudyNote;
import com.campusskills.entity.User;
import com.campusskills.service.StudyNoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class StudyNoteController {
    private final StudyNoteService studyNoteService;

    public StudyNoteController(StudyNoteService studyNoteService) {
        this.studyNoteService = studyNoteService;
    }

    @GetMapping
    public ResponseEntity<List<StudyNote>> getNotes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(studyNoteService.getNotes(user.getId()));
    }

    @PostMapping
    public ResponseEntity<StudyNote> createNote(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody StudyNoteRequest request) {
        return ResponseEntity.ok(studyNoteService.createNote(user.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyNote> updateNote(@AuthenticationPrincipal User user,
                                                @PathVariable Long id,
                                                @Valid @RequestBody StudyNoteRequest request) {
        return ResponseEntity.ok(studyNoteService.updateNote(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@AuthenticationPrincipal User user, @PathVariable Long id) {
        studyNoteService.deleteNote(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
