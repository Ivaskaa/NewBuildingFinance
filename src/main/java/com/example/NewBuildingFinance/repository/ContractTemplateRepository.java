package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long>{
    List<ContractTemplate> findAllByDeletedFalse();
    List<ContractTemplate> findAllByDeletedFalseOrId(Long contractTemplateId);
    @Query("select ct from ContractTemplate ct where ct.main = true")
    ContractTemplate findMain();

    @Modifying
    @Query(value = "update newbuildingfinance.contract_templates set contract_templates.deleted = true where contract_templates.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);
}
