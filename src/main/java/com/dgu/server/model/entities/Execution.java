package com.dgu.server.model.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreatedDate
    private Date createdAt;

    @ManyToOne
    private User executor;

    @ManyToOne
    private Script script;

    @ManyToOne
    private Server server;

    private String result;

    private String existStatus;

    private String details;

    private String username;

    private String port;

    private String args;

    public Execution(User executor, Script script, Server server, String result, String existStatus, String details, String username, String port, String args) {
        this.executor = executor;
        this.script = script;
        this.server = server;
        this.result = result;
        this.existStatus = existStatus;
        this.details = details;
        this.username = username;
        this.port = port;
        this.args = args;
        this.createdAt = new Date();
    }
}
