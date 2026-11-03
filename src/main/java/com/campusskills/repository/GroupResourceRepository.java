package com.campusskills.repository;

import com.campusskills.entity.GroupResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupResourceRepository extends JpaRepository<GroupResource, Long> {
    List<GroupResource> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}
