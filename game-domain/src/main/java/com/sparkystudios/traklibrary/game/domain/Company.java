package com.sparkystudios.traklibrary.game.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
