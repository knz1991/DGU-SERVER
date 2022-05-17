package com.dgu.server.controller;

import com.dgu.server.model.entities.Execution;

import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.service.ExecutionService;
import com.dgu.server.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/history")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(executionService.getExecutions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Execution> getServerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(executionService.getExecutionById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserHistory(@PathVariable("id")Long id){
        List<Execution> executions;
        try{
            executions = this.executionService.getExecutionsByUserId(id);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(executions);
    }
}
