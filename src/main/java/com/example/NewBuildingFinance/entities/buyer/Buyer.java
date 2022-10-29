package com.example.NewBuildingFinance.entities.buyer;


import com.example.NewBuildingFinance.dto.BuyerTableDto;
import com.example.NewBuildingFinance.entities.agency.Agency;
import com.example.NewBuildingFinance.entities.agency.Realtor;
import com.example.NewBuildingFinance.entities.auth.User;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.example.NewBuildingFinance.service.RealtorService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
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
    private String passportSeries;
    private Integer passportNumber;
    private Integer passportWhoIssued;
    private String phone;
    private String email;
    private String note;
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

    public BuyerTableDto build(){
        BuyerTableDto buyer = new BuyerTableDto();
        buyer.setId(id);
        buyer.setName(name);
        buyer.setSurname(surname);
        buyer.setLastname(lastname);
        buyer.setPhone(phone);
        buyer.setEmail(email);
        buyer.setAgency(realtor.getAgency().getName());
        buyer.setRealtor(realtor.getName() + " " + realtor.getSurname());
        buyer.setManager(user.getName() + " " + user.getSurname());
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
                ", passportSeries=" + passportSeries +
                ", passportNumber=" + passportNumber +
                ", passportWhoIssued=" + passportWhoIssued +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", note='" + note + '\'' +
                ", realtor=" + realtor +
                '}';
    }
}
