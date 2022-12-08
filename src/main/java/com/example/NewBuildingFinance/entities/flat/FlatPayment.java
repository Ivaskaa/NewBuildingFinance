package com.example.NewBuildingFinance.entities.flat;

import com.example.NewBuildingFinance.dto.flat.FlatPaymentTableDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatPaymentBarChartDto;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "flat_payments")
public class FlatPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer number;
    private Date date;
    private Double planned;
    private Double actually;
    private boolean paid;
    @JoinColumn(name = "flat_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Flat flat;
    private boolean deleted = false;

    @Formula("(select flat_payments.planned - flat_payments.actually from flat_payments where flat_payments.id = id)")
    private Double remains;

    public FlatPaymentTableDto build(){
        FlatPaymentTableDto flat = new FlatPaymentTableDto();
        flat.setId(id);
        flat.setNumber(number);
        flat.setDate(date);
        flat.setPlanned(planned);
        flat.setActually(actually);
        if (actually != null) {
            flat.setRemains(planned - actually);
        }
        return flat;
    }

    public StatisticFlatPaymentBarChartDto buildFlatPaymentForBarChart(){
        StatisticFlatPaymentBarChartDto flatPayment = new StatisticFlatPaymentBarChartDto();
        flatPayment.setPlaned(planned);
        flatPayment.setDate(date);
        if(paid){
            flatPayment.setFact(actually);
            flatPayment.setRemains(planned - actually);
        } else {
            flatPayment.setFact(0d);
            flatPayment.setRemains(planned);
        }
        return flatPayment;
    }

    @Override
    public String toString() {
        return "FlatPayment{" +
                "id=" + id +
                ", number=" + number +
                ", date=" + date +
                ", planned=" + planned +
                ", actually=" + actually +
                '}';
    }
}
