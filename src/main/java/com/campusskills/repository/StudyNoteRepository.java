package com.campusskills.repository;

import com.campusskills.entity.StudyNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyNoteRepository extends JpaRepository<StudyNote, Long> {
    List<StudyNote> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<StudyNote> findByIdAndUserId(Long id, Long userId);
}
