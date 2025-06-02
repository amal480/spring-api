package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "quantity missing")
    @Min(value = 1,message = "size small")
    @Max(value = 100,message = "size big")
    private Integer quantity;
}
