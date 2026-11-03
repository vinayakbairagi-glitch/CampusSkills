package com.campusskills.repository;

import com.campusskills.entity.XpEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XpEventRepository extends JpaRepository<XpEvent, Long> {
    List<XpEvent> findByUserIdOrderByCreatedAtDesc(Long userId);
}
