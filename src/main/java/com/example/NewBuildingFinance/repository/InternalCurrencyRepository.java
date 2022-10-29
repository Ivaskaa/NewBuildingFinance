package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternalCurrencyRepository extends JpaRepository<InternalCurrency, Long>{
    List<InternalCurrency> findAll();
}