package com.dgu.server.repository;

import com.dgu.server.model.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Boolean existsByName(String name);
}