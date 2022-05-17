package com.dgu.server.model.payload.request;


import lombok.Data;

import java.util.ArrayList;
import java.util.Set;

@Data
public class AddMembersRequest {
    private ArrayList<Long> ids;
}
