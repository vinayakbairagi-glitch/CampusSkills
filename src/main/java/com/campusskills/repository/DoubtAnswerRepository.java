package com.campusskills.repository;

import com.campusskills.entity.DoubtAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoubtAnswerRepository extends JpaRepository<DoubtAnswer, Long> {
    List<DoubtAnswer> findByDoubtIdOrderByUpvoteCountDescCreatedAtAsc(Long doubtId);
    long countByAuthorIdAndIsBestTrue(Long authorId);
    long countByAuthorId(Long authorId);
}
