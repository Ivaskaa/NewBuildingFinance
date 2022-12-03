package com.example.NewBuildingFinance.service.flat;

import com.example.NewBuildingFinance.dto.flat.FlatSaveDto;
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
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Log4j2
@AllArgsConstructor
public class FlatServiceImpl implements FlatService{
    private final FlatRepository flatRepository;
    private final ObjectRepository objectRepository;

    @Override
    public Page<FlatTableDto> findSortingAndSpecificationPage(
            Integer currentPage,
            Integer size,
            String sortingField,
            String sortingDirection,

            Integer number,
            Long objectId,
            String status,
            Double areaStart,
            Double areaFin,
            Integer priceStart,
            Integer priceFin,
            Integer advanceStart,
            Integer advanceFin,
            Integer enteredStart,
            Integer enteredFin,
            Integer remainsStart,
            Integer remainsFin
    ) {
        log.info("get flat page. page: {}, size: {} field: {}, direction: {}",
                currentPage - 1, size, sortingField, sortingDirection);
        Specification<Flat> specification = Specification
                .where(FlatSpecification.likeNumber(number))
                .and(FlatSpecification.likeObjectId(objectId))
                .and(FlatSpecification.likeStatus(status))
                .and(FlatSpecification.likeArea(areaStart, areaFin))
                .and(FlatSpecification.likePrice(priceStart, priceFin))
                .and(FlatSpecification.likeAdvance(advanceStart, advanceFin))
                .and(FlatSpecification.likeEntered(enteredStart, enteredFin))
                .and(FlatSpecification.likeRemains(remainsStart, remainsFin))
                .and(FlatSpecification.deletedFalse());
        Sort sort = Sort.by(Sort.Direction.valueOf(sortingDirection), sortingField);
        Pageable pageable = PageRequest.of(currentPage - 1, size, sort);
        Page<FlatTableDto> flats = flatRepository.findAll(specification, pageable).map(Flat::buildTableDto);
        log.info("success");
        return flats;
    }

    @Override
    public Flat save(Flat flat) {
        log.info("save flat: {}", flat);
        Flat flatAfterSave = flatRepository.save(flat);
        log.info("success");
        return flatAfterSave;
    }

    @Override
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

    @Override
    public void deleteById(Long id) {
        log.info("set flat.deleted true by id: {}", id);
        flatRepository.setDeleted(id);
        log.info("success set flat.deleted true");
    }

    @Override
    public ResponseEntity<byte[]> getXlsx() throws IOException {
        log.info("get flats.xlsx");

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("flats");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 6000);
        sheet.setColumnWidth(7, 6000);
        sheet.setColumnWidth(8, 6000);
        sheet.setColumnWidth(9, 6000);
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
        headerCell.setCellValue("Status");
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

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        List<Flat> flats = flatRepository.findAllByDeletedFalse();

