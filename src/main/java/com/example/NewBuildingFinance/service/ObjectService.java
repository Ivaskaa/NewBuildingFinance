package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.ObjectTableDto;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.repository.ObjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class ObjectService {
    private final ObjectRepository objectRepository;

    public Page<ObjectTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection) {
        log.info("get object page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<ObjectTableDto> objectPage = objectRepository.findAll(pageable).map(Object::build);
        log.info("success");
        return objectPage;
    }

    public List<Object> findAll() {
        List<Object> objectPage = objectRepository.findAll();
        log.info("success");
        return objectPage;
    }

    public Object save(Object object) {
        log.info("save object: {}", object);
        objectRepository.save(object);
        log.info("success");
        return object;
    }

    public Object update(Object objectForm) {
        log.info("update object: {}", objectForm);
        Object object = objectRepository.findById(objectForm.getId()).orElseThrow();
        object.setHouse(objectForm.getHouse());
        object.setSection(objectForm.getSection());
        object.setAddress(objectForm.getAddress());
        object.setStatus(objectForm.getStatus());
        object.setAgency(objectForm.getAgency());
        object.setManager(objectForm.getManager());
        object.setActive(objectForm.isActive());
        objectRepository.save(object);
        log.info("success");
        return object;
    }

    public void deleteById(Long id) {
        log.info("delete object by id: {}", id);
        objectRepository.deleteById(id);
        log.info("success");
    }

    public Object findById(Long id) {
        log.info("get object by id: {}", id);
        Object object = objectRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }

    public boolean checkPercentages(Object object) {
        if (object.getAgency()!= null && object.getManager() != null){
            int agency = object.getAgency();
            int manager = object.getManager();
            return agency + manager > 100;
        }
        return false;
    }

    public List<Object> findAllOnSale() {
        return objectRepository.findAllOnSale();
    }
}
