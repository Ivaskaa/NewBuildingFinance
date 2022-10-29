package com.example.NewBuildingFinance.entities.flat;

import com.example.NewBuildingFinance.dto.flat.FlatTableDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.cashRegister.CashRegister;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.object.Object;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Set<CashRegister> cashRegisterSet;
    @JoinColumn(name = "flat_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<FlatPayment> flatPayments;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Contract contract;
    private Double area; // площа
    private Integer price; // ціна
    private Integer salePrice; // ціна продажі
    private Integer advance; // аванс

    private Integer number;
    private Integer quantityRooms;
    private Integer floor;
    private String description;

    private Integer agency; // %агенство
    private Integer manager; // %менеджер

    private boolean deleted = false;

    public FlatTableDto buildTableDto(){
        FlatTableDto flat = new FlatTableDto();
        flat.setId(id);
        flat.setNumber(number);
        flat.setObject(object.getHouse() + "(" + object.getSection() + ")");
        flat.setStatus(status.getValue());
        flat.setArea(area);
        flat.setPrice(price);
        flat.setEntered(1);
        flat.setRemains(price - 1);
        return flat;
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
                ", advance=" + advance +
                ", number=" + number +
                ", quantityRooms=" + quantityRooms +
                ", floor=" + floor +
                ", description='" + description + '\'' +
                ", agency=" + agency +
                ", manager=" + manager +
                '}';
    }
}
