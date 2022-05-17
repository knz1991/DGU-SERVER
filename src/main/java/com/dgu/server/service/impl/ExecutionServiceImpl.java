package com.dgu.server.service.impl;


import com.dgu.server.model.entities.Execution;
import com.dgu.server.repository.ExecutionRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExecutionServiceImpl implements ExecutionService {

    @Autowired
    private ExecutionRepository executionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Execution> getExecutions() {
        return (List<Execution>) executionRepository.findAll();
    }

    @Override
    public Execution getExecutionById(Long id) {
        return executionRepository.findById(id).get();
    }

    @Override
    public List<Execution> getExecutionsByUserId(Long id) {

        if (!this.userRepository.findById(id).isPresent())
            throw new RuntimeException("Execution doesn't exist!");
        try {
            return executionRepository.getExecutionsByExecutor(this.userRepository.getById(id));
        }
        catch(Exception e){
            throw e;
        }
    }

    @Override
    public Execution createExecution(Execution execution) {
        return executionRepository.save(execution);
    }
}
