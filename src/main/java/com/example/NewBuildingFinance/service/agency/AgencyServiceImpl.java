package com.example.NewBuildingFinance.service.agency;

import com.example.NewBuildingFinance.dto.agency.AgencyTableDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.others.specifications.AgencySpecification;
import com.example.NewBuildingFinance.repository.AgencyRepository;
import com.example.NewBuildingFinance.service.notification.NotificationServiceImpl;
import com.example.NewBuildingFinance.service.staticService.StaticServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class AgencyServiceImpl implements AgencyService{
    private final AgencyRepository agencyRepository;
    private final NotificationServiceImpl notificationServiceImpl;
    private final StaticServiceImpl staticService;

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
                .and(AgencySpecification.likeCount(count))
                .and(AgencySpecification.deletedFalse());
        Sort sort = staticService.sort(sortingField, sortingDirection);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<AgencyTableDto> agencies =
                agencyRepository.findAll(specification, pageable)
                        .map(Agency::build);
        log.info("success");
        return agencies;
    }

    @Override
    public List<Agency> findAllByDeletedFalseOrId(Long agencyId) {
        log.info("get all agency");
        List<Agency> agencyList = agencyRepository.findAllByDeletedFalseOrId(agencyId);
        log.info("success");
        return agencyList;
    }

    public List<Agency> findAllByDeletedFalse(Long agencyId) {
        List<Agency> agencies;
        if(agencyId != null){
            log.info("get all not deleted agencies or by agency id: {}", agencyId);
            agencies = agencyRepository.findAllByDeletedFalseOrId(agencyId);
        } else {
            log.info("get all not deleted agencies");
            agencies = agencyRepository.findAllByDeletedFalse();
        }
        log.info("success get all not deleted agencies");
        return agencies;
    }

    @Override
    public Agency save(Agency agency) {
        log.info("save agency: {}", agency);
        Agency agencyAfterSave = agencyRepository.save(agency);
        notificationServiceImpl.createNotificationFromAgency(agencyAfterSave);
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
    public void deleteById(Long agencyId) {
        log.info("set agency.delete true by id: {}", agencyId);
        agencyRepository.setDeleted(agencyId);
        notificationServiceImpl.deleteNotificationByAgencyId(agencyId);
        log.info("success set agency.delete true");
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
        agency = agencyRepository.findByNameAndDeletedFalse(name);
        return agency != null;
    }
}
