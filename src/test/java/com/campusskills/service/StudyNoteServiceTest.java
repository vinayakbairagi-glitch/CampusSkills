package com.campusskills.service;

import com.campusskills.dto.StudyNoteRequest;
import com.campusskills.entity.StudyNote;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.StudyNoteRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudyNoteServiceTest {
    @Test
    void trimsTitleAndStoresNoteForCurrentUser() {
        StudyNoteRepository repository = mock(StudyNoteRepository.class);
        StudyNoteService service = new StudyNoteService(repository);
        StudyNoteRequest request = new StudyNoteRequest();
        request.setTitle("  Calculus  ");
        request.setContent("Limits and derivatives");

        service.createNote(12L, request);

        verify(repository).save(argThat(note ->
                note.getUserId().equals(12L)
                        && note.getTitle().equals("Calculus")
                        && note.getContent().equals("Limits and derivatives")));
    }

    @Test
    void cannotUpdateNoteOwnedByAnotherUser() {
        StudyNoteRepository repository = mock(StudyNoteRepository.class);
        when(repository.findByIdAndUserId(7L, 12L)).thenReturn(Optional.empty());
        StudyNoteService service = new StudyNoteService(repository);
        StudyNoteRequest request = new StudyNoteRequest();
        request.setTitle("Private");
        request.setContent("Text");

        assertThrows(ApiException.class, () -> service.updateNote(12L, 7L, request));
        verify(repository, never()).save(any(StudyNote.class));
    }
}
