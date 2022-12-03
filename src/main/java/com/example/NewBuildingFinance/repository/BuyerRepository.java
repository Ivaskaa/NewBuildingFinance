package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.buyer.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long>{
    Page<Buyer> findAllByDeletedFalse(Pageable pageable);
    @Query("select b from Buyer b where b.name like :name% and b.deleted = false or b.surname like :name% and b.deleted = false")
    List<Buyer> findByNameAndDeletedFalse(@Param("name") String name);
    @Query("select b from Buyer b where b.name like :name% and b.surname like :surname% and b.deleted = false")
    List<Buyer> findByNameAndDeletedFalse(@Param("name") String name, @Param("surname") String surname);

    @Modifying
    @Query(value = "update newbuildingfinance.buyers set buyers.deleted = true where buyers.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);
}