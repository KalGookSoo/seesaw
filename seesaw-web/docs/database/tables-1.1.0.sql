CREATE TABLE tb_attachment
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    mime_type          VARCHAR(255),
    name               VARCHAR(255),
    original_name      VARCHAR(255),
    path_name          VARCHAR(255),
    reference_id       VARCHAR(36),
    size               BIGINT      NOT NULL
);

COMMENT ON TABLE tb_attachment IS '첨부파일';

COMMENT ON COLUMN tb_attachment.id IS '식별자';

COMMENT ON COLUMN tb_attachment.created_by IS '생성자';

COMMENT ON COLUMN tb_attachment.created_date IS '생성일시';

COMMENT ON COLUMN tb_attachment.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_attachment.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_attachment.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_attachment.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_attachment.version IS '버전';

COMMENT ON COLUMN tb_attachment.mime_type IS 'MIME 타입';

COMMENT ON COLUMN tb_attachment.name IS '이름';

COMMENT ON COLUMN tb_attachment.original_name IS '원본이름';

COMMENT ON COLUMN tb_attachment.path_name IS '경로명';

COMMENT ON COLUMN tb_attachment.reference_id IS '참조 식별자';

COMMENT ON COLUMN tb_attachment.size IS '크기';

ALTER TABLE tb_attachment
    OWNER TO seesaw_admin;

CREATE TABLE tb_code
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    parent_id          VARCHAR(36)
        CONSTRAINT fk_tb_code_parent
            REFERENCES tb_code,
    description        VARCHAR(255),
    name               VARCHAR(255),
    sequence           INTEGER
);

COMMENT ON TABLE tb_code IS '코드';

COMMENT ON COLUMN tb_code.id IS '식별자';

COMMENT ON COLUMN tb_code.created_by IS '생성자';

COMMENT ON COLUMN tb_code.created_date IS '생성일시';

COMMENT ON COLUMN tb_code.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_code.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_code.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_code.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_code.version IS '버전';

COMMENT ON COLUMN tb_code.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_code.description IS '설명';

COMMENT ON COLUMN tb_code.name IS '이름';

COMMENT ON COLUMN tb_code.sequence IS '순서';

ALTER TABLE tb_code
    OWNER TO seesaw_admin;

CREATE TABLE tb_menu
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    parent_id          VARCHAR(36)
        CONSTRAINT fk_tb_menu_parent
            REFERENCES tb_menu,
    name               VARCHAR(255),
    sequence           INTEGER,
    uri                VARCHAR(255)
);

COMMENT ON TABLE tb_menu IS '메뉴';

COMMENT ON COLUMN tb_menu.id IS '식별자';

COMMENT ON COLUMN tb_menu.created_by IS '생성자';

COMMENT ON COLUMN tb_menu.created_date IS '생성일시';

COMMENT ON COLUMN tb_menu.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_menu.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_menu.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_menu.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_menu.version IS '버전';

COMMENT ON COLUMN tb_menu.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_menu.name IS '이름';

COMMENT ON COLUMN tb_menu.sequence IS '순번';

COMMENT ON COLUMN tb_menu.uri IS 'URI';

ALTER TABLE tb_menu
    OWNER TO seesaw_admin;

CREATE TABLE tb_remember_me_token
(
    series    VARCHAR(255) NOT NULL
        PRIMARY KEY,
    last_used TIMESTAMP(6) NOT NULL,
    token     VARCHAR(64)  NOT NULL,
    username  VARCHAR(64)  NOT NULL
);

ALTER TABLE tb_remember_me_token
    OWNER TO seesaw_admin;

CREATE TABLE tb_role
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    alias              VARCHAR(255),
    name               VARCHAR(255)
        CONSTRAINT uq_tb_role_name
            UNIQUE
);

COMMENT ON TABLE tb_role IS '역할';

COMMENT ON COLUMN tb_role.id IS '식별자';

COMMENT ON COLUMN tb_role.created_by IS '생성자';

