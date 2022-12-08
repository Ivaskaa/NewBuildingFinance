package com.example.NewBuildingFinance.entities.agency;

import com.example.NewBuildingFinance.dto.agency.AgencyTableDto;
import com.example.NewBuildingFinance.entities.contract.Contract;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "agencies")
public class Agency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @JoinColumn(name = "agency_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<Realtor> realtors;
    private boolean deleted = false;

    @Formula("(select realtors.name from realtors where realtors.agency_id = " +
            "       (select id from agencies where agencies.id = realtors.agency_id) " +
            "       and realtors.director = true)")
    private String directorName;

    @Formula("(select realtors.surname from realtors where realtors.agency_id = " +
            "       (select id from agencies where agencies.id = realtors.agency_id) " +
            "       and realtors.director = true)")
    private String directorSurname;

    @Formula("(select realtors.phone from realtors where realtors.agency_id = " +
            "       (select id from agencies where agencies.id = realtors.agency_id) " +
            "       and realtors.director = true)")
    private String directorPhone;

    @Formula("(select realtors.email from realtors where realtors.agency_id = " +
            "       (select id from agencies where agencies.id = realtors.agency_id) " +
            "       and realtors.director = true)")
    private String directorEmail;

    @Formula("(select count(*) from contracts where contracts.flat_id =\n" +
            "                (select flats.id from flats where flats.id = contracts.flat_id and flats.contract_id IS NOT NULL and flats.realtor_id =\n" +
            "                                (select realtors.id from realtors where realtors.id = flats.realtor_id and realtors.agency_id = id)))")
    private Integer countContracts;


    public AgencyTableDto build(){
        AgencyTableDto agency = new AgencyTableDto();
        agency.setId(id);
        agency.setName(name);
        agency.setDirectorName(directorName);
        agency.setDirectorEmail(directorEmail);
        agency.setDirectorPhone(directorPhone);
        agency.setCount(countContracts);
        return agency;
    }


    @Override
    public String toString() {
        return "Agency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", deleted=" + deleted +
                ", directorName='" + directorName + '\'' +
                ", directorSurname='" + directorSurname + '\'' +
                ", directorPhone='" + directorPhone + '\'' +
                ", directorEmail='" + directorEmail + '\'' +
                ", countContracts=" + countContracts +
                '}';
    }
}
