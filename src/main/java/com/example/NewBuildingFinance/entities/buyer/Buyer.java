package com.example.NewBuildingFinance.entities.buyer;


import com.example.NewBuildingFinance.dto.buyer.BuyerTableDto;
import com.example.NewBuildingFinance.dto.buyer.BuyerUploadDto;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.Permission;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.entities.flat.StatusFlat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "buyers")
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String lastname;
    private String address;
    private Long idNumber;

    @Enumerated(EnumType.STRING)
    private DocumentStyle documentStyle;

    private String passportSeries;
    private Integer passportNumber;
    private String passportWhoIssued;

    private Long idCardNumber;
    private Integer idCardWhoIssued;
    private String phone;
    private String email;
    private String note;

    private String password;
    @JoinColumn(name = "buyer_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<Flat> flats;
    @JoinColumn(name = "realtor_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Realtor realtor;
    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private User user;
    @JoinColumn(name = "permission_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Permission permission;
    private boolean active = true;
    private boolean deleted = false;

    @Formula("(select count(*) from flats where flats.buyer_id = id and flats.contract_id IS NOT NULL)")
    private long contractSize;

    public BuyerTableDto build(){
        BuyerTableDto buyer = new BuyerTableDto();
        buyer.setId(id);
        buyer.setName(name);

        Integer count = 0;
        for(Flat flat : flats){
            if(flat.getContract() != null){
                count++;
            }
        }
        buyer.setCount(count);

        buyer.setSurname(surname);
        buyer.setLastname(lastname);
        buyer.setPhone(phone);
        buyer.setEmail(email);
        if(realtor.getAgency() != null) {
            buyer.setAgency(realtor.getAgency().getName());
        }
        buyer.setRealtor(realtor.getName() + " " + realtor.getSurname());
        buyer.setManager(user.getName() + " " + user.getSurname());
        return buyer;
    }

    public BuyerUploadDto buildUploadDto(){
        BuyerUploadDto buyer = new BuyerUploadDto();
        buyer.setId(id);
        buyer.setName(name);
        buyer.setSurname(surname);
        buyer.setLastname(lastname);
        buyer.setAddress(address);
        buyer.setIdNumber(idNumber);

        buyer.setDocumentStyle(documentStyle.toString());
        if(documentStyle.equals(DocumentStyle.ID_CARD)){
            buyer.setIdCardNumber(idCardNumber);
            buyer.setIdCardWhoIssued(idCardWhoIssued);
        } else {
            buyer.setPassportSeries(passportSeries);
            buyer.setPassportNumber(passportNumber);
            buyer.setPassportWhoIssued(passportWhoIssued);
        }

        buyer.setPhone(phone);
        buyer.setEmail(email);
        buyer.setNote(note);

        buyer.setRealtorId(realtor.getId());
        if(realtor.getAgency() != null) {
            buyer.setAgencyId(realtor.getAgency().getId());
        }
        buyer.setManagerId(user.getId());
        return buyer;
    }

    @Override
    public String toString() {
        return "Buyer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", address='" + address + '\'' +
                ", idNumber=" + idNumber +
                ", documentStyle=" + documentStyle +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNumber=" + passportNumber +
                ", passportWhoIssued='" + passportWhoIssued + '\'' +
                ", idCardNumber=" + idCardNumber +
                ", idCardWhoIssued=" + idCardWhoIssued +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", note='" + note + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
