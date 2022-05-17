package com.dgu.server.model.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class VerifyCodeRequest {
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "(^[a-zA-Z0-9é]+(\\.)[a-zA-Z0-9]+@(neoxam|gmail)\\.com)?")
    private String email;

    @NotBlank
    @Size(max = 6)
    private String token;
}
