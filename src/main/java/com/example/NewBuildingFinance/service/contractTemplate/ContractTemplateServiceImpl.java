package com.example.NewBuildingFinance.service.contractTemplate;

import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import com.example.NewBuildingFinance.repository.ContractTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class ContractTemplateServiceImpl implements ContractTemplateService{
    private final ContractTemplateRepository contractTemplateRepository;

    @Override
    public List<ContractTemplate> findAllDeletedFalseOrById(Long contractTemplateId) {
        List<ContractTemplate> contractTemplates;
        if(contractTemplateId != null){
            log.info("get all deleted false contract templates or by contract template id: {}", contractTemplateId);
            contractTemplates = contractTemplateRepository.findAllByDeletedFalseOrId(contractTemplateId);
        } else {
            log.info("get all deleted false contract templates");
            contractTemplates = contractTemplateRepository.findAllByDeletedFalse();
        }
        log.info("success get all deleted false contract templates");
        return contractTemplates;
    }

    @Override
    public ContractTemplate save(ContractTemplate contractTemplate) {
        log.info("save contract template: {}", contractTemplate);
        contractTemplate = contractTemplateRepository.save(contractTemplate);
        log.info("success");
        return contractTemplate;
    }

    @Override
    public ContractTemplate update(ContractTemplate contractTemplate) {
        log.info("update contract template: {}", contractTemplate);
        ContractTemplate object = contractTemplateRepository.findById(contractTemplate.getId()).orElseThrow();
        object.setName(contractTemplate.getName());
        object.setText(contractTemplate.getText());
        contractTemplateRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public ContractTemplate changeMain(Long id) {
        log.info("change main contract template by id: {}", id);
        ContractTemplate object = contractTemplateRepository.findById(id).orElseThrow();
        ContractTemplate latestMain = contractTemplateRepository.findMain();
        object.setMain(true);
        if (latestMain != null && !latestMain.equals(object)){
            latestMain.setMain(false);
            contractTemplateRepository.save(latestMain);
        }
        contractTemplateRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public ContractTemplate findById(Long id) {
        log.info("get contract template by id: {}", id);
        ContractTemplate object = contractTemplateRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }

    @Override
    public void deleteById(Long id) {
        log.info("set contract_template.deleted true by id: {}", id);
        contractTemplateRepository.setDeleted(id);
        log.info("success set contract_template.deleted true");
    }

}
