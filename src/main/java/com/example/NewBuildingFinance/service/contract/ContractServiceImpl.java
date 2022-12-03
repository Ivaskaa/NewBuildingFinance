package com.example.NewBuildingFinance.service.contract;

import com.example.NewBuildingFinance.dto.cashRegister.CommissionTableDtoForAgency;
import com.example.NewBuildingFinance.dto.contract.*;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.contract.ContractStatus;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.others.specifications.ContractSpecification;
import com.example.NewBuildingFinance.repository.CashRegisterRepository;
import com.example.NewBuildingFinance.repository.ContractRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.repository.ObjectRepository;
import com.example.NewBuildingFinance.service.notification.NotificationServiceImpl;
import com.example.NewBuildingFinance.service.setting.SettingServiceImpl;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;

@Service
@Log4j2
@AllArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final FlatRepository flatRepository;
    private final ObjectRepository objectRepository;
    private final CashRegisterRepository cashRegisterRepository;

    private final NotificationServiceImpl notificationServiceImpl;
    private final SettingServiceImpl settingService;

    @Override
    public Page<ContractTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Long id,
            String dateStart,
            String dateFin,
            Integer flatNumber,
            Long objectId,
            String buyerName,
            String comment
    ) throws ParseException {
        log.info("get contract page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<Contract> specification = Specification
                .where(ContractSpecification.likeId(id))
                .and(ContractSpecification.likeDate(dateStart, dateFin))
                .and(ContractSpecification.likeObject(objectId))
                .and(ContractSpecification.likeFlatNumber(flatNumber))
                .and(ContractSpecification.likeBuyerName(buyerName))
                .and(ContractSpecification.likeComment(comment))
                .and(ContractSpecification.likeDeletedFalse());
//                .and(ContractSpecification.likeAdvance(advanceStart, advanceFin));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<ContractTableDto> contracts = contractRepository.findAll(specification, pageable).map(Contract::buildTableDto);
        log.info("success");
        return contracts;
    }

    @Override
    public Page<ContractTableDtoForBuyers> findSortingPageByBuyerId(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,
            Long buyerId
    ) {
        log.info("get contract page for buyer id: {} page: {}, size: {} field: {}, direction: {}",
                buyerId, currentPage - 1, size, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<ContractTableDtoForBuyers> contracts
                = contractRepository.findAllByBuyerIdAndDeletedFalse(pageable, buyerId)
                .map(Contract::buildTableDtoForBuyers);
        log.info("success");
        return contracts;
    }

    @Override
    public Page<ContractTableDtoForAgency> findSortingContractsPageByAgencyId(
            Integer currentPage, Integer size, String sortingField, String sortingDirection,
            Long agencyId
    ) {
        log.info("get contract page for agency id: {} page: {}, size: {} field: {}, direction: {}",
                agencyId, currentPage - 1, size, sortingField, sortingDirection);
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<ContractTableDtoForAgency> contracts
                = contractRepository.findAllByFlatRealtorAgencyIdAndDeletedFalse(pageable, agencyId)
                .map(Contract::buildTableDtoForAgency);
        log.info("success");
        return contracts;
    }
    public Page<CommissionTableDtoForAgency> findSortingCommissionsPageByAgencyId(
            Integer page, Integer size, String field, String direction,
            Long agencyId) {
        log.info("get contract page for agency id: {} page: {}, size: {} field: {}, direction: {}",
                agencyId, page - 1, size, field, direction);
        Sort sort = Sort.by(Sort.Direction.valueOf(direction), field);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<CommissionTableDtoForAgency> commissions
                = cashRegisterRepository.findAllByFlatRealtorAgencyIdAndDeletedFalse(pageable, agencyId)
                .map(CashRegister::buildCommissionTableDtoForAgency);
        log.info("success");
        return commissions;
    }

    @Override
    public ContractUploadDto save(ContractSaveDto contractSaveDto) throws ParseException {
        log.info("save contract: {}", contractSaveDto);
        Contract contract = contractSaveDto.build();
        Object object = objectRepository.findById(contractSaveDto.getObjectId()).orElseThrow();
        contract.setObject(object.getHouse() + "(" + object.getSection() + ")");
        Flat flat = flatRepository.findById(contractSaveDto.getFlatId()).orElseThrow();
        contract.setFlat(flat);
        Contract contractAfterSave = contractRepository.save(contract);
        flat.setContract(contractAfterSave);
        flat.setBuyer(contractAfterSave.getBuyer());
        if(flat.getRealtor() == null){
            flat.setRealtor(flat.getBuyer().getRealtor());
        }
        flat.setStatus(StatusFlat.SOLD);
        flatRepository.save(flat);
        ContractUploadDto contractUploadDto = contractAfterSave.buildUploadDto();
        contractUploadDto.setFlat(flat);
        contractUploadDto.setObject(flat.getObject());

        notificationServiceImpl.createNotificationFromContract(contractAfterSave);

        log.info("success");
        return contractUploadDto;
    }

    @Override
    public ContractUploadDto update(ContractSaveDto contractSaveDto) {
        log.info("update contract: {}", contractSaveDto);
        Contract contract = contractSaveDto.build();

        Object object = objectRepository.findById(contractSaveDto.getObjectId()).orElseThrow();
        contract.setObject(object.getHouse() + "(" + object.getSection() + ")");
        Flat latestFlat = flatRepository.findFlatByContractId(contractSaveDto.getId()).orElseThrow();
        Flat flat = flatRepository.findById(contractSaveDto.getFlatId()).orElseThrow();
        if(!flat.equals(latestFlat)){
            latestFlat.setContract(null);
        }
        contract.setFlat(flat);
        Contract contractAfterSave = contractRepository.save(contract);
        flat.setContract(contractAfterSave);
        flat.setBuyer(contractAfterSave.getBuyer());
        if(flat.getRealtor() == null){
            flat.setRealtor(flat.getBuyer().getRealtor());
        }
        flatRepository.save(flat);
        ContractUploadDto contractUploadDto = contractAfterSave.buildUploadDto();
        contractUploadDto.setFlat(flat);
        contractUploadDto.setObject(flat.getObject());

        notificationServiceImpl.updateNotificationFromContract(contractAfterSave);

        log.info("success update contract");
        return contractUploadDto;
    }

    @Override
    public void deleteById(Long contractId) {
        log.info("delete contract by id: {}", contractId);
        Flat flat = flatRepository.findFlatByContractId(contractId).orElse(null);
        if (flat != null) {
            flat.setContract(null);
            flat.setStatus(StatusFlat.ACTIVE);
            flatRepository.save(flat);
        }
        contractRepository.setDeleted(contractId);
        notificationServiceImpl.deleteNotificationByContractId(contractId);
        log.info("success delete contract by id");
    }

    @Override
    public ContractUploadDto findById(Long id) {
        log.info("get contract by id: {}", id);
        Contract contract = contractRepository.findById(id).orElseThrow();
        ContractUploadDto contractUploadDto = contract.buildUploadDto();
        Flat flat = flatRepository.findFlatByContractId(contract.getId()).orElse(null);
        if (flat != null) {
            contractUploadDto.setFlat(flat);
            contractUploadDto.setObject(flat.getObject());
        }
        log.info("success get contract by id");
        return contractUploadDto;
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Long id) throws IOException {
        log.info("get pdf for contract id: {}", id);
        Contract contract = contractRepository.findById(id).orElseThrow();

        String html = contract.getContractTemplate().getText();
        html = html.replace("%BUYERNAME%", contract.getName());
        html = html.replace("%BUYERSURNAME%", contract.getSurname());
        html = html.replace("%BUYERLASTNAME%", contract.getLastname());
        html = html.replace("%BUYERPHONE%", contract.getPhone());
        html = html.replace("%BUYEREMAIL%", contract.getEmail());
        html = html.replace("%FLATNUMBER%", contract.getFlatNumber().toString());
        html = html.replace("%FLATFLOR%", contract.getFlatFloor().toString());
        html = html.replace("%FLATPRICE%", contract.getPrice().toString());
        html = html.replace("%FLATAREA%", contract.getFlatArea().toString());
        html = html.replace("%FLATADDRESS%", contract.getFlatAddress());
        html = html.replace("%CONTRACTNUMBER%", contract.getId().toString());
        html = html.replace("%OBJECT%",
                contract.getFlat().getObject().getHouse() +
                        "(" + contract.getFlat().getObject().getSection() + ")");
        html = html.replace("%REALTOR%",
                contract.getFlat().getRealtor().getSurname() +
                        " " + contract.getFlat().getRealtor().getName());
        html = html.replace("%AGENCY%", contract.getFlat().getRealtor().getAgency().getName());

        html += settingService.getSettings().getPdfFooter();

        File file = new File("src/main/resources/contract.pdf");
        PdfWriter pdfWriter = new PdfWriter(file);
        HtmlConverter.convertToPdf(html, pdfWriter);
        byte[] content = Files.readAllBytes(file.toPath());
        String filename = contract.getContractTemplate().getName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(filename + ".pdf", filename + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("success get pdf for contract");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Override
    public boolean checkRealtor(Long flatId) {
        if(flatId != null) {
            Flat flat = flatRepository.findById(flatId).orElse(null);
            assert flat != null;
            return flat.getRealtor() == null;
        } else return false;
    }

    @Override
    public boolean checkFlatPayments(Long flatId) {
        if(flatId != null) {
            Flat flat = flatRepository.findById(flatId).orElse(null);
            if (flat == null){
                return false;
            }
            Double price = flat.getSalePrice();
            for(FlatPayment flatPayment : flat.getFlatPayments()){
                price -= flatPayment.getPlanned();
            }
            return price != 0;
        } else return false;
    }

    @Override
    public boolean checkContract(Long id) {
        Contract contract = null;
        if (id != null) {
            contract = contractRepository.findById(id).orElse(null);
        }
        return contract == null;
    }
}