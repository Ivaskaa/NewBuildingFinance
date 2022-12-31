package com.example.NewBuildingFinance.service.agency;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.others.specifications.AgencySpecification;
import com.example.NewBuildingFinance.repository.AgencyRepository;
import com.example.NewBuildingFinance.service.notification.NotificationService;
import com.example.NewBuildingFinance.service.staticService.StaticService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgencyServiceTest {
    @Autowired
    private AgencyService agencyService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private StaticService staticService;

    @MockBean
    private AgencyRepository agencyRepository;

    @Test
    void findSortingAndSpecificationPage() {
        Specification<Agency> specification = Specification
                .where(AgencySpecification.likeName(""))
                .and(AgencySpecification.likeDirector(""))
                .and(AgencySpecification.likePhone(""))
                .and(AgencySpecification.likeEmail(""))
                .and(AgencySpecification.likeCount(null))
                .and(AgencySpecification.deletedFalse());

        Agency agency = new Agency();
        agency.setId(1L);
        agency.setName("name");
        agency.setDirectorPhone("phone");
        agency.setDirectorName("name");
        agency.setDirectorEmail("email");
        agency.setCountContracts(1);

        Page<Agency> agencies = new PageImpl<>(List.of(agency));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        Mockito.doReturn(agencies)
                .when(agencyRepository)
                .findAll(specification, pageable);

        agencyService.findSortingAndSpecificationPage(
                1,
                10,
                "id",
                "ASC",
                "",
                "",
                "",
                "",
                null
        );

        Mockito.verify(agencyRepository,
                Mockito.times(1))
                .findAll(specification, pageable);
    }

    @Test
    void findAllByDeletedFalse() {
        Agency agency = new Agency();
        agency.setId(1L);
        agency.setName("name");
        agency.setDirectorPhone("phone");
        agency.setDirectorName("name");
        agency.setDirectorEmail("email");
        agency.setCountContracts(1);

        Mockito.doReturn(List.of(agency))
                .when(agencyRepository)
                .findAllByDeletedFalse();

        List<Agency> agencies = (agencyService.findAllByDeletedFalse(null));

        agencyService.findAllByDeletedFalse(1L);

        Mockito.verify(agencyRepository,
                        Mockito.times(1))
                .findAllByDeletedFalseOrId(1L);
        Mockito.verify(agencyRepository,
                        Mockito.times(1))
                .findAllByDeletedFalse();

        Assertions.assertEquals("name", agencies.get(0).getName());
        Assertions.assertEquals("phone", agencies.get(0).getDirectorPhone());
        Assertions.assertEquals(1, agencies.get(0).getCountContracts());

//        Mockito.verify(service, Mockito.times(1))
//                .savePhoto(
//                        ArgumentMatchers.eq("education"),
//                        ArgumentMatchers.eq(file),
//                        ArgumentMatchers.contains(file.getOriginalFilename())
//                );
    }

    @Test
    void save() {
        Agency agency = new Agency();
        agency.setId(1L);
        agency.setName("name");
        agency.setDirectorPhone("phone");
        agency.setDirectorName("name");
        agency.setDirectorEmail("email");
        agency.setCountContracts(1);

        Mockito.doReturn(agency)
                .when(agencyRepository)
                .save(agency);

        agency = agencyService.save(agency);

        Mockito.verify(agencyRepository,
                        Mockito.times(1))
                .save(agency);

        Assertions.assertEquals("name", agency.getName());
        Assertions.assertEquals("phone", agency.getDirectorPhone());
        Assertions.assertEquals(1, agency.getCountContracts());
    }

    @Test
    void update() {
        Agency agency = new Agency();
        agency.setId(1L);
        agency.setName("name");
        agency.setDirectorPhone("phone");
        agency.setDirectorName("name");
        agency.setDirectorEmail("email");
        agency.setCountContracts(1);

        Agency agencyUpdate = new Agency();
        agencyUpdate.setId(1L);
        agencyUpdate.setName("nameUpdate");
        agencyUpdate.setDescription("descriptionUpdate");
        agencyUpdate.setCountContracts(1);

        Mockito.doReturn(agencyUpdate)
                .when(agencyRepository)
                .save(agencyUpdate);

        Mockito.doReturn(agency)
                .when(agencyRepository)
                .findById(1L);

        agencyService.update(agency);

//        Mockito.doReturn()
    }

    @Test
    void deleteById() {
    }

    @Test
    void findById() {
    }

    @Test
    void checkAgencyName() {
    }
}