COMMENT ON COLUMN tb_role.created_date IS '생성일시';

COMMENT ON COLUMN tb_role.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_role.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_role.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_role.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_role.version IS '버전';

COMMENT ON COLUMN tb_role.alias IS '별칭';

COMMENT ON COLUMN tb_role.name IS '이름';

ALTER TABLE tb_role
    OWNER TO seesaw_admin;

CREATE TABLE tb_menu_role
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    menu_id            VARCHAR(36)
        CONSTRAINT fk_tb_menu_role
            REFERENCES tb_menu,
    role_id            VARCHAR(36)
        CONSTRAINT fk_tb_role_menu
            REFERENCES tb_role
);

COMMENT ON TABLE tb_menu_role IS '메뉴 역할 매핑';

COMMENT ON COLUMN tb_menu_role.id IS '식별자';

COMMENT ON COLUMN tb_menu_role.created_by IS '생성자';

COMMENT ON COLUMN tb_menu_role.created_date IS '생성일시';

COMMENT ON COLUMN tb_menu_role.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_menu_role.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_menu_role.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_menu_role.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_menu_role.version IS '버전';

ALTER TABLE tb_menu_role
    OWNER TO seesaw_admin;

CREATE TABLE tb_site
(
    id                    VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by            VARCHAR(255),
    created_date          TIMESTAMP(6),
    created_ip            VARCHAR(45),
    last_modified_by      VARCHAR(255),
    last_modified_date    TIMESTAMP(6),
    last_modified_ip      VARCHAR(45),
    version               INTEGER     NOT NULL,
    parent_id             VARCHAR(36)
        CONSTRAINT fk_tb_site_parent
            REFERENCES tb_site,
    address               VARCHAR(255),
    zipcode               VARCHAR(255),
    contact_number        VARCHAR(255),
    content               TEXT,
    description           VARCHAR(255),
    distribution_code     VARCHAR(255),
    domain_name           VARCHAR(255),
    image_exposed         BOOLEAN     NOT NULL,
    intro                 VARCHAR(255),
    name                  VARCHAR(255),
    search_engine_exposed BOOLEAN     NOT NULL,
    tags                  VARCHAR(255)
);

COMMENT ON TABLE tb_site IS '사이트';

COMMENT ON COLUMN tb_site.id IS '식별자';

COMMENT ON COLUMN tb_site.created_by IS '생성자';

COMMENT ON COLUMN tb_site.created_date IS '생성일시';

COMMENT ON COLUMN tb_site.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_site.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_site.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_site.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_site.version IS '버전';

COMMENT ON COLUMN tb_site.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_site.address IS '주소';

COMMENT ON COLUMN tb_site.contact_number IS '연락처';

COMMENT ON COLUMN tb_site.content IS '본문';

COMMENT ON COLUMN tb_site.description IS '설명';

COMMENT ON COLUMN tb_site.distribution_code IS '분류코드';

COMMENT ON COLUMN tb_site.domain_name IS '도메인이름';

COMMENT ON COLUMN tb_site.image_exposed IS '이미지 노출여부';

COMMENT ON COLUMN tb_site.intro IS '소개글';

COMMENT ON COLUMN tb_site.name IS '이름';

COMMENT ON COLUMN tb_site.search_engine_exposed IS '검색엔진 노출여부';

COMMENT ON COLUMN tb_site.tags IS '태그';

ALTER TABLE tb_site
    OWNER TO seesaw_admin;

