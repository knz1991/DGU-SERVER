package com.dgu.server.repository;

import com.dgu.server.model.enums.ERole;
import com.dgu.server.model.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(ERole role);
    Optional<Role> findById(Long id);
    Boolean existsByName(String name);

}