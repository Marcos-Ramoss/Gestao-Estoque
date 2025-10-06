package com.controleestoque.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto {
    
    private int page;
    private int limit;
    private String orderBy;
    private String orderDirection;
}




