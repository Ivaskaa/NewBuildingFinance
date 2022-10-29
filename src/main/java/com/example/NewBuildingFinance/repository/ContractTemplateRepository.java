package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.contract.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long>{
    List<ContractTemplate> findAllByDeletedFalse();
    @Query("select ct from ContractTemplate ct where ct.main = true")
    ContractTemplate findMain();
}
