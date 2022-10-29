package com.example.NewBuildingFinance.dto.flat;

import lombok.Data;
@Data
public class FlatTableDto {
    private Long id;
    private Integer number;
    private String object;
    private String status;
    private Double area; // площа
    private Integer price; // ціна
    private Integer advance = 0;
    private Integer entered;
    private Integer remains;
}
