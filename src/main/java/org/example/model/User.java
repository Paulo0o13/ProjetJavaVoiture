package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.RoleType;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(unique = true, nullable = false)
    public String pseudo;

    public String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;
}