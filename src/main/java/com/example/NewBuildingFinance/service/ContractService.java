package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.contract.ContractSaveDto;
import com.example.NewBuildingFinance.dto.contract.ContractTableDto;
import com.example.NewBuildingFinance.dto.contract.ContractUploadDto;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.others.specifications.ContractSpecification;
import com.example.NewBuildingFinance.repository.ContractRepository;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.repository.ObjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final FlatRepository flatRepository;
    private final ObjectRepository objectRepository;
    private static final String PDF_OUTPUT = "src/main/resources/html2pdf.pdf";

    public Page<ContractTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Optional<Long> id,
            Optional<String> dateStart,
            Optional<String> dateFin,
            Optional<Integer> flatNumber,
            Optional<Long> objectId,
            Optional<String> buyerName,
            Optional<String> comment
    ) {
        log.info("get flat page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<Contract> specification = Specification
                .where(ContractSpecification.likeId(id.orElse(null)))
                .and(ContractSpecification.likeObject(objectId.orElse(null)))
                .and(ContractSpecification.likeFlatNumber(flatNumber.orElse(null)))
                .and(ContractSpecification.likeBuyerName(buyerName.orElse(null)))
                .and(ContractSpecification.likeComment(comment.orElse(null)))
                .and(ContractSpecification.likeDeletedFalse());
//                .and(ContractSpecification.likeAdvance(advanceStart.orElse(null), advanceFin.orElse(null)));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<ContractTableDto> flats = contractRepository.findAll(specification, pageable).map(Contract::buildTableDto);
        log.info("success");
        return flats;
    }

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
        flatRepository.save(flat);
        ContractUploadDto contractUploadDto = contractAfterSave.buildUploadDto();
        contractUploadDto.setFlat(flat);
        contractUploadDto.setObject(flat.getObject());
        log.info("success");
        return contractUploadDto;
    }

    public ContractUploadDto update(ContractSaveDto contractSaveDto) throws ParseException {
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
        log.info("success");
        return contractUploadDto;
    }

    public Contract deleteById(Long id) {
        log.info("delete contract by id: {}", id);
        Contract contract = contractRepository.findById(id).orElseThrow();

        Flat flat = flatRepository.findFlatByContractId(contract.getId()).orElse(null);
        if (flat != null) {
            flat.setContract(null);
            flat.setBuyer(null);
            flatRepository.save(flat);
        }
        contract.setBuyer(null);
        contract.setDeleted(true);
        contractRepository.save(contract);
        log.info("success");
        return contract;
    }

    public ContractUploadDto findById(Long id) {
        log.info("get contract by id: {}", id);
        Contract contract = contractRepository.findById(id).orElseThrow();
        ContractUploadDto contractUploadDto = contract.buildUploadDto();
        Flat flat = flatRepository.findFlatByContractId(contract.getId()).orElse(null);
        if (flat != null) {
            contractUploadDto.setFlat(flat);
            contractUploadDto.setObject(flat.getObject());
        }
        log.info("success");
        return contractUploadDto;
    }

    public void exportPdf( Long id) throws Exception {
        Contract contract = contractRepository.findById(id).orElseThrow();
        Document document = Jsoup.parse(contract.getContractTemplate().getText(), "UTF-8");
        File outputPdf = new File(PDF_OUTPUT);
        xhtmlToPdf(document, outputPdf);
//        return outputPdf;
    }

    private void xhtmlToPdf(Document xhtml, File outputPdf) throws Exception {
        try (OutputStream outputStream = new FileOutputStream(outputPdf)) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            renderer.setDocumentFromString(xhtml.html());
            renderer.layout();
            renderer.createPDF(outputStream);
        }
    }

//    public Map<String, ?> createPdf(Long id) throws FileNotFoundException {
//        log.info("create pdf for contract id: {}", id);
//
//        log.info("success");
//        return map;
//    }

    public boolean checkRealtor(Long flatId) {
        Flat flat = flatRepository.findFlatByContractId(flatId).orElse(null);
        if (flat == null){
            return false;
        }
        return flat.getRealtor() == null;
    }

    public boolean checkFlatPayments(Long flatId) {
        Flat flat = flatRepository.findFlatByContractId(flatId).orElse(null);
        if (flat == null){
            return false;
        }
        Integer price = flat.getSalePrice();
        for(FlatPayment flatPayment : flat.getFlatPayments()){
            price -= flatPayment.getPlanned();
        }
        return price != 0;
    }

}
