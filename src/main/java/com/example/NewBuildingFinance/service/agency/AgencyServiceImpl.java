package com.example.NewBuildingFinance.service.agency;

import com.example.NewBuildingFinance.dto.agency.AgencyTableDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.others.specifications.AgencySpecification;
import com.example.NewBuildingFinance.repository.AgencyRepository;
import com.example.NewBuildingFinance.service.notification.NotificationServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class AgencyServiceImpl implements AgencyService{
    private final AgencyRepository agencyRepository;
    private final NotificationServiceImpl notificationServiceImpl;

    @Override
    public Page<AgencyTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            String name,
            String director,
            String phone,
            String email,
            Integer count
    ) {
        log.info("get agency page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<Agency> specification = Specification
                .where(AgencySpecification.likeName(name))
                .and(AgencySpecification.likeDirector(director))
                .and(AgencySpecification.likePhone(phone))
                .and(AgencySpecification.likeEmail(email))
                .and(AgencySpecification.likeCount(count));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<AgencyTableDto> agencies =
                agencyRepository.findAll(specification, pageable)
                        .map(Agency::build);
        log.info("success");
        return agencies;
    }

    @Override
    public List<Agency> findAll() {
        log.info("get all agency");
        List<Agency> agencyList = agencyRepository.findAll();
        log.info("success");
        return agencyList;
    }

    @Override
    public Agency save(Agency agency) {
        log.info("save agency: {}", agency);
        Agency agencyAfterSave = agencyRepository.save(agency);
        notificationServiceImpl.createNotificationFromAgency(agencyAfterSave.getId());
        log.info("success");
        return agencyAfterSave;
    }

    @Override
    public Agency update(Agency agencyForm) {
        log.info("update agency: {}", agencyForm);
        Agency object = agencyRepository.findById(agencyForm.getId()).orElseThrow();
        object.setName(agencyForm.getName());
        object.setDescription(agencyForm.getDescription());
        agencyRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public void deleteById(Long id) {
        log.info("delete agency by id: {}", id);
        agencyRepository.deleteById(id);
        log.info("success");
    }

    @Override
    public Agency findById(Long id) {
        log.info("get agency by id: {}", id);
        Agency agency = agencyRepository.findById(id).orElseThrow();
        log.info("success");
        return agency;
    }

    @Override
    public boolean checkAgencyName(String name) {
        Agency agency;
        agency = agencyRepository.findByName(name);
        return agency != null;
    }
}
