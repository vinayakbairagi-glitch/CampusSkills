package com.campusskills.repository;

import com.campusskills.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    List<StudyGroup> findAllByOrderByCreatedAtDesc();
    Optional<StudyGroup> findFirstByNameAndSubjectAndCategory(String name, String subject, String category);

    @Query("SELECT g FROM StudyGroup g WHERE " +
           "(:query IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(g.subject) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:subject IS NULL OR LOWER(g.subject) = LOWER(:subject)) AND " +
           "(:category IS NULL OR g.category = :category) AND " +
           "(:branch IS NULL OR g.branch = :branch) AND " +
           "(:studyYear IS NULL OR g.studyYear = :studyYear) AND " +
           "(:mode IS NULL OR g.mode = :mode) " +
           "ORDER BY g.createdAt DESC")
    List<StudyGroup> searchGroups(@Param("query") String query,
                                  @Param("subject") String subject,
                                  @Param("category") String category,
                                  @Param("branch") String branch,
                                  @Param("studyYear") Integer studyYear,
                                  @Param("mode") String mode);
}
