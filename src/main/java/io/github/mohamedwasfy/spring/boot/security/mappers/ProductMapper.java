package io.github.mohamedwasfy.spring.boot.security.mappers;

import io.github.mohamedwasfy.spring.boot.security.dtos.request.CreateProductRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.request.UpdateProductRequest;
import io.github.mohamedwasfy.spring.boot.security.dtos.response.ProductDto;
import io.github.mohamedwasfy.spring.boot.security.entities.Category;
import io.github.mohamedwasfy.spring.boot.security.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping (
        target = "categoryId",
        source = "category"
    )
    ProductDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    Product toEntity(CreateProductRequest request);

    void update(UpdateProductRequest request, @MappingTarget Product product);

    // ↓ MapStruct auto-detects this and uses it whenever it needs Byte → Category
    default Category map(Byte categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    default Byte map(Category category) {
        if (category == null) return null;
        return (byte) category.getId();
    }
}