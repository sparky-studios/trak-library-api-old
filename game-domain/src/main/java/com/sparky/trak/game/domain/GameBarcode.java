package com.sparky.trak.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name = "game_barcode")
public class GameBarcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "game_id", nullable = false, updatable = false)
    private long gameId;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "game_id", updatable = false, insertable = false)
    private Game game;

    @Column(name = "platform_id", nullable = false, updatable = false)
    private long platformId;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "platform_id", updatable = false, insertable = false)
    private Platform platform;

    @Column(name = "barcode", nullable = false, unique = true, length = 48)
    private String barcode;

    @Column(name = "barcode_type", nullable = false)
    private BarcodeType barcodeType;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
