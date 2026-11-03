package com.campusskills.service;

import com.campusskills.dto.StudyNoteRequest;
import com.campusskills.entity.StudyNote;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.StudyNoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyNoteService {
    private final StudyNoteRepository studyNoteRepository;

    public StudyNoteService(StudyNoteRepository studyNoteRepository) {
        this.studyNoteRepository = studyNoteRepository;
    }

    public List<StudyNote> getNotes(Long userId) {
        return studyNoteRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional
    public StudyNote createNote(Long userId, StudyNoteRequest request) {
        StudyNote note = new StudyNote(userId, request.getTitle().trim(), contentOrEmpty(request.getContent()));
        return studyNoteRepository.save(note);
    }

    @Transactional
    public StudyNote updateNote(Long userId, Long noteId, StudyNoteRequest request) {
        StudyNote note = findOwnedNote(userId, noteId);
        note.setTitle(request.getTitle().trim());
        note.setContent(contentOrEmpty(request.getContent()));
        return studyNoteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long userId, Long noteId) {
        studyNoteRepository.delete(findOwnedNote(userId, noteId));
    }

    private StudyNote findOwnedNote(Long userId, Long noteId) {
        return studyNoteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ApiException("Note not found", HttpStatus.NOT_FOUND));
    }

    private String contentOrEmpty(String content) {
        return content == null ? "" : content;
    }
}
