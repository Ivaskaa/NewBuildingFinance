package com.example.NewBuildingFinance.service.contractTemplate;

import com.example.NewBuildingFinance.entities.contract.ContractTemplate;

import java.util.List;

public interface ContractTemplateService {

    List<ContractTemplate> findAll();

    ContractTemplate save(ContractTemplate object);

    ContractTemplate update(ContractTemplate objectForm);

    ContractTemplate changeMain(Long id);

    ContractTemplate findById(Long id);

    void deleteById(Long id);
}
