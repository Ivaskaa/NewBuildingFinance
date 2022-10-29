package com.example.NewBuildingFinance.dto.flat;

import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.FlatPayment;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FlatPaymentDto {
    private Long id;
    @NotNull(message = "Must not be empty")
    private Long number;
    @NotNull(message = "Must not be empty")
    private Date date;
    @NotNull(message = "Must not be empty")
    private Integer planned;
    @NotNull(message = "Must not be empty")
    private Long flatId;

    public FlatPayment build(){
        FlatPayment flatPayment = new FlatPayment();
        flatPayment.setId(id);
        flatPayment.setNumber(number);
        flatPayment.setDate(date);
        flatPayment.setPlanned(planned);
        Flat flat = new Flat();
        flat.setId(flatId);
        flatPayment.setFlat(flat);
        return flatPayment;
    }

    @Override
    public String toString() {
        return "FlatPaymentDto{" +
                "id=" + id +
                ", number=" + number +
                ", date=" + date +
                ", planned=" + planned +
                ", flatId=" + flatId +
                '}';
    }
}
