package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.repository.ContractTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class ContractTemplateService {
    private final ContractTemplateRepository repository;

    public List<ContractTemplate> findAll() {
        log.info("get all contract templates");
        List<ContractTemplate> currencyList = repository.findAllByDeletedFalse();
        log.info("success");
        return currencyList;
    }

    public ContractTemplate save(ContractTemplate object) {
        log.info("save contract template: {}", object);
        object = repository.save(object);
        log.info("success");
        return object;
    }

    public ContractTemplate update(ContractTemplate objectForm) {
        log.info("update contract template: {}", objectForm);
        ContractTemplate object = repository.findById(objectForm.getId()).orElseThrow();
        object.setName(objectForm.getName());
        object.setText(objectForm.getText());
        repository.save(object);
        log.info("success");
        return object;
    }

    public ContractTemplate changeMain(Long id) {
        log.info("change main contract template by id: {}", id);
        ContractTemplate object = repository.findById(id).orElseThrow();
        ContractTemplate latestMain = repository.findMain();
        object.setMain(true);
        if (latestMain != null && !latestMain.equals(object)){
            latestMain.setMain(false);
            repository.save(latestMain);
        }
        repository.save(object);
        log.info("success");
        return object;
    }

    public ContractTemplate findById(Long id) {
        log.info("get contract template by id: {}", id);
        ContractTemplate object = repository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }

    public void deleteById(Long id) {
        log.info("delete contract template by id: {}", id);
        ContractTemplate object = repository.findById(id).orElseThrow();
        object.setDeleted(true);
        repository.save(object);
        log.info("success");
    }

}
