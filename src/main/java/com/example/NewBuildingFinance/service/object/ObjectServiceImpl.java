package com.example.NewBuildingFinance.service.object;

import com.example.NewBuildingFinance.dto.object.ObjectDto;
import com.example.NewBuildingFinance.dto.object.ObjectTableDto;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.repository.ObjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class ObjectServiceImpl implements ObjectService{
    private final ObjectRepository objectRepository;

    @Override
    public Page<ObjectTableDto> findSortingPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection
    ) {
        log.info("get object page: {}, field: {}, direction: {}", currentPage - 1, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<ObjectTableDto> objectPage = objectRepository.findAllByDeletedFalse(pageable).map(Object::build);
        log.info("success");
        return objectPage;
    }

    @Override
    public List<Object> findAll() {
        log.info("find all objects");
        List<Object> objectPage = objectRepository.findAll();
        log.info("success find all objects");
        return objectPage;
    }

    @Override
    public List<Object> findAllOnSaleOrObjectId(Long objectId) {
        List<Object> objects;
        if(objectId != null){
            objects = objectRepository.findAllOnSaleByDeletedFalseOrId(objectId);
        } else {
            objects = objectRepository.findAllOnSaleByDeletedFalse();
        }
        return objects;
    }

    @Override
    public List<Object> findAllDeletedFalseOrObjectId(Long objectId) {
        List<Object> objects;
        if(objectId != null){
            objects = objectRepository.findAllByDeletedFalseOrId(objectId);
        } else {
            objects = objectRepository.findAllByDeletedFalse();
        }
        return objects;
    }

    @Override
    public Object save(Object object) {
        log.info("save object: {}", object);
        objectRepository.save(object);
        log.info("success save object");
        return object;
    }

    @Override
    public Object update(Object objectForm) {
        log.info("update object: {}", objectForm);
        Object object = objectRepository.findById(objectForm.getId()).orElseThrow();
        object.setHouse(objectForm.getHouse());
        object.setSection(objectForm.getSection());
        object.setAddress(objectForm.getAddress());
        object.setStatus(objectForm.getStatus());
        object.setAgency(objectForm.getAgency());
        object.setManager(objectForm.getManager());
        objectRepository.save(object);
        log.info("success update object");
        return object;
    }

    @Override
    public void deleteById(Long objectId) {
        log.info("set object.deleted true by id: {}", objectId);
        objectRepository.setDeleted(objectId);
        log.info("success set object.deleted true");
    }

    @Override
    public Object findById(Long id) {
        log.info("get object by id: {}", id);
        Object object = objectRepository.findById(id).orElseThrow();
        log.info("success get object by id");
        return object;
    }

    @Override
    public boolean checkPercentages(Integer agency, Integer manager) {
        if (agency != null && manager != null){
            return agency + manager > 100;
        }
        return false;
    }

    @Override
    public boolean validationCreateWithDatabase(BindingResult bindingResult, ObjectDto objectDto) {
        if(objectDto.getHouse() != null && !objectDto.getHouse().equals("")){
            if(objectDto.getSection() != null && !objectDto.getSection().equals("")){
                Object object = objectRepository.findByHouseAndSection(objectDto.getHouse(), objectDto.getSection());
                if (object != null){
                    bindingResult.addError(new FieldError("objectDto", "house", "House with such section already exists"));
                    bindingResult.addError(new FieldError("objectDto", "section", "House with such section already exists"));
                    return false;
                }else {
                    return true;
                }
            } else {
                return true;
            }
        }else {
            return true;
        }
    }

    @Override
    public boolean validationUpdateWithDatabase(BindingResult bindingResult, ObjectDto objectDto) {
        Object objectFirst = objectRepository.findById(objectDto.getId()).orElse(null);
        if(objectFirst == null){
            return true;
        } else {
            if (objectDto.getHouse() != null && !objectDto.getHouse().equals("")) {
                if (objectDto.getSection() != null && !objectDto.getSection().equals("")) {
                    if (!objectFirst.getHouse().equals(objectDto.getHouse()) || !objectFirst.getSection().equals(objectDto.getSection())) {
                        Object object = objectRepository.findByHouseAndSection(objectDto.getHouse(), objectDto.getSection());
                        if (object != null) {
                            bindingResult.addError(new FieldError("objectDto", "house", "House with such section already exists"));
                            bindingResult.addError(new FieldError("objectDto", "section", "House with such section already exists"));
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }
}
