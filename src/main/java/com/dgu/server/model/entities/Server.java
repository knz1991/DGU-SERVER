package com.dgu.server.model.entities;

import com.dgu.server.model.enums.EType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class Server {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column
    @NotBlank
    private String name;

    @Column(length= 125)
    private String password;
    @Column
    private String  login;
    @Column
    private String description;

    private EType type;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> owners;

    @Column
    private String serverIp;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="server")
    @JsonIgnore
    private Set<Execution> executions;


    public Server(String name, String password, String login,String description, String serverIp) {
        this.name = name;
        this.password = password;
        this.login = login;
        this.description = description;
        this.serverIp = serverIp;
    }

}



