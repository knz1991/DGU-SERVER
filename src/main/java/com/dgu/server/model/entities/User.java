package com.dgu.server.model.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "trigramme")
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 50, unique = true)
    @Pattern(regexp = "(^[a-zA-Z0-9é]+(\\.)[a-zA-Z0-9]+@(neoxam|gmail)\\.com)?")
    @NotBlank
    private String email;

    @Column(length= 125)
    @JsonIgnore
    private String password;

    @Column(length = 3, unique = true)
    private String trigramme;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(	name = "user_scripts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "script_id"))
    @JsonIgnore
    private Set<Script> scripts = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER,mappedBy="author",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Script> ownedscripts;

    @Column(columnDefinition="boolean default false")
    private Boolean approved = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length=6)
    private String resetpasswordtoken;

    @ManyToMany(mappedBy="owners", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Server> servers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="executor")
    @JsonIgnore
    private Set<Execution> executions;

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groups = new HashSet<>();


    public User(String email, String encode, String trigramme) {
        this.email = email;
        this.password = encode;
        this.trigramme = trigramme;
        this.createdAt = new Date();
    }

    public User(String email, String encode, String trigramme, Boolean approved) {
        this.email = email;
        this.password = encode;
        this.trigramme = trigramme;
        this.approved = approved;
        this.createdAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User that = (User) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}