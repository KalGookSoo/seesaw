package kr.me.seesaw.service;

import jakarta.persistence.EntityNotFoundException;
import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.domain.vo.Address;
import kr.me.seesaw.model.SiteModel;
import kr.me.seesaw.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@ActiveProfiles({"test"})
@DataJpaTest
class DefaultSiteServiceTest {

    private SiteService siteService;

    @TempDir
    private Path tempDir;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SiteRepository siteRepository;

    @MockitoBean
    private AttachmentRepository attachmentRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private RoleMappingRepository roleMappingRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        String filepath = tempDir + "/";
        ArticleSearchRepository articleSearchRepository = new ArticleSearchRepository(entityManager.getEntityManager());
        siteService = new DefaultSiteService(
                filepath,
                siteRepository,
                attachmentRepository,
                categoryRepository,
                articleSearchRepository,
                roleMappingRepository,
                userRepository
        );
    }

    @Test
    @DisplayName("사이트 생성 시 사이트 모델을 반환한다.")
    void createSiteShouldReturnSiteModel() throws IOException {
        // given
        CreateSiteCommand createSiteCommand = new CreateSiteCommand(
                "site-1",
                "",
                "",
                "",
                true,
                false,
                "",
                Address.empty(),
                "",
                "",
                "",
                new MockMultipartFile("profileImage", "profile-image.jpeg", MediaType.IMAGE_JPEG_VALUE, new byte[4]),
                new MockMultipartFile("backgroundImage", "background-image.jpeg", MediaType.IMAGE_JPEG_VALUE, new byte[4])
        );

        // when
        SiteModel siteModel = siteService.createSite(createSiteCommand);
        entityManager.flush();

        // then
        Assertions.assertNotNull(siteModel);
    }

    @Test
    @DisplayName("존재하지 않는 사이트 삭제 시 EntityNotFoundException을 던진다.")
    void deleteSiteShouldThrowEntityNotFoundException() {
        // given
        String id = UUID.randomUUID().toString();

        // when
        Exception exception = Assertions.assertThrows(Exception.class, () -> siteService.deleteSite(id));

        // then
        Assertions.assertEquals(EntityNotFoundException.class, exception.getClass());
    }

}