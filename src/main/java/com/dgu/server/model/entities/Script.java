package com.dgu.server.model.entities;

import com.dgu.server.model.enums.EType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "script")
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreatedBy
    @ManyToOne()
    private User author;

    @ManyToMany(mappedBy = "scripts")
    private Set<User> shared = new HashSet<>();

    @NotBlank
    @Column(length= 255)
    private String src;

    @NotBlank
    private String name;

    private boolean editable;

    private String description;

    @Enumerated(EnumType.STRING)
    private EType type;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date lastModifiedAt;

    @LastModifiedBy
    @ManyToOne
    private User modifier;

    public Script(String name ,String description ,String src ,boolean editable, EType type){
        this.name = name;
        this.description = description;
        this.src = src;
        this.editable = editable;
        this.type = type;
        Date now = new Date();
        this.createdAt = now;
        this.lastModifiedAt = now;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Script script = (Script) o;
        return id != null && Objects.equals(id, script.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}