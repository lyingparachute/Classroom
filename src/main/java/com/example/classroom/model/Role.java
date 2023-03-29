package com.example.classroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @ManyToMany(mappedBy = "roles")
//    private Collection<User> users;

    @ManyToMany
    @JoinTable(name = "roles_privileges",
            joinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id",
                    referencedColumnName = "id"))
    private Collection<Privilege> privileges;
}
