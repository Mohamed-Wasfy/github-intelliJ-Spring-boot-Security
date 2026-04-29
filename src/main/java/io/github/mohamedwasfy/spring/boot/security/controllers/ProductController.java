package io.github.mohamedwasfy.spring.boot.security.controllers;

import io.github.mohamedwasfy.spring.boot.security.dtos.request.CreateProductRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.UpdateProductRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.response.ProductDto;
import io.github.mohamedwasfy.spring.boot.security.entities.Product;
import io.github.mohamedwasfy.spring.boot.security.mappers.ProductMapper;
import io.github.mohamedwasfy.spring.boot.security.repositories.CategoryRepository;
import io.github.mohamedwasfy.spring.boot.security.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts (
            @RequestParam (
                required = false,
                defaultValue = ""
            ) Byte categoryId
    ) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }
        return products.stream()
                       .map(productMapper::toDto)
                       .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct (
            @Valid @RequestBody CreateProductRequest request,
            UriComponentsBuilder builder
    ) {
        // Check Category Existence.
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        // Mapping Category to Product.
        var product = productMapper.toEntity(request);
        product.setCategory(category);
        product = productRepository.save(product);

        // URI Location Header
        var path = builder.path("/products/{id}").buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(path).body(productMapper.toDto(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct (
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request
    ) {
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            return ResponseEntity.notFound().build();

        productMapper.update(request, product);
        productRepository.save(product);

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct (
            @PathVariable Long id
    ) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            return ResponseEntity.notFound().build();

        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

//    @ExceptionHandler({MethodArgumentNotValidException.class})
//    public ResponseEntity<Map<String, String>> handleValidationExceptions (MethodArgumentNotValidException exception) {
//        var errors = new HashMap<String, String>();
//
//        exception.getBindingResult().getFieldErrors().forEach((fieldError) -> {
//            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
//        });
//
//        return ResponseEntity.badRequest().body(errors);
//    }
}

// Introduction to Jakarta Validation.
// Handling Validation errors in Controllers
// Implementing global error handling.
// Creating Custom Validation Annotations.
// Validating business rules.
// JAKARTA VALIDATION
// A specification that provides a set of annotations to validate user input.

// String Validation
// @NotBlank
// @NotEmpty
// @Size
// @Pattern
// @Email
// @CreditCardNumber

// NUMBER Validation
// @Min
// @Max
// @Positive
// @Negative
// @PositiveOrZero
// @NegativeOrZero

// DATE/TIME VALIDATION
// @Past
// @PastOrPresent
// @Future
// @FutureOrPresent

// GENERAL VALIDATION
// @NonNull
// IMPLEMENTING CUSTOM VALIDATION.
