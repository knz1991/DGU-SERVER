package com.dgu.server.repository;

import com.dgu.server.model.entities.Execution;
import com.dgu.server.model.entities.Role;
import com.dgu.server.model.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExecutionRepository extends CrudRepository<Execution, Long> {
        List<Execution> getExecutionsByExecutor(User executor);
}
