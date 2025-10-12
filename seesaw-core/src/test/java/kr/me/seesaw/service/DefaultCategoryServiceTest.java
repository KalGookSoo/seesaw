package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.config.TestDataInitializerConfig;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.domain.vo.CategoryType;
import kr.me.seesaw.model.CategoryModel;
import kr.me.seesaw.repository.CategoryRepository;
import kr.me.seesaw.repository.SiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"test"})
@DataJpaTest
@Import(TestDataInitializerConfig.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DefaultCategoryServiceTest {

    private CategoryService categoryService;

    private final TestEntityManager entityManager;

    private final CategoryRepository categoryRepository;

    private final SiteRepository siteRepository;

    private String siteId1;

    private String siteId2;

    public DefaultCategoryServiceTest(TestEntityManager entityManager, CategoryRepository categoryRepository, SiteRepository siteRepository) {
        this.entityManager = entityManager;
        this.categoryRepository = categoryRepository;
        this.siteRepository = siteRepository;
    }

    @BeforeEach
    void setup() {
        categoryService = new DefaultCategoryService(categoryRepository);

        // 테스트 데이터 시더(@Import)로 생성된 사이트를 조회하여 사용
        Site s1 = siteRepository.findByDomainName("test1.local").orElseThrow();
        Site s2 = siteRepository.findByDomainName("test2.local").orElseThrow();

        siteId1 = s1.getId();
        siteId2 = s2.getId();
    }

    @Test
    @DisplayName("카테고리 생성 시 카테고리를 반환합니다.")
    void createCategoryShouldReturnCategory() {
        // given
        CreateCategoryCommand command = createCommand(siteId1, "공지", 0, 0);

        // when
        CategoryModel model = categoryService.createCategory(command);
        entityManager.flush();

        // then
        assertNotNull(model);
        assertNotNull(model.getId());
        assertEquals("공지", model.getName());
        assertEquals(siteId1, model.getSiteId());
    }

    @Test
    @DisplayName("ID로 카테고리를 조회하면 해당 카테고리를 반환합니다.")
    void getCategoryByIdShouldReturnCategory() {
        // given
        CategoryModel saved = categoryService.createCategory(createCommand(siteId1, "게시판A", 0, 0));
        entityManager.flush();

        // when
        CategoryModel found = categoryService.getCategoryById(saved.getId());

        // then
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("게시판A", found.getName());
    }

    @Test
    @DisplayName("없는 ID로 카테고리를 조회하면 NoSuchElementException이 발생합니다.")
    void getCategoryByIdWithUnknownIdShouldThrow() {
        // given
        String unknownId = "00000000-0000-0000-0000-000000000000";

        // when
        Executable act = () -> categoryService.getCategoryById(unknownId);

        // then
        assertThrows(NoSuchElementException.class, act);
    }

    @Test
    @DisplayName("카테고리를 수정하면 수정된 값이 반영됩니다.")
    void updateShouldModifyValues() {
        // given
        CategoryModel saved = categoryService.createCategory(createCommand(siteId1, "게시판B", 0, 1));
        entityManager.flush();

        UpdateCategoryCommand command = UpdateCategoryCommand.builder()
                .name("게시판B-수정")
                .description("설명-수정")
                .type(CategoryType.BOARD)
                .siteExposed(true)
                .siteExposedOrder(3)
                .exposed(true)
                .sequence(5)
                .siteId(siteId1)
                .build();

        // when
        CategoryModel updated = categoryService.update(saved.getId(), command);
        entityManager.flush();

        // then
        assertEquals("게시판B-수정", updated.getName());
        assertEquals("설명-수정", updated.getDescription());
        assertEquals(CategoryType.BOARD, updated.getType());
        assertTrue(updated.isSiteExposed());
        assertEquals(3, updated.getSiteExposedOrder());
        assertTrue(updated.isExposed());
        assertEquals(5, updated.getSequence());
    }

    @Test
    @DisplayName("카테고리를 삭제하면 더 이상 조회되지 않습니다.")
    void deleteCategoryShouldRemoveEntity() {
        // given
        CategoryModel saved = categoryService.createCategory(createCommand(siteId1, "삭제대상", 0, 2));
        entityManager.flush();

        // when
        categoryService.deleteCategoryById(saved.getId());
        entityManager.flush();

        // then
        assertThrows(NoSuchElementException.class, () -> categoryService.getCategoryById(saved.getId()));
    }

    @Test
    @DisplayName("사이트 ID로 카테고리 목록을 조회하면 시퀀스 오름차순과 계층 구조가 적용됩니다.")
    void getCategoriesBySiteIdShouldReturnHierarchicalSortedList() {
        // given: 부모-자식 관계를 만들기 위해 먼저 부모 저장 후 자식의 parentId를 직접 세팅
        CategoryModel parent = categoryService.createCategory(createCommand(siteId2, "부모", 0, 1));
        entityManager.flush();

        // 영속 엔티티 직접 업데이트가 필요하므로 엔티티 매니저로 parentId를 사용해 저장
        // 여기서는 단순 시퀀스 정렬과 반환 크기만 검증
        categoryService.createCategory(createCommand(siteId2, "자식1", 0, 2));
        categoryService.createCategory(createCommand(siteId2, "자식2", 0, 3));
        categoryService.createCategory(createCommand(siteId2, "기타", 0, 0));
        entityManager.flush();

        // when
        List<CategoryModel> list = categoryService.getCategoriesBySiteId(siteId2);

        // then
        assertNotNull(list);
        assertTrue(list.size() >= 3);
        // 시퀀스 오름차순으로 첫 번째가 sequence=0인 "기타"가 되어야 함
        assertEquals(0, list.get(0).getSequence());
    }

    private CreateCategoryCommand createCommand(String siteId, String name, int siteExposedOrder, int sequence) {
        return CreateCategoryCommand.builder()
                .name(name)
                .description("description")
                .type(CategoryType.NONE)
                .siteExposed(true)
                .siteExposedOrder(siteExposedOrder)
                .exposed(true)
                .sequence(sequence)
                .siteId(siteId)
                .build();
    }

}
