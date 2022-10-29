package com.example.NewBuildingFinance.dto.flat;

import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.buyer.Buyer;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.example.NewBuildingFinance.entities.object.Object;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FlatSaveDto {
    private Long id;
    @NotNull(message = "Must not be empty")
    private Long objectId;
    @NotNull(message = "Must not be empty")
    private StatusFlat status;
    @NotNull(message = "Must not be empty")
    private Double area; // площа
    @NotNull(message = "Must not be empty")
    private Integer price; // ціна
    @NotNull(message = "Must not be empty")
    private Integer salePrice; // ціна продажі

    @NotNull(message = "Must not be empty")
    private Integer number;
    @NotNull(message = "Must not be empty")
    private Integer quantityRooms;
    @NotNull(message = "Must not be empty")
    private Integer floor;
    private String description;

    @NotNull(message = "Must not be empty")
    private Integer agency; // %агенство
    @NotNull(message = "Must not be empty")
    private Integer manager; // %менеджер
    private Long buyerId;
    private Long agencyId;
    private Long realtorId;

    public Flat build(){
        Flat flat = new Flat();
        flat.setId(id);
        Object object = new Object();
        object.setId(objectId);
        flat.setObject(object);
        flat.setStatus(status);
        flat.setArea(area);
        flat.setPrice(price);
        flat.setSalePrice(salePrice);
        flat.setNumber(number);
        flat.setQuantityRooms(quantityRooms);
        flat.setFloor(floor);
        flat.setDescription(description);
        flat.setAgency(agency);
        flat.setManager(manager);
        if (buyerId != null) {
            Buyer buyer = new Buyer();
            buyer.setId(buyerId);
            flat.setBuyer(buyer);
        }
        if (realtorId != null) {
            Realtor realtor = new Realtor();
            realtor.setId(realtorId);
            flat.setRealtor(realtor);
        }
        return flat;
    }
}
