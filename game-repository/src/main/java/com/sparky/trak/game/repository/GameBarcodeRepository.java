package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.GameBarcode;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface GameBarcodeRepository extends PagingAndSortingRepository<GameBarcode, Long> {

    Optional<GameBarcode> findByBarcode(String barcode);
}
