package com.example.NewBuildingFinance.service.contractTemplate;

import com.example.NewBuildingFinance.entities.contract.ContractTemplate;

import java.util.List;

public interface ContractTemplateService {

    /**
     * find all contract templates
     * @return list of contract templates
     */
    List<ContractTemplate> findAll();

    /**
     * save new contract template
     * @param contractTemplate contract template entity
     * @return contract template after save
     */
    ContractTemplate save(ContractTemplate contractTemplate);

    /**
     * update old contract template
     * @param contractTemplate contract template entity
     * @return contract template after save
     */
    ContractTemplate update(ContractTemplate contractTemplate);

    /**
     * contract template with choose id will be main and others contract templates will be common
     * @param id id of contract template
     * @return main contract template
     */
    ContractTemplate changeMain(Long id);

    /**
     * find contract template by id
     * @param id contract template id
     * @return contract template
     */
    ContractTemplate findById(Long id);

    /**
     * delete contract template by id
     * @param id contract template id for delete
     */
    void deleteById(Long id);
}
