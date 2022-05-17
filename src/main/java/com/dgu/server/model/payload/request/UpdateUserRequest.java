package com.dgu.server.model.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank
    private Long id;

    @Size(max = 50)
    @Pattern(regexp = "(^[a-zA-Z0-9é]+(\\.)[a-zA-Z0-9]+@(neoxam|gmail)\\.com)?")
    private String email;

    private Set<String> roles ;

    private String approved;
}
