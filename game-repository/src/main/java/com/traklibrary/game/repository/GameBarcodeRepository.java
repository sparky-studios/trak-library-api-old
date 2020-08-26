package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GameBarcode;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameBarcodeRepository extends PagingAndSortingRepository<GameBarcode, Long> {

    Optional<GameBarcode> findByBarcode(String barcode);
}
