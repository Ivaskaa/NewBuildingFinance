package com.example.NewBuildingFinance.dto.cashRegister;

import com.example.NewBuildingFinance.entities.cashRegister.Article;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.cashRegister.Economic;
import com.example.NewBuildingFinance.entities.cashRegister.StatusCashRegister;
import com.example.NewBuildingFinance.entities.currency.InternalCurrency;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import com.example.NewBuildingFinance.entities.object.Object;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class IncomeSaveDto {
    private Long id;
    @NotNull(message = "Must not be empty")
    private Long number;
    @NotNull(message = "Must not be empty")
    private Date date;
    private boolean status;
    private Long objectId;
    private Long flatId;
    private Long flatPaymentId;
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
        cashRegister.setEconomic(Economic.INCOME);
        if (status){
            cashRegister.setStatus(StatusCashRegister.COMPLETED);
        } else {
            cashRegister.setStatus(StatusCashRegister.PLANNED);
        }
        if(article.equals(Article.FLAT_PAYMENT)) {
            if (objectId != null) {
                Object object = new Object();
                object.setId(objectId);
                cashRegister.setObject(object);
            }
            if (flatPaymentId != null) {
                FlatPayment flatPayment = new FlatPayment();
                flatPayment.setId(flatPaymentId);
                cashRegister.setFlatPayment(flatPayment);
            }
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
