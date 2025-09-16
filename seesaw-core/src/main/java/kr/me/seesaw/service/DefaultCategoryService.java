package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
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
        return new CategoryModel(savedCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryModel getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 카테고리가 존재하지 않습니다. id: " + id));
        return new CategoryModel(category);
    }

    @Override
    public CategoryModel update(String id, UpdateCategoryCommand command) {
        Category category = categoryRepository.getReferenceById(id);
        category.update(command);
        Category updatedCategory = categoryRepository.save(category);
        return new CategoryModel(updatedCategory);
    }

    @Override
    public void deleteCategoryById(String id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryModel> getCategoriesBySiteId(String siteId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "sequence");
        Collection<Category> categories = categoryRepository.findAllBySiteId(siteId, sort);
        List<CategoryModel> models = categories.stream()
                .map(CategoryModel::new)
                .toList();
        return HierarchicalFactory.build(models);
    }

}
