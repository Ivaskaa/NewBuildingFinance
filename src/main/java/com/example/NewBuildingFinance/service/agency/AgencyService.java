package com.example.NewBuildingFinance.service.agency;

import com.example.NewBuildingFinance.dto.agency.AgencyTableDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AgencyService {

    /**
     * sorting specification request for agency table
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param name agency name
     * @param director agency director (name, surname)
     * @param phone agency director phone
     * @param email agency director email
     * @param count count og agency sales
     * @return page of agency table dto
     */
    Page<AgencyTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            String name,
            String director,
            String phone,
            String email,
            Integer count
    );

    /**
     * find all agencies
     * @return list of agency
     */
    List<Agency> findAllByDeletedFalseOrId(Long agencyId);

    /**
     * save new agency
     * @param agency agency model
     * @return agency after save
     */
    Agency save(Agency agency);

    /**
     * update old agency
     * @param agencyForm agency model
     * @return agency after save
     */
    Agency update(Agency agencyForm);

    /**
     * delete agency by id
     * @param id id for delete
     */
    void deleteById(Long id);

    /**
     * find agency by id
     * @param id id for find
     * @return agency object
     */
    Agency findById(Long id);

    /**
     * in database check unique agency name
     * @param name agency name
     * @return if name is exist return true
     */
    boolean checkAgencyName(String name);
}

