package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.domain.Category;
import kr.me.seesaw.model.CategoryModel;

public interface CategoryService {
    CategoryModel createCategory(CreateCategoryCommand command);

    Category getCategoryById(String id);

    Category update(String id, UpdateCategoryCommand command);

    void delete(String id);
}
