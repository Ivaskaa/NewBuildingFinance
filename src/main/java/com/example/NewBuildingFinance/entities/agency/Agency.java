package com.example.NewBuildingFinance.entities.agency;

import com.example.NewBuildingFinance.dto.AgencyTableDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

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
    private Integer count; //sale count
    @JoinColumn(name = "agency_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JsonBackReference
    private Set<Realtor> realtors;

    public AgencyTableDto build(){
        AgencyTableDto agency = new AgencyTableDto();
        agency.setId(id);
        agency.setName(name);
        for(Realtor realtor : realtors){
            if(realtor.isDirector() && realtor.getAgency().equals(this)){
                agency.setDirectorName(realtor.getName());
                agency.setDirectorPhone(realtor.getPhone());
                agency.setDirectorEmail(realtor.getEmail());
            }
        }
        agency.setCount(count);
        return agency;
    }

    @Override
    public String toString() {
        return "Agency{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", count=" + count +
                ", realtors=" + realtors +
                '}';
    }
}
