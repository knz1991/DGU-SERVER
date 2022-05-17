package com.dgu.server.model.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ExecuteScriptRequest {
    @NotBlank
    private String user;

    @NotBlank
    private String password;

    @NotBlank
    private Long executorId;

    @NotBlank
    private int port;

    @NotBlank
    private Long scriptId;

    @NotBlank
    private Long serverId;

    private String args;
}