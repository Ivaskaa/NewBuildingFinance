package com.example.NewBuildingFinance.repository;

import com.example.NewBuildingFinance.entities.object.Object;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Repository
public interface ObjectRepository extends JpaRepository<Object, Long>{
    Page<Object> findAllByDeletedFalse(Pageable pageable);

    @Query("select o from Object o where o.status = 'ONSALE' and o.deleted = false")
    List<Object> findAllOnSaleByDeletedFalse();

    @Query("select o from Object o where o.status = 'ONSALE' and o.deleted = false or o.id = :objectId")
    List<Object> findAllOnSaleByDeletedFalseOrId(@Param("objectId") Long objectId);

    @Modifying
    @Query(value = "update objects set objects.deleted = true where objects.id = :id", nativeQuery = true)
    @Transactional
    void setDeleted(@Param("id") Long id);

    Object findByHouseAndSection(@NotEmpty String house, @NotEmpty String section);

    List<Object> findAllByDeletedFalseOrId(Long objectId);

    List<Object> findAllByDeletedFalse();
}

