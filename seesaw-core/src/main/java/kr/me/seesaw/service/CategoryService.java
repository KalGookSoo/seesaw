package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.domain.Category;

public interface CategoryService {
    Category create(CreateCategoryCommand command);
    Category find(String id);
    Category update(String id, UpdateCategoryCommand command);
    void delete(String id);
}
