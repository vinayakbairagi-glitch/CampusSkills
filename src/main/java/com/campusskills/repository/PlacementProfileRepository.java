package com.campusskills.repository;

import com.campusskills.entity.PlacementProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlacementProfileRepository extends JpaRepository<PlacementProfile, Long> {
    Optional<PlacementProfile> findByUserId(Long userId);
}
