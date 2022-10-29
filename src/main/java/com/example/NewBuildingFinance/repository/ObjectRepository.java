package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.object.Object;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectRepository extends JpaRepository<Object, Long>{
    Page<Object> findAll(Pageable pageable);
    @Query("select o from Object o where o.status = 'ONSALE'")
    List<Object> findAllOnSale();
}

