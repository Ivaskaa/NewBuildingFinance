package com.example.NewBuildingFinance.dto.cashRegister;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.cashRegister.Article;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.Economic;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.object.Object;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SpendingSaveDto {
    private Long id;
    @NotNull(message = "Must not be empty")
    private Long number;
    @NotNull(message = "Must not be empty")
    private Date date;
    private boolean status;
    private Long objectId;
    private Long flatId;
    private Long managerId;
    private Long realtorId;
    private String counterparty;
    @NotNull(message = "Must not be empty")
    private Article article; // стаття
    @NotNull(message = "Must not be empty")
    private Double price;
    @NotNull(message = "Must not be empty")
    private Long currencyId; // валюта
    @NotNull(message = "Must not be empty")
    private Double admissionCourse;
    private String comment;

    public CashRegister build(){
        CashRegister cashRegister = new CashRegister();
        cashRegister.setId(id);
        cashRegister.setNumber(number);
        cashRegister.setDate(date);
        cashRegister.setEconomic(Economic.SPENDING);
        if (status){
            cashRegister.setStatus(StatusCashRegister.COMPLETED);
        } else {
            cashRegister.setStatus(StatusCashRegister.PLANNED);
        }
        if(article.equals(Article.COMMISSION_MANAGER)) {
            if (managerId != null){
                User user = new User();
                user.setId(managerId);
                cashRegister.setManager(user);
            }
            if (objectId != null) {
                Object object = new Object();
                object.setId(objectId);
                cashRegister.setObject(object);
            }
            if (flatId != null) {
                Flat flat = new Flat();
                flat.setId(flatId);
                cashRegister.setFlat(flat);
            }
        } else if(article.equals(Article.COMMISSION_AGENCIES)){
            if (realtorId != null){
                Realtor realtor = new Realtor();
                realtor.setId(realtorId);
                cashRegister.setRealtor(realtor);
            }
            if (objectId != null) {
                Object object = new Object();
                object.setId(objectId);
                cashRegister.setObject(object);
            }
            if (flatId != null) {
                Flat flat = new Flat();
                flat.setId(flatId);
                cashRegister.setFlat(flat);
            }
        } else if(article.equals(Article.CONSTRUCTION_COSTS)){
            cashRegister.setCounterparty(counterparty);
        }
        cashRegister.setArticle(article);
        cashRegister.setPrice(price);
        if (currencyId != null) {
            InternalCurrency currency = new InternalCurrency();
            currency.setId(currencyId);
            cashRegister.setCurrency(currency);
        }
        cashRegister.setAdmissionCourse(admissionCourse);
        cashRegister.setComment(comment);
        return cashRegister;
    }
}