CREATE TABLE tb_category
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    parent_id          VARCHAR(36)
        CONSTRAINT fk_tb_category_parent
            REFERENCES tb_category,
    description        VARCHAR(255),
    exposed            BOOLEAN     NOT NULL,
    name               VARCHAR(255),
    sequence           INTEGER,
    site_exposed       BOOLEAN     NOT NULL,
    site_exposed_order INTEGER     NOT NULL,
    site_id            VARCHAR(36)
        CONSTRAINT fk_tb_category_site
            REFERENCES tb_site,
    type               VARCHAR(255)
        CONSTRAINT tb_category_type_check
            CHECK ((type)::TEXT = ANY
        (ARRAY [('NONE'::CHARACTER VARYING)::TEXT, ('STATIC_CONTENT'::CHARACTER VARYING)::TEXT, ('BOARD'::CHARACTER VARYING)::TEXT, ('QNA'::CHARACTER VARYING)::TEXT, ('SCHEDULE'::CHARACTER VARYING)::TEXT, ('STORE'::CHARACTER VARYING)::TEXT, ('BUSINESS'::CHARACTER VARYING)::TEXT]))
    );

COMMENT ON TABLE tb_category IS '카테고리';

COMMENT ON COLUMN tb_category.id IS '식별자';

COMMENT ON COLUMN tb_category.created_by IS '생성자';

COMMENT ON COLUMN tb_category.created_date IS '생성일시';

COMMENT ON COLUMN tb_category.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_category.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_category.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_category.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_category.version IS '버전';

COMMENT ON COLUMN tb_category.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_category.description IS '설명';

COMMENT ON COLUMN tb_category.exposed IS '노출여부';

COMMENT ON COLUMN tb_category.name IS '이름';

COMMENT ON COLUMN tb_category.sequence IS '순서';

COMMENT ON COLUMN tb_category.site_exposed IS '사이트 노출여부';

COMMENT ON COLUMN tb_category.site_exposed_order IS '사이트 노출순서';

COMMENT ON COLUMN tb_category.site_id IS '사이트 식별자';

COMMENT ON COLUMN tb_category.type IS '타입';

ALTER TABLE tb_category
    OWNER TO seesaw_admin;

CREATE TABLE tb_article
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    parent_id          VARCHAR(36)
        CONSTRAINT fk_tb_article_parent
            REFERENCES tb_article,
    category_id        VARCHAR(36)
        CONSTRAINT fk_tb_article_category
            REFERENCES tb_category,
    content            TEXT,
    exposed            BOOLEAN     NOT NULL,
    fixed              BOOLEAN     NOT NULL,
    fixed_order        INTEGER,
    title              VARCHAR(255),
    type               VARCHAR(255)
        CONSTRAINT tb_article_type_check
            CHECK ((type)::TEXT = ANY
        (ARRAY [('MAP'::CHARACTER VARYING)::TEXT, ('HTML'::CHARACTER VARYING)::TEXT, ('CAROUSEL'::CHARACTER VARYING)::TEXT, ('BUTTON_GROUP'::CHARACTER VARYING)::TEXT, ('IMAGE'::CHARACTER VARYING)::TEXT, ('TABLE'::CHARACTER VARYING)::TEXT, ('VIDEO'::CHARACTER VARYING)::TEXT]))
    );

COMMENT ON TABLE tb_article IS '게시글';

COMMENT ON COLUMN tb_article.id IS '식별자';

COMMENT ON COLUMN tb_article.created_by IS '생성자';

COMMENT ON COLUMN tb_article.created_date IS '생성일시';

COMMENT ON COLUMN tb_article.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_article.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_article.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_article.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_article.version IS '버전';

COMMENT ON COLUMN tb_article.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_article.category_id IS '카테고리 식별자';

COMMENT ON COLUMN tb_article.content IS '본문';

COMMENT ON COLUMN tb_article.exposed IS '노출여부';

COMMENT ON COLUMN tb_article.fixed IS '고정여부';

COMMENT ON COLUMN tb_article.fixed_order IS '고정순서';

COMMENT ON COLUMN tb_article.title IS '제목';

COMMENT ON COLUMN tb_article.type IS '타입';

ALTER TABLE tb_article
    OWNER TO seesaw_admin;

