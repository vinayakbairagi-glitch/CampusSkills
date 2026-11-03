package com.campusskills.repository;

import com.campusskills.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
