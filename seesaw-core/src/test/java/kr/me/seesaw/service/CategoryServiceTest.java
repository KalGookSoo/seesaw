package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.domain.vo.CategoryType;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test"})
@DataJpaTest
class CategoryServiceTest {
    private CategoryService categoryService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        categoryService = new DefaultCategoryService(categoryRepository);
    }

    @Test
    @DisplayName("카테고리 생성 시 카테고리를 반환합니다.")
    void createCategoryShould() {
        // given
        CreateCategoryCommand command = CreateCategoryCommand.builder()
                .name("name")
                .description("description")
                .type(CategoryType.NONE)
                .siteExposed(true)
                .siteExposedOrder(0)
                .exposed(true)
                .sequence(0)
                .siteId("siteId")
                .build();

        // when
        CategoryModel model = categoryService.createCategory(command);
        entityManager.flush();

        // then
        Assertions.assertNotNull(model);
        Assertions.assertNotNull(model.getId());
    }
}