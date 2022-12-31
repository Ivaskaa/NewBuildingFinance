package com.example.NewBuildingFinance.entities.flat;

import com.example.NewBuildingFinance.dto.flat.FlatTableDto;
import com.example.NewBuildingFinance.dto.statistic.flats.StatisticFlatsTableDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.object.Object;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "flats")
public class Flat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "object_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Object object;
    @Enumerated(EnumType.STRING)
    private StatusFlat status;
    @JoinColumn(name = "buyer_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Buyer buyer;
    @JoinColumn(name = "realtor_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Realtor realtor;
    @JoinColumn(name = "flat_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<CashRegister> cashRegisterSet;
    @JoinColumn(name = "flat_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<FlatPayment> flatPayments;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Contract contract;
    private Double area; // площа
    private Double price; // ціна
    private Double salePrice; // ціна продажі

    private Integer number;
    private Integer quantityRooms;
    private Integer floor;
    private String description;

    private Integer agency; // %агенство
    private Integer manager; // %менеджер

    private boolean deleted = false;

    @Formula("(select max(fp.planned)\n" +
            "from flat_payments fp\n" +
            "where fp.date = (select min(flat_payments.date)\n" +
            "                from flat_payments\n" +
            "                         INNER JOIN flats f on flat_payments.flat_id = f.id\n" +
            "                where flat_payments.flat_id = id\n" +
            "                and flat_payments.deleted = false\n" +
            "                order by flat_payments.flat_id)\n" +
            "and fp.flat_id = id\n" +
            "and fp.deleted = false\n" +
            "order by fp.flat_id)")
    private Double advance;
    @Formula("(select sum(fp.actually)\n" +
            "from flat_payments fp\n" +
            "where fp.flat_id = id\n" +
            "and fp.deleted = false)")
    private Double entered;
    @Formula("(select f.sale_price - (select sum(ifnull(fp.actually, 0))\n" +
            "                       from flat_payments fp\n" +
            "                       where fp.flat_id = id\n" +
            "                       group by fp.flat_id)\n" +
            "from flats f\n" +
            "where f.id = id)")
    private Double remains;
    @Formula("(select sum(fp.planned - fp.actually)\n" +
            "        from flat_payments fp\n" +
            "        where fp.actually is not null\n" +
            "        and fp.flat_id = id\n" +
            "        group by fp.flat_id)")
    private Double debt;
    @Formula("(select f.price - f.sale_price\n" +
            "        from flats f\n" +
            "        where f.id = id)")
    private Double sale;


    public FlatTableDto buildTableDto(){
        FlatTableDto flat = new FlatTableDto();
        flat.setId(id);
        flat.setNumber(number);
        flat.setObject(object.getHouse() + "(" + object.getSection() + ")");
        flat.setStatus(status.getValue());
        flat.setArea(area);
        flat.setPrice(price);

        if(advance == null){
            advance = 0d;
        }
        flat.setAdvance(advance);

        if(entered == null){
            entered = 0d;
        }
        flat.setEntered(entered);
        if(remains == null){
            remains = 0d;
        }
        flat.setRemains(remains);
        return flat;
    }

    public StatisticFlatsTableDto buildStatisticTableDto(){
        StatisticFlatsTableDto statisticFlat = new StatisticFlatsTableDto();
        statisticFlat.setFlatId(id);
        statisticFlat.setFlatNumber(number);
        statisticFlat.setObject(object.getHouse() + "(" + object.getSection() + ")");

        statisticFlat.setPrice(price);
        statisticFlat.setSalePrice(salePrice);

        statisticFlat.setFact(entered);
        statisticFlat.setRemains(remains);
        statisticFlat.setDebt(debt);

        statisticFlat.setStatus(status.getValue());
        if(contract != null) {
            statisticFlat.setContractId(contract.getId());
        }
        if(buyer != null){
            statisticFlat.setBuyer(buyer.getSurname() + " " + buyer.getName());
        } else {
            statisticFlat.setBuyer("None");
        }
        if(realtor != null){
            statisticFlat.setRealtor(realtor.getSurname() + " " + realtor.getName());
        } else {
            statisticFlat.setRealtor("None");
        }
        statisticFlat.setSale(price - salePrice);

        return statisticFlat;
    }

    @Override
    public String
    toString() {
        return "Flat{" +
                "id=" + id +
                ", object=" + object +
                ", status=" + status +
                ", buyer=" + buyer +
                ", realtor=" + realtor +
                ", area=" + area +
                ", price=" + price +
                ", salePrice=" + salePrice +
                ", number=" + number +
                ", quantityRooms=" + quantityRooms +
                ", floor=" + floor +
                ", description='" + description + '\'' +
                ", agency=" + agency +
                ", manager=" + manager +
                '}';
    }
}
