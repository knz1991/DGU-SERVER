package com.dgu.server.model.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class UpdateScriptRequest {
    private boolean editable;

    private String update;

    private String code;

    private String name;

    private String description;

    private Long id;

    private String type;


}
