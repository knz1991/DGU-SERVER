package com.dgu.server.service;

import com.dgu.server.model.entities.Execution;
import com.dgu.server.model.entities.Server;
import com.dgu.server.model.payload.response.MessageResponse;

import java.util.List;

public interface ExecutionService {

    List<Execution> getExecutions();

    Execution getExecutionById(Long id);

    List<Execution> getExecutionsByUserId(Long id);

    Execution createExecution(Execution execution);

}
