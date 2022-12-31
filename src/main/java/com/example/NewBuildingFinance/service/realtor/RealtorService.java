package com.example.NewBuildingFinance.service.realtor;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import org.springframework.data.domain.Page;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface RealtorService {
    /**
     * find realtor page by agency id
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param id agency id
     * @return page of realtors
     */
    Page<Realtor> findPageByAgencyId(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,
            Long id
    );

    /**
     * find all by agency id and deleted false or agencyId and id
     * @param agencyId agency id
     * @param realtorId realtor id
     * @return list of realtors
     */
    List<Realtor> findAllByAgencyIdOrRealtorId(@NotNull Long agencyId, Long realtorId);

    /**
     * save realtor
     * @param realtor realtor
     * @param agencyId agency id
     * @return realtor
     */
    Realtor save(Realtor realtor, Long agencyId);

    /**
     * update realtor
     * @param realtorForm realtor
     * @param agencyId agency id
     * @return realtor
     */
    Realtor update(Realtor realtorForm, Long agencyId);

    /**
     * delete realtor by id
     * @param id realtor id
     */
    void deleteById(Long id);

    /**
     * find realtor by id
     * @param id realtor id
     * @return realtor
     */
    Realtor findById(Long id);

    /**
     * phone validation
     * without database request
     * @param phone phone
     * @return if validation failed return true
     */
    boolean checkPhone(String phone);
}
