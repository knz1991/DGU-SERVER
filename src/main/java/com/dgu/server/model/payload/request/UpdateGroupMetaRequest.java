package com.dgu.server.model.payload.request;


import lombok.Data;

@Data
public class UpdateGroupMetaRequest {
    private Long id;
    private String name;
    private String description;
}
