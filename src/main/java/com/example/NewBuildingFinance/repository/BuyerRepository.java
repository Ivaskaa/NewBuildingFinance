package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.buyer.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long>{
    Page<Buyer> findAll(Pageable pageable);
    @Query("select b from Buyer b where b.name like :name% or b.surname like :name%")
    List<Buyer> findByName(@Param("name") String name);
    @Query("select b from Buyer b where b.name like :name% and b.surname like :surname%")
    List<Buyer> findByName(@Param("name") String name, @Param("surname") String surname);
}