CREATE TABLE tb_notification
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    category_id        VARCHAR(36)
        CONSTRAINT fk_tb_notification_category
            REFERENCES tb_category,
    type               VARCHAR(255)
        CONSTRAINT tb_notification_type_check
            CHECK ((type)::TEXT = ANY
        (ARRAY [('TOKTOK'::CHARACTER VARYING)::TEXT, ('SMS'::CHARACTER VARYING)::TEXT, ('EMAIL'::CHARACTER VARYING)::TEXT]))
    );

COMMENT ON TABLE tb_notification IS '알림';

COMMENT ON COLUMN tb_notification.id IS '식별자';

COMMENT ON COLUMN tb_notification.created_by IS '생성자';

COMMENT ON COLUMN tb_notification.created_date IS '생성일시';

COMMENT ON COLUMN tb_notification.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_notification.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_notification.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_notification.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_notification.version IS '버전';

COMMENT ON COLUMN tb_notification.category_id IS '카테고리 식별자';

COMMENT ON COLUMN tb_notification.type IS '유형';

ALTER TABLE tb_notification
    OWNER TO seesaw_admin;

CREATE TABLE tb_reply
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    parent_id          VARCHAR(36)
        CONSTRAINT fk_tb_reply_parent
            REFERENCES tb_reply,
    article_id         VARCHAR(36)
        CONSTRAINT fk_tb_reply_article
            REFERENCES tb_article,
    content            TEXT,
    exposed            BOOLEAN     NOT NULL
);

COMMENT ON TABLE tb_reply IS '댓글';

COMMENT ON COLUMN tb_reply.id IS '식별자';

COMMENT ON COLUMN tb_reply.created_by IS '생성자';

COMMENT ON COLUMN tb_reply.created_date IS '생성일시';

COMMENT ON COLUMN tb_reply.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_reply.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_reply.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_reply.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_reply.version IS '버전';

COMMENT ON COLUMN tb_reply.parent_id IS '부모 식별자';

COMMENT ON COLUMN tb_reply.article_id IS '게시글 식별자';

COMMENT ON COLUMN tb_reply.content IS '본문';

COMMENT ON COLUMN tb_reply.exposed IS '노출여부';

ALTER TABLE tb_reply
    OWNER TO seesaw_admin;

CREATE TABLE tb_user
(
    id                       VARCHAR(36)  NOT NULL
        PRIMARY KEY,
    created_by               VARCHAR(255),
    created_date             TIMESTAMP(6),
    created_ip               VARCHAR(45),
    last_modified_by         VARCHAR(255),
    last_modified_date       TIMESTAMP(6),
    last_modified_ip         VARCHAR(45),
    version                  INTEGER      NOT NULL,
    contact_number           VARCHAR(255),
    credentials_expired_date TIMESTAMP(6),
    email_domain             VARCHAR(255),
    email_id                 VARCHAR(255),
    expired_date             TIMESTAMP(6),
    locked_date              TIMESTAMP(6),
    name                     VARCHAR(255),
    password                 VARCHAR(255),
    username                 VARCHAR(255) NOT NULL
        CONSTRAINT uk4wv83hfajry5tdoamn8wsqa6x
            UNIQUE
);

COMMENT ON TABLE tb_user IS '계정';

COMMENT ON COLUMN tb_user.id IS '식별자';

COMMENT ON COLUMN tb_user.created_by IS '생성자';

COMMENT ON COLUMN tb_user.created_date IS '생성일시';

COMMENT ON COLUMN tb_user.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_user.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_user.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_user.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_user.version IS '버전';

COMMENT ON COLUMN tb_user.contact_number IS '연락처';

COMMENT ON COLUMN tb_user.credentials_expired_date IS '패스워드 만료 일시';

COMMENT ON COLUMN tb_user.email_domain IS '이메일';

COMMENT ON COLUMN tb_user.expired_date IS '만료 일시';

COMMENT ON COLUMN tb_user.locked_date IS '잠금 일시';

COMMENT ON COLUMN tb_user.name IS '이름';

COMMENT ON COLUMN tb_user.password IS '패스워드';

