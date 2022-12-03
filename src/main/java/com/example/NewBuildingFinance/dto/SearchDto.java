package com.example.NewBuildingFinance.dto;

import lombok.Data;

@Data
public abstract class SearchDto {
    private Integer page;
    private Integer size;
    private String sortingField;
    private String sortingDirection;
}
