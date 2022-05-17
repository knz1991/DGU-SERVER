package com.dgu.server.model.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class AddUserRequest {

    @NotBlank
    @Pattern(regexp = "(^[a-zA-Z0-9é]+(\\.)[a-zA-Z0-9]+@(neoxam|gmail)\\.com)?")
    private String email;

    @NotBlank
    @Pattern(regexp = "(^[A-Z]{3})")
    private String trigramme;

    @NotBlank
    @Size(min = 8)
    private String password;

    private Set<String> roles ;

    private boolean approved = true;
}
