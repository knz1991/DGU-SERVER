package com.dgu.server.repository;

import com.dgu.server.model.entities.Script;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScriptRepository extends JpaRepository<Script, Long> {
}