package com.example.NewBuildingFinance.service.realtor;

import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.repository.RealtorRepository;
import com.example.NewBuildingFinance.service.agency.AgencyServiceImpl;
import com.example.NewBuildingFinance.service.staticService.StaticServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class RealtorServiceImpl implements RealtorService{
    private final RealtorRepository realtorRepository;

    private final StaticServiceImpl staticService;

    @Override
    public Page<Realtor> findPageByAgencyId(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,
            Long id
    ) {
        log.info("get realtor page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Sort sort = staticService.sort(sortingField, sortingDirection);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<Realtor> agencies = realtorRepository.findAllByAgencyIdAndDeletedFalse(pageable, id);
        log.info("success");
        return agencies;
    }

    @Override
    public List<Realtor> findAllByAgencyIdOrRealtorId(@NotNull Long agencyId, Long realtorId) {
        List<Realtor> realtorList;
        if(realtorId == null){
            log.info("get all not deleted realtors by agencyId: {}", agencyId);
            realtorList = realtorRepository.findAllByAgencyIdAndDeletedFalse(agencyId);
            log.info("success get all not deleted realtors by agencyId");
        } else {
            log.info("get all not deleted realtors by agencyId: {} or realtorId: {}", agencyId, realtorId);
            realtorList = realtorRepository.findAllByAgencyIdAndDeletedFalseOrAgencyIdAndId(agencyId, agencyId, realtorId);
            log.info("success get all not deleted realtors by agencyId or realtorId");
        }
        return realtorList;
    }

    @Override
    public Realtor save(Realtor realtor, Long agencyId) {
        log.info("save realtor: {}", realtor);
        Agency agency = new Agency();
        agency.setId(agencyId); // fixme create dto
        realtor.setAgency(agency);
        if(realtor.isDirector()){
            Realtor latestDirector = realtorRepository.findDirectorByAgencyId(agencyId);
            if (latestDirector != null){
                latestDirector.setDirector(false);
                realtorRepository.save(latestDirector);
            }
        }
        Realtor realtorAfterSave = realtorRepository.save(realtor);
        log.info("success");
        return realtorAfterSave;
    }

    @Override
    public Realtor update(Realtor realtorForm, Long agencyId) {
        log.info("update realtor: {}", realtorForm);
        Realtor object = realtorRepository.findById(realtorForm.getId()).orElseThrow();
        object.setName(realtorForm.getName());
        object.setSurname(realtorForm.getSurname());
        object.setEmail(realtorForm.getEmail());
        object.setPhone(realtorForm.getPhone());
        object.setDirector(realtorForm.isDirector());
        if(realtorForm.isDirector()){
            Realtor latestDirector = realtorRepository.findDirectorByAgencyId(agencyId);
            if (latestDirector != null && !latestDirector.equals(object)){
                latestDirector.setDirector(false);
                realtorRepository.save(latestDirector);
            }
        }
        realtorRepository.save(object);
        log.info("success");
        return object;
    }

    @Override
    public void deleteById(Long id) {
        log.info("set realtor.deleted true by id: {}", id);
        realtorRepository.setDeleted(id);
        log.info("success set realtor.deleted true");
    }

    @Override
    public Realtor findById(Long id) {
        log.info("get realtor by id: {}", id);
        Realtor object = realtorRepository.findById(id).orElseThrow();
        log.info("success");
        return object;
    }

    @Override
    public boolean checkPhone(String phone) {
        return phone.contains("_");
    }

}
