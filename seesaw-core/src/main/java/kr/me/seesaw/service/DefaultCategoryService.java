package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryModel createCategory(CreateCategoryCommand command) {
        Category category = Category.create(command);
        Category savedCategory = categoryRepository.save(category);
        return CategoryModel.builder()
                .id(savedCategory.getId())
                .createdBy(savedCategory.getCreatedBy())
                .createdIp(savedCategory.getCreatedIp())
                .createdDate(savedCategory.getCreatedDate())
                .lastModifiedBy(savedCategory.getLastModifiedBy())
                .lastModifiedIp(savedCategory.getLastModifiedIp())
                .lastModifiedDate(savedCategory.getLastModifiedDate())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .type(savedCategory.getType())
                .siteExposed(savedCategory.isSiteExposed())
                .siteExposedOrder(savedCategory.getSiteExposedOrder())
                .exposed(savedCategory.isExposed())
                .sequence(savedCategory.getSequence())
                .siteId(savedCategory.getSiteId())
                .build();
    }
    
    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 카테고리가 존재하지 않습니다. id: " + id));
    }

    @Override
    public Category update(String id, UpdateCategoryCommand command) {
        Category category = categoryRepository.getReferenceById(id);
        category.update(command);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(String id) {
        categoryRepository.deleteById(id);
    }
}
