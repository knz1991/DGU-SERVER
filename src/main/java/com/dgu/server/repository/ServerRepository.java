package com.dgu.server.repository;


import com.dgu.server.model.entities.Server;

import com.dgu.server.model.entities.User;
import com.dgu.server.model.enums.EType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.Set;

@Repository
public interface ServerRepository extends CrudRepository<Server, Long> {

    Server findByname(String name);
    Set<Server> findAll();
    Set<Server> findByType(EType type);
    Set<Server> findByOwnersContaining(User user);



}




