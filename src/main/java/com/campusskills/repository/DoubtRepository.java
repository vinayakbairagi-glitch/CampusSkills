package com.campusskills.repository;

import com.campusskills.entity.Doubt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoubtRepository extends JpaRepository<Doubt, Long> {
    List<Doubt> findAllByOrderByCreatedAtDesc();
    List<Doubt> findBySubjectIgnoreCase(String subject);
    List<Doubt> findByStatus(String status);

    @Query("SELECT d FROM Doubt d WHERE " +
           "(:query IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.topic) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:subject IS NULL OR LOWER(d.subject) = LOWER(:subject)) AND " +
           "(:status IS NULL OR d.status = :status) " +
           "ORDER BY d.createdAt DESC")
    List<Doubt> searchDoubts(@Param("query") String query,
                             @Param("subject") String subject,
                             @Param("status") String status);
}
