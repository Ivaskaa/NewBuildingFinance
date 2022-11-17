package com.example.NewBuildingFinance.service.buyer;

import com.example.NewBuildingFinance.dto.buyer.BuyerTableDto;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BuyerService {
    /**
     * find sorting page for buyer table from database
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @return page of buyer table dto
     */
    Page<BuyerTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection);

    /**
     * save new buyer in database
     * @param buyer buyer model
     * @return buyer after save
     */
    Buyer save(Buyer buyer);

    /**
     * update old buyer in database
     * @param objectForm buyer model
     * @return buyer after save
     */
    Buyer update(Buyer objectForm);

    /**
     * delete buyer by id from database
     * @param id id for delete
     */
    void deleteById(Long id);

    /**
     * find buyer by id from database
     * @param id id for find
     * @return buyer
     */
    Buyer findById(Long id);

    /**
     * find buyer by name from database
     * @param name buyer name
     * @return buyer
     */
    List<Buyer> findByName(String name);
}