        for(int i = 0; i < flats.size(); i++){
            Row row = sheet.createRow(i + 1);

            Cell cell = row.createCell(0);
            cell.setCellValue(flats.get(i).getNumber());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(flats.get(i).getQuantityRooms());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(flats.get(i).getFloor());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue(flats.get(i).getArea());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(
                    flats.get(i).getObject().getHouse() + "(" +
                            flats.get(i).getObject().getSection() + ")");
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue(flats.get(i).getStatus().toString());
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue(flats.get(i).getPrice());
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue(flats.get(i).getSalePrice());
            cell.setCellStyle(style);

            cell = row.createCell(8);
            cell.setCellValue(flats.get(i).getManager());
            cell.setCellStyle(style);

            cell = row.createCell(9);
            cell.setCellValue(flats.get(i).getAgency());
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

        log.info("success get flats.xlsx");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getXlsxExample() throws IOException {
        log.info("get flats.xlsx example");

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("flats");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 6000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 6000);
        sheet.setColumnWidth(7, 6000);
        sheet.setColumnWidth(8, 6000);
        sheet.setColumnWidth(9, 6000);

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
        headerCell.setCellValue("Status");
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
        rulesStyle.setFillPattern(FillPatternType.DIAMONDS);
        rulesStyle.setWrapText(true);

        XSSFFont rulesFont = ((XSSFWorkbook) workbook).createFont();
        rulesFont.setFontName("Arial");
        rulesFont.setFontHeightInPoints((short) 9);
        rulesStyle.setFont(rulesFont);

        // cell0 number rules
        Cell rulesCell = rules.createCell(0);
        rulesCell.setCellValue("Must be integer, cannot be \nrepeated in the same object");
        rulesCell.setCellStyle(rulesStyle);

        // cell1 quantity rooms rules
        rulesCell = rules.createCell(1);
        rulesCell.setCellValue("Must be integer");
        rulesCell.setCellStyle(rulesStyle);

        // cell2 flor rules
        rulesCell = rules.createCell(2);
        rulesCell.setCellValue("Must be integer");
        rulesCell.setCellStyle(rulesStyle);

        // cell3 area rules
        rulesCell = rules.createCell(3);
        rulesCell.setCellValue("Must be double");
        rulesCell.setCellStyle(rulesStyle);

        // cell4 object rules
        rulesCell = rules.createCell(4);
        rulesCell.setCellValue("Must be already created, must \nbe in format \"House(Section)\"");
        rulesCell.setCellStyle(rulesStyle);

        // cell5 style rules
        rulesCell = rules.createCell(5);
        rulesCell.setCellValue("Must be \"ACTIVE\" or \"BOOKING\" or \n\"NOTACTIVE\" or \"REMOVEDFROMSALE\"\n or \"RESERVE\"");
        rulesCell.setCellStyle(rulesStyle);

        // cell6 price rules
        rulesCell = rules.createCell(6);
        rulesCell.setCellValue("Must be double");
        rulesCell.setCellStyle(rulesStyle);

        // cell7 sale price rules
        rulesCell = rules.createCell(7);
        rulesCell.setCellValue("Must be double");
        rulesCell.setCellStyle(rulesStyle);

        // cell8 manager % and cell9 agency % rules
        CellRangeAddress cellAddresses = new CellRangeAddress(1, 1, 8,9);
        sheet.addMergedRegion(cellAddresses);
        rulesCell = rules.createCell(8);
        rulesCell.setCellValue("Must be integer, the sum should be 100");
        rulesCell.setCellStyle(rulesStyle);

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

        log.info("success get flats.xlsx example");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Override
    public boolean setXlsx(@NotNull MultipartFile file) throws IOException {
        log.info("save flats.xlsx");

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        CellStyle cellStyleError = workbook.createCellStyle();
        cellStyleError.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        cellStyleError.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle cellStyleCorrect = workbook.createCellStyle();
        cellStyleCorrect.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        cellStyleCorrect.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        List<Flat> flats = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        boolean ok = false;
        for (Row row : sheet) {
            if(sheet.getRow(0).equals(row) || sheet.getRow(1).equals(row)){continue;}
            Flat flat = new Flat();
            Object object = null;

            // cell4 object
            Cell cell4 = row.getCell(4);
            if(cell4.getCellType().equals(CellType.STRING)){
                if(cell4.getStringCellValue().contains("(")){
                    String[] words = cell4.getStringCellValue().split("\\(");
                    if (words.length == 2){
                        String house = words[0];
                        String sectionWithBracket = words[1];
                        if (!house.equals("") && !sectionWithBracket.equals("")) {
                            String[] words2 = sectionWithBracket.split("\\)");
                            if(words2.length == 1) {
                                String section = words2[0];

                                object = objectRepository.findByHouseAndSection(house, section);
                                if(object != null){
                                    flat.setObject(object);
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
                } else {
                    ok = true;
                    cell4.setCellStyle(cellStyleError);
                }
            } else {
                ok = true;
                cell4.setCellStyle(cellStyleError);
            }

            // cell0 number
            Cell cell0 = row.getCell(0);
            if(cell0.getCellType().equals(CellType.NUMERIC)){
                if(object != null){
                    if(flatRepository.findFlatInObject((int) Math.round(cell0.getNumericCellValue()), object.getId()) == null){
                        flat.setNumber((int) Math.round(cell0.getNumericCellValue()));
                        cell0.setCellStyle(cellStyleCorrect);
                    } else {
                        ok = true;
                        cell0.setCellStyle(cellStyleError);
                    }
                } else {
                    ok = true;
                    cell0.setCellStyle(cellStyleError);
                }
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
                if(flat.getPrice() >= flat.getSalePrice()){
                    cell7.setCellStyle(cellStyleCorrect);
                } else {
                    ok = true;
                    cell6.setCellStyle(cellStyleError);
                    cell7.setCellStyle(cellStyleError);
                }
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

            if(flat.getManager() + flat.getAgency() > 100){
                ok = true;
                cell8.setCellStyle(cellStyleError);
                cell9.setCellStyle(cellStyleError);
            }

            flats.add(flat);
        }

        if(ok){
            log.info("error save flats.xlsx");
            File responseFile = new File("src/main/resources/flatsErrors.xlsx");

            FileOutputStream outputStream = new FileOutputStream(responseFile);
            workbook.write(outputStream);
            workbook.close();

            return false;
        } else {
            if(!flats.isEmpty()){
                log.info("success save flats.xlsx");
                flatRepository.saveAll(flats);
                return true;
            } else {
                log.info("success but flats list is empty");
                return false;
            }
        }
    }

    @Override
    public ResponseEntity<byte[]> getXlsxErrors() throws IOException {
        log.info("get flatsErrors.xlsx");
        File responseFile = new File("src/main/resources/flatsErrors.xlsx");

        byte[] content = Files.readAllBytes(responseFile.toPath());

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(
                "flatsWithErrors" + format.format(new Date()) + ".xlsx",
                "flatsWithErrors" + format.format(new Date()) + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("error save flats.xlsx");
        log.info("success get flatsErrors.xlsx");
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @Override
    public Flat findById(@NotNull Long id) {
        log.info("get flat by id: {}", id);
        Flat flat = null;
        if (id != null) {
            flat = flatRepository.findById(id).orElseThrow();
        }
        log.info("success");
        return flat;
    }

    public List<Pair<StatusFlat, String>> getStatusesByFlatId(@NotNull Long flatId) {

        List<Pair<StatusFlat, String>> list = new ArrayList<>();
        if(flatId != null && flatId != 0){
            Flat flat = flatRepository.findById(flatId).orElseThrow();
            if(flat.getContract() != null){
                for(StatusFlat statusObject : StatusFlat.values()){
                    if(statusObject.equals(StatusFlat.SOLD)) {
                        list.add(Pair.of(statusObject, statusObject.getValue()));
                    }
                }
            } else {
                for(StatusFlat statusObject : StatusFlat.values()){
                    if(!statusObject.equals(StatusFlat.SOLD)) {
                        list.add(Pair.of(statusObject, statusObject.getValue()));
                    }
                }
            }
        } else {
            for(StatusFlat statusObject : StatusFlat.values()){
                if(!statusObject.equals(StatusFlat.SOLD)) {
                    list.add(Pair.of(statusObject, statusObject.getValue()));
                }
            }
        }
        return list;
    }

    @Override
    public List<Flat> getFlatsWithContractWithFlatPaymentByObjectId(@NotNull Long objectId, Long flatId) {
        log.info("get flats with contract with flat payments by object id: {}", objectId);
        List<Flat> flats;
        if(flatId == null) {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNotNull(objectId);
        } else {
            flats = flatRepository.findAllByDeletedFalseAndObjectIdAndContractNotNullOrObjectIdAndId(objectId, objectId, flatId);
        }
        log.info("success get flats with contract with flat payments by object id");
        return flats;
    }

    @Override
    public List<Flat> getFlatsWithoutContractByObjectId(@NotNull Long id, Long flatId) {
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

    public boolean validationWithoutDatabase(@NotNull BindingResult bindingResult, @NotNull FlatSaveDto flatSaveDto) {
        boolean isValid = true;
        if(flatSaveDto.getPrice() != null && flatSaveDto.getSalePrice() != null){
            if(flatSaveDto.getPrice() < flatSaveDto.getSalePrice()){
                isValid = false;
                bindingResult.addError(new FieldError("flatSaveDto", "price", "Price must be rather then sale price"));
                bindingResult.addError(new FieldError("flatSaveDto", "salePrice", "Price must be rather then sale price"));
            }
        }
        if(flatSaveDto.getAgency() != null && flatSaveDto.getManager() != null){
            if(flatSaveDto.getAgency() + flatSaveDto.getManager() > 100){
                isValid = false;
                bindingResult.addError(new FieldError("flatSaveDto", "agency", "The sum of percentages must be less than 100"));
                bindingResult.addError(new FieldError("flatSaveDto", "manager", "The sum of percentages must be less than 100"));
            }
        }
        return isValid;
    }

    public boolean validationCreateWithDatabase(@NotNull BindingResult bindingResult, @NotNull FlatSaveDto flatSaveDto) {
        boolean isValid = true;
        if(flatSaveDto.getNumber() != null && flatSaveDto.getObjectId() != null) {
            Flat flat = flatRepository.findFlatInObject(flatSaveDto.getNumber(), flatSaveDto.getObjectId());
            if (flat != null) {
                isValid = false;
                bindingResult.addError(new FieldError("flatSaveDto", "number", "In selected object the flat with this number is exists"));
            }
        }
        return isValid;
    }

    public boolean validationUpdateWithDatabase(BindingResult bindingResult, FlatSaveDto flatSaveDto) {
        boolean isValid = true;
        Integer flatNumber = flatRepository.findFlatNumberById(flatSaveDto.getId());
        if(flatNumber == null){
            return false;
        } else {
            if(flatSaveDto.getNumber() != null && flatSaveDto.getObjectId() != null) {
                if(!flatNumber.equals(flatSaveDto.getNumber())){
                    Flat flat2 = flatRepository.findFlatInObject(flatSaveDto.getNumber(), flatSaveDto.getObjectId());
                    if (flat2 != null) {
                        isValid = false;
                        bindingResult.addError(new FieldError("flatSaveDto", "number", "In selected object the flat with this number is exists"));
                    }
                }
            }
            if(flatSaveDto.getId() != null && flatSaveDto.getSalePrice() != null){
                Flat flat2 = flatRepository.findById(flatSaveDto.getId()).orElse(null);
                Double price = 0d;
                if(flat2 == null){
                    return false;
                } else {
                    for(FlatPayment flatPayment : flat2.getFlatPayments()){
                        price += flatPayment.getPlanned();
                    }
                }
                if(price > flatSaveDto.getSalePrice()){
                    bindingResult.addError(new FieldError("flatSaveDto", "salePrice", "Sale price must be rather or equals to flat payment list"));
                }
            }
        }
        return isValid;
    }

    @Override
    public boolean validationCheckStatus(StatusFlat status) {
        return !status.equals(StatusFlat.ACTIVE);
    }
}
