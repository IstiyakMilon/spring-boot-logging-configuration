package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageableInfo {
    private SortInfo sort;
    private long offset;
    private int pageNumber;
    private int pageSize;
    private boolean paged;
    private boolean unpaged;
}

