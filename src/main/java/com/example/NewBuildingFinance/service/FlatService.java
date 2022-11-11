package com.example.NewBuildingFinance.service;

import com.example.NewBuildingFinance.dto.flat.FlatTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.entities.object.Object;
import com.example.NewBuildingFinance.others.specifications.FlatSpecification;
import com.example.NewBuildingFinance.repository.FlatRepository;
import com.example.NewBuildingFinance.repository.ObjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Log4j2
@AllArgsConstructor
public class FlatService {
    private final FlatRepository flatRepository;
    private final ObjectRepository objectRepository;

    public Page<FlatTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Optional<Integer> number,
            Optional<Long> objectId,
            Optional<String> status,
            Optional<Double> areaStart,
            Optional<Double> areaFin,
            Optional<Integer> priceStart,
            Optional<Integer> priceFin,
            Optional<Integer> advanceStart,
            Optional<Integer> advanceFin
    ) {
        Object object = null;
        if (objectId.isPresent()) {
            object = objectRepository.findById(objectId.get()).orElse(null);
        }
        log.info("get flat page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<Flat> specification = Specification
                .where(FlatSpecification.likeNumber(number.orElse(null)))
                .and(FlatSpecification.likeObject(object))
                .and(FlatSpecification.likeStatus(status.orElse(null)))
                .and(FlatSpecification.likeArea(areaStart.orElse(null), areaFin.orElse(null)))
                .and(FlatSpecification.likePrice(priceStart.orElse(null), priceFin.orElse(null)))
                .and(FlatSpecification.likeAdvance(advanceStart.orElse(null), advanceFin.orElse(null)));
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<FlatTableDto> flats = flatRepository.findAll(specification, pageable).map(Flat::buildTableDto);
        log.info("success");
        return flats;
    }

    public Flat save(Flat flat) {
        log.info("save flat: {}", flat);
        Flat flatAfterSave = flatRepository.save(flat);
        log.info("success");
        return flatAfterSave;
    }

    public Flat update(Flat flatForm) {
        log.info("update object: {}", flatForm);
        Flat object = flatRepository.findById(flatForm.getId()).orElseThrow();
        object.setObject(flatForm.getObject());
        object.setStatus(flatForm.getStatus());
        object.setArea(flatForm.getArea());
        object.setPrice(flatForm.getPrice());
        object.setSalePrice(flatForm.getSalePrice());
        object.setAdvance(flatForm.getAdvance());
        object.setNumber(flatForm.getNumber());
        object.setQuantityRooms(flatForm.getQuantityRooms());
        object.setFloor(flatForm.getFloor());
        object.setDescription(flatForm.getDescription());
        object.setAgency(flatForm.getAgency());
        object.setManager(flatForm.getManager());
        object.setBuyer(flatForm.getBuyer());
        object.setRealtor(flatForm.getRealtor());
        flatRepository.save(object);
        log.info("success");
        return object;
    }

    public void deleteById(Long id) {
        log.info("delete flat by id: {}", id);
        flatRepository.deleteById(id);
        log.info("success");
    }

    public ResponseEntity<byte[]> getXlsxExample() throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("flats");
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 2500);
        sheet.setColumnWidth(4, 2500);
        sheet.setColumnWidth(5, 3500);
        sheet.setColumnWidth(6, 3500);
        sheet.setColumnWidth(7, 3500);
        sheet.setColumnWidth(8, 3500);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 15);
        headerStyle.setFont(font);

        // cell0 number
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Number");
        headerCell.setCellStyle(headerStyle);

        // cell1 quantity rooms
        headerCell = header.createCell(1);
        headerCell.setCellValue("Quantity rooms");
        headerCell.setCellStyle(headerStyle);

        // cell2 flor
        headerCell = header.createCell(2);
        headerCell.setCellValue("Flor");
        headerCell.setCellStyle(headerStyle);

        // cell3 area
        headerCell = header.createCell(3);
        headerCell.setCellValue("Area");
        headerCell.setCellStyle(headerStyle);

        // cell4 object
        headerCell = header.createCell(4);
        headerCell.setCellValue("Object");
        headerCell.setCellStyle(headerStyle);

        // cell5 style
        headerCell = header.createCell(5);
        headerCell.setCellValue("Style");
        headerCell.setCellStyle(headerStyle);

        // cell6 price
        headerCell = header.createCell(6);
        headerCell.setCellValue("Price");
        headerCell.setCellStyle(headerStyle);

        // cell7 sale price
        headerCell = header.createCell(7);
        headerCell.setCellValue("Sale price");
        headerCell.setCellStyle(headerStyle);

        // cell8 manager %
        headerCell = header.createCell(8);
        headerCell.setCellValue("Manager %");
        headerCell.setCellStyle(headerStyle);

        // cell9 agency %
        headerCell = header.createCell(9);
        headerCell.setCellValue("Agency %");
        headerCell.setCellStyle(headerStyle);

        Row rules = sheet.createRow(1);

        CellStyle rulesStyle = workbook.createCellStyle();
        rulesStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        rulesStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont rulesFont = ((XSSFWorkbook) workbook).createFont();
        rulesFont.setFontName("Arial");
        rulesFont.setFontHeightInPoints((short) 15);
        rulesStyle.setFont(rulesFont);

        rules.setRowStyle(rulesStyle);

        // cell0 number rules
        Cell rulesCell = rules.createCell(0);
        rulesCell.setCellValue("Must be integer, cannot be repeated in the same object");

        // cell1 quantity rooms rules
        rulesCell = header.createCell(1);
        rulesCell.setCellValue("Must be integer");

        // cell2 flor rules
        rulesCell = header.createCell(2);
        rulesCell.setCellValue("Must be integer");

        // cell3 area rules
        rulesCell = header.createCell(3);
        rulesCell.setCellValue("Must be double");

        // cell4 object rules
        rulesCell = header.createCell(4);
        rulesCell.setCellValue("Must be already created, must be in format \"House(Section)\"");

        // cell5 style rules
        rulesCell = header.createCell(5);
        rulesCell.setCellValue("Must be \"ACTIVE\" or \"BOOKING\" or \"NOTACTIVE\" or \"REMOVEDFROMSALE\" or \"RESERVE\"");

        // cell6 price rules
        rulesCell = header.createCell(6);
        rulesCell.setCellValue("Must be double");

        // cell7 sale price rules
        rulesCell = header.createCell(7);
        rulesCell.setCellValue("Must be double");

        // cell8 manager % and cell9 agency % rules
        CellRangeAddress cellAddresses = new CellRangeAddress(1, 1, 8,9);
        sheet.addMergedRegion(cellAddresses);
        rulesCell = header.createCell(8);
        rulesCell.setCellValue("Must be integer, the sum should be 100");

        File file = new File("src/main/resources/flats.xlsx");

        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();

        byte[] content = Files.readAllBytes(file.toPath());

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "flatsExample" + format.format(new Date()) + ".xlsx",
                "flatsExample" + format.format(new Date()) + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("success get flats.xlsx for income");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    public ResponseEntity<byte[]> setXlsx(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        CellStyle cellStyleError = workbook.createCellStyle();
        cellStyleError.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        cellStyleError.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle cellStyleCorrect = workbook.createCellStyle();
        cellStyleCorrect.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        cellStyleCorrect.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<Flat> flats = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        boolean ok = false;
        for (Row row : sheet) {
            if(sheet.getRow(0).equals(row)){continue;}
            if(sheet.getRow(1).equals(row)){continue;}
            Flat flat = new Flat();

            // cell0 number
            Cell cell0 = row.getCell(0);
            if(cell0.getCellType().equals(CellType.NUMERIC)){
                flat.setNumber((int) Math.round(cell0.getNumericCellValue()));
                cell0.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell0.setCellStyle(cellStyleError);
            }

            // cell1 quantity rooms
            Cell cell1 = row.getCell(1);
            if(cell1.getCellType().equals(CellType.NUMERIC)){
                flat.setQuantityRooms((int) Math.round(cell1.getNumericCellValue()));
                cell1.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell1.setCellStyle(cellStyleError);
            }

            // cell2 flor
            Cell cell2 = row.getCell(2);
            if(cell2.getCellType().equals(CellType.NUMERIC)){
                flat.setFloor((int) Math.round(cell2.getNumericCellValue()));
                cell2.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell2.setCellStyle(cellStyleError);
            }

            // cell3 area
            Cell cell3 = row.getCell(3);
            if(cell3.getCellType().equals(CellType.NUMERIC)){
                flat.setArea(cell3.getNumericCellValue());
                cell3.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell3.setCellStyle(cellStyleError);
            }

            // cell4 object
            Cell cell4 = row.getCell(4);
            if(cell4.getCellType().equals(CellType.STRING)){
                if(cell4.getStringCellValue().contains("\\(")){
                    String[] words = cell4.getStringCellValue().split("\\(");
                    if (words.length == 2){
                        String house = words[0];
                        String section = words[1];
                        if (section.contains("\\)")){
                            section = section.replace("\\)", "");

                            // search object
                            cell4.setCellStyle(cellStyleCorrect);
                        } else {
                            ok = true;
                            cell4.setCellStyle(cellStyleError);
                        }
                    } else {
                        ok = true;
                        cell4.setCellStyle(cellStyleError);
                    }
                } else {
                    ok = true;
                    cell4.setCellStyle(cellStyleError);
                }
            } else {
                ok = true;
                cell4.setCellStyle(cellStyleError);
            }

            // cell5 style
            Cell cell5 = row.getCell(5);
            if(cell5.getCellType().equals(CellType.STRING)){
                try{
                    flat.setStatus(StatusFlat.valueOf(cell5.getStringCellValue()));
                    cell5.setCellStyle(cellStyleCorrect);
                } catch (Exception e){
                    ok = true;
                    cell5.setCellStyle(cellStyleError);
                }
            } else {
                ok = true;
                cell5.setCellStyle(cellStyleError);
            }

            // cell6 price
            Cell cell6 = row.getCell(6);
            if(cell6.getCellType().equals(CellType.NUMERIC)){
                flat.setPrice(cell6.getNumericCellValue());
                cell6.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell6.setCellStyle(cellStyleError);
            }

            // cell7 sale price
            Cell cell7 = row.getCell(7);
            if(cell7.getCellType().equals(CellType.NUMERIC)){
                flat.setSalePrice(cell7.getNumericCellValue());
                cell7.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell7.setCellStyle(cellStyleError);
            }

            // cell8 manager %
            Cell cell8 = row.getCell(8);
            if(cell8.getCellType().equals(CellType.NUMERIC)){
                flat.setManager((int) Math.round(cell8.getNumericCellValue()));
                cell8.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell8.setCellStyle(cellStyleError);
            }

            // cell9 agency %
            Cell cell9 = row.getCell(9);
            if(cell9.getCellType().equals(CellType.NUMERIC)){
                flat.setAgency((int) Math.round(cell9.getNumericCellValue()));
                cell9.setCellStyle(cellStyleCorrect);
            } else {
                ok = true;
                cell9.setCellStyle(cellStyleError);
            }

            flats.add(flat);
        }

        if(ok){
            File responseFile = new File("src/main/resources/flats.xlsx");

            FileOutputStream outputStream = new FileOutputStream(responseFile);
            workbook.write(outputStream);
            workbook.close();

            byte[] content = Files.readAllBytes(responseFile.toPath());

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                    "flatsWithErrors" + format.format(new Date()) + ".xlsx",
                    "flatsWithErrors" + format.format(new Date()) + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            log.info("error save flats.xlsx");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } else {
            log.info("success save flats.xlsx");
            return null;
//            flatRepository.saveAll(flats);
        }
    }


    public ResponseEntity<byte[]> getXlsx() throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("flats");
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 2500);
        sheet.setColumnWidth(4, 2500);
        sheet.setColumnWidth(5, 3500);
        sheet.setColumnWidth(6, 3500);
        sheet.setColumnWidth(7, 3500);
        sheet.setColumnWidth(8, 3500);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 15);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Number");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Object");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Status");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Area");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Price");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Sale price");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("Advance");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Entered");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Remains");
        headerCell.setCellStyle(headerStyle);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        List<Flat> flats = flatRepository.findAllByDeletedFalse();

        for(int i = 0; i < flats.size(); i++){
            Row row = sheet.createRow(i + 1);

            Cell cell = row.createCell(0);
            cell.setCellValue(flats.get(i).getNumber());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(
                    flats.get(i).getObject().getHouse() + "(" +
                    flats.get(i).getObject().getSection() + ")");
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(flats.get(i).getStatus().getValue());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue(flats.get(i).getArea());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(flats.get(i).getPrice());
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue(flats.get(i).getSalePrice());
            cell.setCellStyle(style);

            Double advance = 0d;
            Double entered = 0d;
            Date date = new Date(99999999999999L);
            for(FlatPayment flatPayment : flats.get(i).getFlatPayments()){
                if(date.after(flatPayment.getDate())){
                    date = flatPayment.getDate();
                    advance = flatPayment.getPlanned();
                    if(flatPayment.getActually() != null) {
                        entered += flatPayment.getActually();
                    }
                }
            }

            cell = row.createCell(6);
            cell.setCellValue(advance);
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue(entered);
            cell.setCellStyle(style);

            cell = row.createCell(8);
            cell.setCellValue(flats.get(i).getSalePrice() - entered);
            cell.setCellStyle(style);
        }

        File file = new File("src/main/resources/flats.xlsx");

        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();

        byte[] content = Files.readAllBytes(file.toPath());

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "flats" + format.format(new Date()) + ".xlsx",
                "flats" + format.format(new Date()) + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("success get flats.xlsx for income");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    public Flat findById(Long id) {
        log.info("get flat by id: {}", id);
        Flat flat = null;
        if (id != null) {
            flat = flatRepository.findById(id).orElseThrow();
        }
        log.info("success");
        return flat;
    }

    public Flat findByContractId(Long id) {
        log.info("get flat by contract id: {}", id);
        Flat flat = flatRepository.findFlatByContractId(id).orElseThrow();
        log.info("success");
        return flat;
    }

    public List<Flat> getWithContractWithFlatPaymentByObjectId(Long id, Long flatId) {
        log.info("get flats with contract with flat payments by object id: {}", id);
        List<Flat> flats;
        if(flatId == null) {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNotNull(id);
        } else {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNotNullOrId(id, flatId);
        }
        log.info("success get flats with contract with flat payments by object id");
        return flats;
    }

    public List<Flat> getFlatsWithoutContractByObjectId(Long id, Long flatId) {
        log.info("get flats without contracts by object id: {}", id);
        List<Flat> flats;
        if(flatId == null) {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNullAndStatus(id, StatusFlat.ACTIVE);
        } else {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNullAndStatusOrId(id, StatusFlat.ACTIVE, flatId);
        }
        log.info("success get flats without contracts by object id");
        return flats;
    }

    public boolean checkPrice(Double price, Double salePrice) {
        return price < salePrice;
    }

    public boolean checkPercentages(Integer agency, Integer manager) {
        if (agency == null || manager == null){
            return false;
        }
        return agency + manager > 100;
    }

    public boolean checkFlatNumber(Integer number, Long objectId) {
        Flat flat;
        flat = flatRepository.findFlatInObject(number, objectId);
        return flat != null;
    }

    public boolean checkStatus(StatusFlat status) {
        return !status.equals(StatusFlat.ACTIVE);
    }


}
