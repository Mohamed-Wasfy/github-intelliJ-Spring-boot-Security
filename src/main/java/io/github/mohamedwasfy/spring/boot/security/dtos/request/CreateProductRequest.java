package io.github.mohamedwasfy.spring.boot.security.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateProductRequest {
    @NotBlank(message = "Product name is mandatory")
    @Size(max = 100, message = "Product name is must be less than 100 Characters")
    private String name;
    @NotBlank(message = "Product description is mandatory")
    @Size(max = 500, message = "Product description must be less than 500 Characters")
    private String description;
    @NotNull(message = "Product Price is mandatory")
    private BigDecimal price;
    @NotNull(message = "Product must have a Category")
    private Byte categoryId;
}

// GLOBAL ERROR HANDLING.