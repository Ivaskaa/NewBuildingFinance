package com.example.NewBuildingFinance.service.contract;

import com.example.NewBuildingFinance.dto.cashRegister.CommissionTableDtoForAgency;
import com.example.NewBuildingFinance.dto.contract.*;
import com.example.NewBuildingFinance.entities.buyer.DocumentStyle;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.others.specifications.ContractSpecification;
import com.example.NewBuildingFinance.repository.CashRegisterRepository;
import com.example.NewBuildingFinance.repository.ContractRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.service.notification.NotificationServiceImpl;
import com.example.NewBuildingFinance.service.setting.SettingServiceImpl;
import com.example.NewBuildingFinance.service.staticService.StaticServiceImpl;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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
    private final CashRegisterRepository cashRegisterRepository;

    private final NotificationServiceImpl notificationServiceImpl;
    private final SettingServiceImpl settingService;
    private final StaticServiceImpl staticService;

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
        Sort sort = staticService.sort(sortingField, sortingDirection);
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
        log.info("success get contract page for buyer");
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
        log.info("success get contract page for agency");
        return contracts;
    }

    public Page<CommissionTableDtoForAgency> findSortingCommissionsPageByAgencyId(
            Integer page, Integer size, String field, String direction,
            Long agencyId) {
        log.info("get commission page for agency id: {} page: {}, size: {} field: {}, direction: {}",
                agencyId, page - 1, size, field, direction);
        Sort sort = Sort.by(Sort.Direction.valueOf(direction), field);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<CommissionTableDtoForAgency> commissions
                = cashRegisterRepository.findAllByFlatRealtorAgencyIdAndDeletedFalse(pageable, agencyId)
                .map(CashRegister::buildCommissionTableDtoForAgency);
        log.info("success get commission page for agency");
        return commissions;
    }

    @Override
    public ContractUploadDto save(ContractSaveDto contractSaveDto) throws ParseException {
        log.info("save contract: {}", contractSaveDto);
        Contract contract = contractSaveDto.build();
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

        notificationServiceImpl.createNotificationFromContract(contractAfterSave);

        log.info("success save contract");
        return contractUploadDto;
    }

    @Override
    public ContractUploadDto update(ContractSaveDto contractSaveDto) {
        log.info("update contract: {}", contractSaveDto);
        Contract contract = contractSaveDto.build();
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

    public void checkDocument(BindingResult bindingResult, ContractSaveDto contractSaveDto) {
        if(contractSaveDto.getDocumentStyle() == null || contractSaveDto.getDocumentStyle().equals("")){
            return;
        }
        DocumentStyle documentStyle = null;
        try {
            documentStyle = DocumentStyle.valueOf(contractSaveDto.getDocumentStyle());
        } catch (Exception exception){
            exception.printStackTrace();
        }
        if(documentStyle != null) {
            if (documentStyle.equals(DocumentStyle.ID_CARD)) {
                if (contractSaveDto.getIdCardNumber() == null) {
                    bindingResult.addError(new FieldError("contractSaveDto", "idCardNumber", "Must not be empty"));
                } else if (contractSaveDto.getIdCardNumber() > 9999999999999L || contractSaveDto.getIdCardNumber() <= 999999999999L) {
                    bindingResult.addError(new FieldError("contractSaveDto", "idCardNumber", "Must be entered 13 numbers"));
                }
                if (contractSaveDto.getIdCardWhoIssued() == null) {
                    bindingResult.addError(new FieldError("contractSaveDto", "idCardWhoIssued", "Must not be empty"));
                } else if (contractSaveDto.getIdCardWhoIssued() > 9999 || contractSaveDto.getIdCardWhoIssued() <= 999) {
                    bindingResult.addError(new FieldError("contractSaveDto", "idCardWhoIssued", "Must be entered 4 numbers"));
                }
            } else if (documentStyle.equals(DocumentStyle.PASSPORT)) {
                if (contractSaveDto.getPassportSeries() == null || contractSaveDto.getPassportSeries().equals("")) {
                    bindingResult.addError(new FieldError("contractSaveDto", "passportSeries", "Must not be empty"));
                } else if (contractSaveDto.getPassportSeries().length() != 2) {
                    bindingResult.addError(new FieldError("contractSaveDto", "passportSeries", "Must be entered 2 letters"));
                }
                if (contractSaveDto.getPassportNumber() == null) {
                    bindingResult.addError(new FieldError("contractSaveDto", "passportNumber", "Must not be empty"));
                } else if (contractSaveDto.getPassportNumber() > 999999 || contractSaveDto.getPassportNumber() <= 99999) {
                    bindingResult.addError(new FieldError("contractSaveDto", "passportNumber", "Must be entered 6 numbers"));
                }
                if (contractSaveDto.getPassportWhoIssued() == null || contractSaveDto.getPassportWhoIssued().equals("")) {
                    bindingResult.addError(new FieldError("contractSaveDto", "passportWhoIssued", "Must not be empty"));
                }
            }
        }
    }
}