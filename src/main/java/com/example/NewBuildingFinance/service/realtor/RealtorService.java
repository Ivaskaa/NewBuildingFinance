package com.example.NewBuildingFinance.service.realtor;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RealtorService {
    Page<Realtor> findPageByAgencyId(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,
            Long id
    );

    List<Realtor> findAllByAgencyId(Long id);

    Realtor save(Realtor realtor, Long agencyId);

    Realtor update(Realtor realtorForm, Long agencyId);

    void deleteById(Long id);

    Realtor findById(Long id);

    boolean checkPhone(String phone);
}
