package com.example.NewBuildingFinance.entities.object;

import com.example.NewBuildingFinance.dto.ObjectTableDto;
import com.example.NewBuildingFinance.entities.flat.Flat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "objects")
public class Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String house;
    @NotEmpty
    private String section;
    @NotEmpty
    private String address;
    @JoinColumn(name = "object_id")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonBackReference
    private Set<Flat> flats;
    @Enumerated(EnumType.STRING)
    private StatusObject status;
    private Integer agency;
    private Integer manager;
    private boolean active;

    public ObjectTableDto build(){
        ObjectTableDto object = new ObjectTableDto();
        object.setId(id);
        object.setHouse(house);
        object.setSection(section);
        object.setAddress(address);
        object.setStatus(status.getValue());
        object.setAgency(agency);
        object.setManager(manager);
        object.setActive(active);
        return object;
    }

    @Override
    public String toString() {
        return "Object{" +
                "id=" + id +
                ", house='" + house + '\'' +
                ", section='" + section + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", agency='" + agency + '\'' +
                ", manager='" + manager + '\'' +
                ", active=" + active +
                '}';
    }
}
