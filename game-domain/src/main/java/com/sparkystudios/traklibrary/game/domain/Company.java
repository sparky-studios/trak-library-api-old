package com.sparkystudios.traklibrary.game.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "company")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Developer.class, name = "developer"),
        @JsonSubTypes.Type(value = Publisher.class, name = "publisher")
})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "founded_date", nullable = false)
    private LocalDate foundedDate;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
