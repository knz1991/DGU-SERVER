package com.dgu.server.repository;

import com.dgu.server.model.entities.Server;
import com.dgu.server.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    Optional<User> findById(Long id);
    Boolean existsByEmail(String email);
    Boolean existsByTrigramme(String trigramme);
    User findByResetpasswordtoken(String token);
    User getByEmail(String email);

Set<User> getByServersContains(Server server);
Set<User> getByServersNotContaining(Server server);


    @Override
    void deleteById(Long aLong);


}