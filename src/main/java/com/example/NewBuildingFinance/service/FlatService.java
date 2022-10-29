package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.flat.FlatTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.others.specifications.FlatSpecification;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.repository.ObjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class FlatService {
    private final FlatRepository flatRepository;
    private final ObjectRepository objectRepository;

    public Page<FlatTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Optional<Integer> number,
            Optional<Long> objectId,
            Optional<String> status,
            Optional<Integer> areaStart,
            Optional<Integer> areaFin,
            Optional<Integer> priceStart,
            Optional<Integer> priceFin,
            Optional<Integer> advanceStart,
            Optional<Integer> advanceFin
    ) {
        Object object = null;
        if (objectId.isPresent()) {
            object = objectRepository.findById(objectId.get()).orElse(null);
        }
        log.info("get flat page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<Flat> specification = Specification
                .where(FlatSpecification.likeNumber(number.orElse(null)))
                .and(FlatSpecification.likeObject(object))
                .and(FlatSpecification.likeStatus(status.orElse(null)))
                .and(FlatSpecification.likeArea(areaStart.orElse(null), areaFin.orElse(null)))
                .and(FlatSpecification.likePrice(priceStart.orElse(null), priceFin.orElse(null)))
                .and(FlatSpecification.likeAdvance(advanceStart.orElse(null), advanceFin.orElse(null)));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<FlatTableDto> flats = flatRepository.findAll(specification, pageable).map(Flat::buildTableDto);
        log.info("success");
        return flats;
    }

    public Flat save(Flat flat) {
        log.info("save flat: {}", flat);
        Flat flatAfterSave = flatRepository.save(flat);
        log.info("success");
        return flatAfterSave;
    }

    public Flat update(Flat flatForm) {
        log.info("update object: {}", flatForm);
        Flat object = flatRepository.findById(flatForm.getId()).orElseThrow();
        object.setObject(flatForm.getObject());
        object.setStatus(flatForm.getStatus());
        object.setArea(flatForm.getArea());
        object.setPrice(flatForm.getPrice());
        object.setSalePrice(flatForm.getSalePrice());
        object.setAdvance(flatForm.getAdvance());
        object.setNumber(flatForm.getNumber());
        object.setQuantityRooms(flatForm.getQuantityRooms());
        object.setFloor(flatForm.getFloor());
        object.setDescription(flatForm.getDescription());
        object.setAgency(flatForm.getAgency());
        object.setManager(flatForm.getManager());
        object.setBuyer(flatForm.getBuyer());
        object.setRealtor(flatForm.getRealtor());
        flatRepository.save(object);
        log.info("success");
        return object;
    }

    public void deleteById(Long id) {
        log.info("delete flat by id: {}", id);
        flatRepository.deleteById(id);
        log.info("success");
    }

    public Flat findById(Long id) {
        log.info("get flat by id: {}", id);
        Flat flat = flatRepository.findById(id).orElseThrow();
        log.info("success");
        return flat;
    }

    public Flat findByContractId(Long id) {
        log.info("get flat by contract id: {}", id);
        Flat flat = flatRepository.findFlatByContractId(id).orElseThrow();
        log.info("success");
        return flat;
    }

    public List<Flat> getFlatsWithoutContractByObjectId(Long id, Long flatId) {
        log.info("get flats by object id: {} and without contract", id);
        List<Flat> flats;
        if(flatId == null) {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNullAndStatus(id, StatusFlat.ACTIVE);
        } else {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNullAndStatusOrId(id, StatusFlat.ACTIVE, flatId);
        }
        log.info("success");
        return flats;
    }

    public boolean checkPrice(Integer price, Integer salePrice) {
        // if price > salePrice return false
        return price < salePrice;
    }

    public boolean checkPercentages(Integer agency, Integer manager) {
        if (agency == null || manager == null){
            return false;
        }
        return agency + manager > 100;
    }

    public boolean checkFlatNumber(Integer number, Long objectId) {
        Flat flat;
        flat = flatRepository.findFlatInObject(number, objectId);
        return flat != null;
    }

    public boolean checkStatus(StatusFlat status) {
        return !status.equals(StatusFlat.ACTIVE);
    }
}
