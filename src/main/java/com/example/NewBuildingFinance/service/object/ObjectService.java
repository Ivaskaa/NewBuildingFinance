package com.example.NewBuildingFinance.service.object;

import com.example.NewBuildingFinance.dto.object.ObjectTableDto;
import com.example.NewBuildingFinance.entities.object.Object;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ObjectService {

    Page<ObjectTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection
    );

    List<Object> findAll();

    Object save(Object object);

    Object update(Object objectForm);

    void deleteById(Long id);

    Object findById(Long id);



    boolean checkPercentages(Integer agency, Integer manager);
}
