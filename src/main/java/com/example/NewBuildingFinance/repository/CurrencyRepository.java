package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long>{
    Currency findByName(String name);
}
