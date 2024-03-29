package com.example.NewBuildingFinance.service.flat;

import com.example.NewBuildingFinance.dto.flat.FlatSaveDto;
import com.example.NewBuildingFinance.dto.flat.FlatTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

public interface FlatService {

    /**
     * sorting specification page for flat table
     * @param currentPage current page
     * @param size table size
     * @param sortingField sorting field
     * @param sortingDirection sorting direction
     * @param number flat number
     * @param objectId flat object id
     * @param status flat status
     * @param areaStart flat area start
     * @param areaFin flat area finish
     * @param priceStart flat price start
     * @param priceFin flat price finish
     * @param advanceStart flat -> flat payment with smaller date -> price start
     * @param advanceFin flat -> flat payment with smaller date -> price finish
     * @param enteredStart flat -> flat payments -> price sum start
     * @param enteredFin flat -> flat payments -> price sum finish
     * @param remainsStart flat remains start
     * @param remainsFin flat remains finish
     * @return page of flat table dto
     */
    Page<FlatTableDto> findSortingAndSpecificationPage(
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
    );

    /**
     * save new flat
     * @param flat flat entity
     * @return flat after save
     */
    Flat save(Flat flat);

    /**
     * update old flat
     * @param flatForm flat entity
     * @return flat after save
     */
    Flat update(Flat flatForm);

    /**
     * find flat entity by id
     * @param id flat id
     * @return flat entity
     */
    Flat findById(Long id);

    /**
     * delete flat by id
     * @param id flat id
     */
    void deleteById(Long id);

    /**
     * get xlsx list of all flats
     * @return response of bytes
     * @throws IOException if file not found
     */
    ResponseEntity<byte[]> getXlsx() throws IOException;

    /**
     * get xlsx example for setXlsx
     * @return response of bytes
     * @throws IOException if file not found
     */
    ResponseEntity<byte[]> getXlsxExample() throws IOException;

    /**
     * set xlsx file to database
     * @param file multipart file
     * @return true if file has exception and false if file is correct
     * @throws IOException if file not found
     */
    boolean setXlsx(MultipartFile file) throws IOException;

    /**
     * get xlsx errors file
     * @return response of bytes
     * @throws IOException if file not found
     */
    ResponseEntity<byte[]> getXlsxErrors() throws IOException;

    /**
     * flat statuses for individual flat
     * @param flatId flat id
     * @return list a pair of status and status value
     */
    List<Pair<StatusFlat, String>> getStatusesByFlatId(@NotNull Long flatId);

    /**
     * get flat list with contract with flat payments and by object id or flat id
     * @param id object id
     * @param flatId flat id (can be null)
     * @return flat list
     */
    List<Flat> getFlatsWithContractWithFlatPaymentByObjectId(Long id, Long flatId);

    /**
     * get flat list without contracts, with status ACTIVE by object id or flat id
     * @param id object id
     * @param flatId flat id (can be null)
     * @return flat list
     */
    List<Flat> getFlatsWithoutContractByObjectId(Long id, Long flatId);

    /**
     * price and sale price validation
     * @param bindingResult binding result for exceptions
     * @param flatSaveDto flat dto
     * @return false if we have exceptions
     */
    boolean validationWithoutDatabase(@NotNull BindingResult bindingResult, @NotNull FlatSaveDto flatSaveDto);

    /**
     * validation for create flat
     * @param bindingResult binding result for exceptions
     * @param flatSaveDto flat dto
     * @return false if we have exceptions
     */
    boolean validationCreateWithDatabase(@NotNull BindingResult bindingResult, @NotNull FlatSaveDto flatSaveDto);

    /**
     * validation for update flat
     * @param bindingResult binding result for exceptions
     * @param flatSaveDto flat dto
     * @return false if we have exceptions
     */
    boolean validationUpdateWithDatabase(@NotNull BindingResult bindingResult, @NotNull FlatSaveDto flatSaveDto);

    /**
     * check status (without database)
     * @param status flat status
     * @return if status equals active return false
     */
    boolean validationCheckStatus(StatusFlat status);

}
