package com.example.NewBuildingFinance.dto.flat;

import lombok.Data;
@Data
public class FlatTableDto {
    private Long id;
    private Integer number;
    private String object;
    private String status;
    private Double area; // площа
    private Double price; // ціна
    private Double advance;
    private Double entered;
    private Double remains;
}
