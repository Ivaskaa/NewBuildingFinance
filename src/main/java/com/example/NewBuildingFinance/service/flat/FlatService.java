package com.example.NewBuildingFinance.service.flat;

import com.example.NewBuildingFinance.dto.flat.FlatTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FlatService {

    Page<FlatTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Optional<Integer> number,
            Optional<Long> objectId,
            Optional<String> status,
            Optional<Double> areaStart,
            Optional<Double> areaFin,
            Optional<Integer> priceStart,
            Optional<Integer> priceFin,
            Optional<Integer> advanceStart,
            Optional<Integer> advanceFin
    );

    Flat save(Flat flat);

    Flat update(Flat flatForm);

    void deleteById(Long id);

    ResponseEntity<byte[]> getXlsx() throws IOException;

    ResponseEntity<byte[]> getXlsxExample() throws IOException;

    boolean setXlsx(MultipartFile file) throws IOException;

    ResponseEntity<byte[]> getXlsxErrors() throws IOException;

    Flat findById(Long id);

    Flat findByContractId(Long id);

    List<Flat> getWithContractWithFlatPaymentByObjectId(Long id, Long flatId);

    List<Flat> getFlatsWithoutContractByObjectId(Long id, Long flatId);

    boolean checkPrice(Double price, Double salePrice);

    boolean checkPercentages(Integer agency, Integer manager);

    boolean checkFlatNumber(Integer number, Long objectId);

    boolean checkStatus(StatusFlat status);
}
