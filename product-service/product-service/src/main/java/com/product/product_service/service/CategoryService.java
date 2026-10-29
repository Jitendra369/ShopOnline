package com.product.product_service.service;

import com.product.product_service.Sequence;
import com.product.product_service.dto.CategoryDto;
import com.product.product_service.entity.CategoryEntity;
import com.product.product_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public CategoryDto saveCategoryDetails(CategoryDto dto) {
        CategoryEntity category = new CategoryEntity();
        copyFields(dto, category);
        category.setId(sequenceGeneratorService.generateSequence(Sequence.CATEGORY_SEQUENCE.name()));
        category.setActive(true);
        return mapToDto(categoryRepository.save(category));
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findByActiveTrue().stream().map(this::mapToDto).toList();
    }

    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findByParentIdIsNullAndActiveTrue().stream().map(this::mapToDto).toList();
    }

    public List<CategoryDto> getChildren(String parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId).stream().map(this::mapToDto).toList();
    }

    public CategoryDto getCategory(Long id) {
        return mapToDto(findActiveCategory(id));
    }

    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        CategoryEntity category = findActiveCategory(id);
        copyFields(dto, category);
        return mapToDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        CategoryEntity category = findActiveCategory(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryEntity findActiveCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (!category.isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        return category;
    }

    private void copyFields(CategoryDto dto, CategoryEntity category) {
        setIfPresent(dto.getName(), category::setName);
        setIfPresent(dto.getCategoryCode(), category::setCategoryCode);
        setIfPresent(dto.getSlug(), category::setSlug);
        setIfPresent(dto.getDescription(), category::setDescription);
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId().isBlank() ? null : dto.getParentId());
        }
    }

    private void setIfPresent(String value, Consumer<String> setter) {
        if (value != null) setter.accept(value);
    }

    private CategoryDto mapToDto(CategoryEntity category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCategoryCode(category.getCategoryCode());
        dto.setSlug(category.getSlug());
        dto.setParentId(category.getParentId());
        dto.setDescription(category.getDescription());
        dto.setActive(category.isActive());
        dto.setCreatedDate(category.getCreatedDate());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
