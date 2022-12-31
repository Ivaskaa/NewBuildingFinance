package com.example.NewBuildingFinance;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.auth.Permission;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.others.specifications.AgencySpecification;
import com.example.NewBuildingFinance.others.specifications.FlatSpecification;
import com.example.NewBuildingFinance.repository.AgencyRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.service.agency.AgencyService;
import com.example.NewBuildingFinance.service.buyer.BuyerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@SpringBootTest
class NewBuildingFinanceApplicationTests {

	@Autowired
	BuyerServiceImpl buyerService;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Test
	void contextLoads() {
//		Buyer buyer = new Buyer();
//		buyer.setPassword(bCryptPasswordEncoder.encode("$EogSt_2R4"));
//		buyer.setEmail("password@gmail.com");
//		buyer.setPermission(new Permission(9L, "API"));
//		buyer.setDeleted(false);
//		buyer.setActive(true);
//		buyer.setName("name");
//		buyerService.save(buyer);

//		Specification<Flat> specification = Specification
//				.where(FlatSpecification.likeRemains(43000, 50000));
//
//		Page<Flat> result = repository.findAll(specification, PageRequest.of(0, 10));
//
//		System.out.println("size:"+result.getContent().size());
//		result.getContent().forEach(a -> System.out.println(a.getId()));
//		result.getContent().forEach(a -> System.out.println(a.getRemains()));
	}

}
