package com.example.NewBuildingFinance.entities.agency;

import com.example.NewBuildingFinance.entities.flat.Flat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "realtors")
public class Realtor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    @JoinColumn(name = "agency_id")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonManagedReference
    private Agency agency;
    @JoinColumn(name = "realtor_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<Flat> flats;
    private boolean director;

    @Override
    public String toString() {
        return "Realtor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", agency=" + agency +
                ", director=" + director +
                '}';
    }
}