COMMENT ON COLUMN tb_user.username IS '계정명';

ALTER TABLE tb_user
    OWNER TO seesaw_admin;

CREATE TABLE tb_role_mapping
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    role_id            VARCHAR(36)
        CONSTRAINT fk_tb_mapping_role
            REFERENCES tb_role,
    site_id            VARCHAR(36)
        CONSTRAINT fk_tb_mapping_site
            REFERENCES tb_site,
    user_id            VARCHAR(36)
        CONSTRAINT fk_tb_mapping_user
            REFERENCES tb_user
);

COMMENT ON TABLE tb_role_mapping IS '역할 매핑';

COMMENT ON COLUMN tb_role_mapping.id IS '식별자';

COMMENT ON COLUMN tb_role_mapping.created_by IS '생성자';

COMMENT ON COLUMN tb_role_mapping.created_date IS '생성일시';

COMMENT ON COLUMN tb_role_mapping.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_role_mapping.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_role_mapping.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_role_mapping.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_role_mapping.version IS '버전';

ALTER TABLE tb_role_mapping
    OWNER TO seesaw_admin;

CREATE TABLE tb_view
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    article_id         VARCHAR(36)
        CONSTRAINT fk_tb_view_article
            REFERENCES tb_article
);

COMMENT ON TABLE tb_view IS '뷰';

COMMENT ON COLUMN tb_view.id IS '식별자';

COMMENT ON COLUMN tb_view.created_by IS '생성자';

COMMENT ON COLUMN tb_view.created_date IS '생성일시';

COMMENT ON COLUMN tb_view.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_view.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_view.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_view.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_view.version IS '버전';

COMMENT ON COLUMN tb_view.article_id IS '게시글 식별자';

ALTER TABLE tb_view
    OWNER TO seesaw_admin;

CREATE TABLE tb_vote
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    approved           BOOLEAN     NOT NULL,
    reference_id       VARCHAR(36)
);

COMMENT ON TABLE tb_vote IS '투표';

COMMENT ON COLUMN tb_vote.id IS '식별자';

COMMENT ON COLUMN tb_vote.created_by IS '생성자';

COMMENT ON COLUMN tb_vote.created_date IS '생성일시';

COMMENT ON COLUMN tb_vote.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_vote.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_vote.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_vote.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_vote.version IS '버전';

COMMENT ON COLUMN tb_vote.approved IS '찬성여부';

COMMENT ON COLUMN tb_vote.reference_id IS '참조 식별자';

ALTER TABLE tb_vote
    OWNER TO seesaw_admin;

CREATE TABLE tb_permission
(
    id                 VARCHAR(36) NOT NULL
        PRIMARY KEY,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    created_ip         VARCHAR(45),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6),
    last_modified_ip   VARCHAR(45),
    version            INTEGER     NOT NULL,
    mask               INTEGER     NOT NULL,
    role_id            VARCHAR(36),
    target_id          VARCHAR(36)
);

COMMENT ON TABLE tb_permission IS '권한';

COMMENT ON COLUMN tb_permission.id IS '식별자';

COMMENT ON COLUMN tb_permission.created_by IS '생성자';

COMMENT ON COLUMN tb_permission.created_date IS '생성일시';

COMMENT ON COLUMN tb_permission.created_ip IS '생성 IP';

COMMENT ON COLUMN tb_permission.last_modified_by IS '수정자';

COMMENT ON COLUMN tb_permission.last_modified_date IS '수정일시';

COMMENT ON COLUMN tb_permission.last_modified_ip IS '수정 IP';

COMMENT ON COLUMN tb_permission.version IS '버전';

COMMENT ON COLUMN tb_permission.mask IS '비트마스크';

COMMENT ON COLUMN tb_permission.role_id IS '역할 식별자';

COMMENT ON COLUMN tb_permission.target_id IS '대상 식별자';

ALTER TABLE tb_permission
    OWNER TO seesaw_admin;

