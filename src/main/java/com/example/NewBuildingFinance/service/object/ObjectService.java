package com.example.NewBuildingFinance.service.object;

import com.example.NewBuildingFinance.dto.object.ObjectDto;
import com.example.NewBuildingFinance.dto.object.ObjectTableDto;
import com.example.NewBuildingFinance.entities.object.Object;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface ObjectService {

    /**
     * find sorting object page
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @return page of object dto
     */
    Page<ObjectTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection
    );

    /**
     * find all objects
     * @return list of all objects
     */
    List<Object> findAll();

    /**
     * find all on sale object or by object id
     * @return list of objects
     */
    List<Object> findAllOnSaleOrObjectId(Long objectId);

    /**
     * find all on deleted false object or by object id
     * @return list of objects
     */
    List<Object> findAllDeletedFalseOrObjectId(Long objectId);

    /**
     * save object
     * @param object object
     * @return object
     */
    Object save(Object object);

    /**
     * update object
     * @param objectForm object
     * @return object
     */
    Object update(Object objectForm);

    /**
     * delete object by id
     * @param id object id
     */
    void deleteById(Long id);

    /**
     * fnd object by id
     * @param id object id
     * @return object
     */
    Object findById(Long id);

    /**
     * percentages validation
     * @param agency agency percentages
     * @param manager manager percentages
     * @return if sum greater than 100 return true
     */
    boolean checkPercentages(Integer agency, Integer manager);

    /**
     * validation for create
     * @param bindingResult binding result for exceptions
     * @param objectDto object dto
     * @return true if find exceptions
     */
    boolean validationCreateWithDatabase(BindingResult bindingResult, ObjectDto objectDto);

    /**
     * validation for update
     * @param bindingResult binding result for exceptions
     * @param objectDto object dto
     * @return true if find exceptions
     */
    boolean validationUpdateWithDatabase(BindingResult bindingResult, ObjectDto objectDto);
}
