package com.example.NewBuildingFinance;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.others.specifications.AgencySpecification;
import com.example.NewBuildingFinance.others.specifications.FlatSpecification;
import com.example.NewBuildingFinance.repository.AgencyRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.service.agency.AgencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@SpringBootTest
class NewBuildingFinanceApplicationTests {

	@Autowired
	FlatRepository repository;

	@Test
	void contextLoads() {
		Specification<Flat> specification = Specification
				.where(FlatSpecification.likeRemains(43000, 50000));

		Page<Flat> result = repository.findAll(specification, PageRequest.of(0, 10));

		System.out.println("size:"+result.getContent().size());
		result.getContent().forEach(a -> System.out.println(a.getId()));
		result.getContent().forEach(a -> System.out.println(a.getRemains()));
	}

}
