package com.dgu.server.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String trigramme;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String trigramme, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.trigramme = trigramme;
        this.email = email;
        this.roles = roles;
    }
}
