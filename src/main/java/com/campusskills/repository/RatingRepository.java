package com.campusskills.repository;

import com.campusskills.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedUserId(Long ratedUserId);
    List<Rating> findBySessionId(Long sessionId);
    List<Rating> findByProjectId(Long projectId);
    boolean existsBySessionIdAndRaterId(Long sessionId, Long raterId);
    boolean existsByProjectIdAndRaterId(Long projectId, Long raterId);
}
