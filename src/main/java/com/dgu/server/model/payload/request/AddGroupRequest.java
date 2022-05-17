package com.dgu.server.model.payload.request;

import lombok.Data;

@Data
public class AddGroupRequest {
    private String name;
    private String description;
}
