package com.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "developer")
@PrimaryKeyJoinColumn(name = "id")
public class Developer extends Company {

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "developer", cascade = CascadeType.ALL)
    private Set<GameDeveloperXref> gameDeveloperXrefs;
}
