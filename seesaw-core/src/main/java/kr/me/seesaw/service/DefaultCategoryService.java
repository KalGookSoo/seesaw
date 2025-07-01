package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.domain.Category;
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
    public Category create(CreateCategoryCommand command) {
        Category category = Category.create(command);
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    @Override
    public Category find(String id) {
        return categoryRepository.findById(id).orElseThrow(NoSuchElementException::new);
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
