package com.dgu.server.model.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ShareScriptRequest {

    @NotBlank
    private boolean share;

    @NotBlank
    private Long uId;

}
