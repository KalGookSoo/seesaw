# Database ERD 분석 문서

## 개요

이 문서는 Seesaw 프로젝트의 데이터베이스 스키마를 분석한 내용입니다. PostgreSQL 기반으로 설계되었으며, 총 **14개의 테이블**로 구성되어 있습니다.

---

## 테이블 목록

| 테이블명 | 한글명 | 설명 |
|---------|--------|------|
| `tb_attachment` | 첨부파일 | 게시글 및 기타 엔티티의 첨부파일 관리 |
| `tb_code` | 코드 | 공통 코드 관리 (계층 구조 지원) |
| `tb_menu` | 메뉴 | 시스템 메뉴 관리 (계층 구조 지원) |
| `tb_remember_me_token` | 자동 로그인 토큰 | Remember Me 기능을 위한 토큰 저장 |
| `tb_role` | 역할 | 사용자 역할 정의 |
| `tb_menu_role` | 메뉴 역할 매핑 | 메뉴와 역할의 다대다 관계 |
| `tb_site` | 사이트 | 멀티 사이트 관리 (계층 구조 지원) |
| `tb_category` | 카테고리 | 게시판 카테고리 관리 (계층 구조 지원) |
| `tb_article` | 게시글 | 게시판 글 관리 |
| `tb_notification` | 알림 | 사용자 알림 관리 |
| `tb_reply` | 댓글 | 게시글 댓글 (계층 구조 지원) |
| `tb_user` | 계정 | 사용자 계정 정보 |
| `tb_role_mapping` | 역할 매핑 | 사용자-역할-사이트 매핑 |
| `tb_vote` | 투표 | 게시글/댓글 추천/반대 |
| `tb_permission` | 권한 | ACL 기반 권한 관리 |
| `tb_view` | 뷰 | 게시글 조회수 관리 |

---

## 도메인별 분류

### 1. 사용자 및 인증 도메인

#### `tb_user` (계정)
사용자 계정 정보를 관리하는 핵심 테이블입니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자 (UUID)
- `username` (VARCHAR(255), UNIQUE, NOT NULL): 계정명
- `password` (VARCHAR(255)): 암호화된 패스워드
- `name` (VARCHAR(255)): 사용자 이름
- `email_id`, `email_domain` (VARCHAR(255)): 이메일 (분리 저장)
- `contact_number` (VARCHAR(255)): 연락처
- `expired_date` (TIMESTAMP): 계정 만료 일시
- `locked_date` (TIMESTAMP): 계정 잠금 일시
- `credentials_expired_date` (TIMESTAMP): 패스워드 만료 일시

**특징**:
- Spring Security와 통합된 계정 상태 관리
- 이메일을 ID와 도메인으로 분리 저장
- 감사(Audit) 필드 포함 (created_by, created_date, etc.)

---

#### `tb_role` (역할)
사용자 역할을 정의합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `name` (VARCHAR(255), UNIQUE): 역할명 (예: ROLE_USER, ROLE_ADMIN)
- `alias` (VARCHAR(255)): 역할 별칭

---

#### `tb_role_mapping` (역할 매핑)
사용자-역할-사이트 간의 다대다 관계를 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `user_id` (VARCHAR(36), FK → tb_user): 사용자 식별자
- `role_id` (VARCHAR(36), FK → tb_role): 역할 식별자
- `site_id` (VARCHAR(36), FK → tb_site): 사이트 식별자

**인덱스**:
- `(user_id, site_id)`: 사용자별 사이트 역할 조회 최적화
- `(role_id)`: 역할별 사용자 조회 최적화

**특징**:
- 멀티 사이트 환경에서 사이트별로 다른 역할 부여 가능
- 예: 사용자 A는 사이트 1에서 ADMIN, 사이트 2에서 USER

---

#### `tb_remember_me_token` (자동 로그인 토큰)
Remember Me 기능을 위한 토큰을 저장합니다.

**주요 컬럼**:
- `series` (VARCHAR(255), PK): 토큰 시리즈
- `username` (VARCHAR(64), NOT NULL): 사용자명
- `token` (VARCHAR(64), NOT NULL): 토큰 값
- `last_used` (TIMESTAMP, NOT NULL): 마지막 사용 시각

---

### 2. 권한 및 메뉴 도메인

#### `tb_permission` (권한)
ACL(Access Control List) 기반 권한 관리를 지원합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `role_id` (VARCHAR(36)): 역할 식별자
- `target_id` (VARCHAR(36)): 대상 객체 식별자
- `mask` (INTEGER, NOT NULL): 비트마스크 (READ, WRITE, DELETE 등)

**특징**:
- Spring Security ACL 패턴 구현
- 비트마스크를 사용한 세밀한 권한 제어
- 객체 레벨 권한 관리 가능

---

#### `tb_menu` (메뉴)
시스템 메뉴를 계층 구조로 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `parent_id` (VARCHAR(36), FK → tb_menu): 부모 메뉴 식별자
- `name` (VARCHAR(255)): 메뉴명
- `uri` (VARCHAR(255)): 메뉴 URI
- `sequence` (INTEGER): 정렬 순서

**인덱스**:
- `(parent_id)`: 계층 구조 조회 최적화

**특징**:
- 자기 참조(Self-referencing) 구조로 무한 depth 지원
- 메뉴 순서 관리 (sequence)

---

#### `tb_menu_role` (메뉴 역할 매핑)
메뉴와 역할의 다대다 관계를 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `menu_id` (VARCHAR(36), FK → tb_menu): 메뉴 식별자
- `role_id` (VARCHAR(36), FK → tb_role): 역할 식별자

**인덱스**:
- `(menu_id)`: 메뉴별 역할 조회
- `(role_id)`: 역할별 메뉴 조회

**특징**:
- 역할 기반 메뉴 접근 제어

---

### 3. 사이트 및 카테고리 도메인

#### `tb_site` (사이트)
멀티 사이트 환경을 지원하는 사이트 정보를 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `parent_id` (VARCHAR(36), FK → tb_site): 부모 사이트 식별자
- `domain_name` (VARCHAR(255), UNIQUE): 도메인명
- `name` (VARCHAR(255)): 사이트명
- `description` (VARCHAR(255)): 설명
- `intro` (VARCHAR(255)): 소개글
- `content` (TEXT): 본문
- `address` (VARCHAR(255)): 주소
- `zipcode` (VARCHAR(255)): 우편번호
- `contact_number` (VARCHAR(255)): 연락처
- `distribution_code` (VARCHAR(255)): 분류코드
- `tags` (VARCHAR(255)): 태그
- `image_exposed` (BOOLEAN, NOT NULL): 이미지 노출여부
- `search_engine_exposed` (BOOLEAN, NOT NULL): 검색엔진 노출여부

**인덱스**:
- `(parent_id)`: 계층 구조 조회

**특징**:
- 계층 구조로 서브 사이트 관리 가능
- SEO 설정 지원 (search_engine_exposed)
- 도메인별 독립적인 사이트 운영

---

#### `tb_category` (카테고리)
게시판 카테고리를 계층 구조로 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `parent_id` (VARCHAR(36), FK → tb_category): 부모 카테고리 식별자
- `site_id` (VARCHAR(36), FK → tb_site): 사이트 식별자
- `name` (VARCHAR(255)): 카테고리명
- `description` (VARCHAR(255)): 설명
- `type` (VARCHAR(255)): 카테고리 타입
  - `NONE`: 일반
  - `STATIC_CONTENT`: 정적 콘텐츠
  - `BOARD`: 게시판
  - `QNA`: Q&A
  - `SCHEDULE`: 일정
  - `STORE`: 스토어
  - `BUSINESS`: 비즈니스
- `sequence` (INTEGER): 정렬 순서
- `exposed` (BOOLEAN, NOT NULL): 노출여부
- `site_exposed` (BOOLEAN, NOT NULL): 사이트 노출여부
- `site_exposed_order` (INTEGER, NOT NULL): 사이트 노출 순서

**인덱스**:
- `(parent_id)`: 계층 구조 조회
- `(site_id)`: 사이트별 카테고리 조회

**특징**:
- 다양한 카테고리 타입 지원
- 사이트별 독립적인 카테고리 관리
- 노출 순서 관리

---

### 4. 콘텐츠 도메인

#### `tb_article` (게시글)
게시판 글을 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `category_id` (VARCHAR(36), FK → tb_category): 카테고리 식별자
- `title` (VARCHAR(255)): 제목
- `content` (TEXT): 본문
- `exposed` (BOOLEAN, NOT NULL): 노출여부
- `fixed_exposed` (BOOLEAN, NOT NULL): 고정 노출여부
- `fixed_exposed_order` (INTEGER, NOT NULL): 고정 노출 순서
- `tags` (VARCHAR(255)): 태그

**인덱스**:
- `(category_id)`: 카테고리별 게시글 조회

**특징**:
- 공지사항 고정 기능 (fixed_exposed)
- 태그 기반 분류
- 감사 필드로 작성자/수정자 추적

---

#### `tb_reply` (댓글)
게시글 댓글을 계층 구조로 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `parent_id` (VARCHAR(36), FK → tb_reply): 부모 댓글 식별자
- `article_id` (VARCHAR(36), FK → tb_article): 게시글 식별자
- `content` (TEXT): 본문
- `exposed` (BOOLEAN, NOT NULL): 노출여부

**인덱스**:
- `(article_id)`: 게시글별 댓글 조회
- `(parent_id)`: 대댓글 조회

**특징**:
- 자기 참조 구조로 대댓글 지원
- 삭제된 댓글도 노출 제어 가능 (exposed)

---

#### `tb_attachment` (첨부파일)
게시글 및 기타 엔티티의 첨부파일을 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `reference_id` (VARCHAR(36)): 참조 객체 식별자
- `file_name` (VARCHAR(255)): 파일명
- `file_path` (VARCHAR(255)): 파일 경로
- `file_size` (BIGINT): 파일 크기
- `content_type` (VARCHAR(255)): MIME 타입
- `download_count` (INTEGER): 다운로드 횟수

**인덱스**:
- `(reference_id)`: 참조 객체별 첨부파일 조회

**특징**:
- 범용적인 첨부파일 관리 (reference_id로 다양한 엔티티 연결)
- 다운로드 통계 관리

---

#### `tb_notification` (알림)
사용자 알림을 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `reference_id` (VARCHAR(36)): 참조 객체 식별자
- `message` (TEXT): 알림 메시지
- `read_date` (TIMESTAMP): 읽은 일시
- `type` (VARCHAR(255)): 알림 타입

**특징**:
- 읽음/안읽음 상태 관리 (read_date)
- 다양한 알림 타입 지원

---

### 5. 상호작용 도메인

#### `tb_vote` (투표)
게시글 및 댓글의 추천/반대를 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `reference_id` (VARCHAR(36)): 참조 객체 식별자 (게시글 또는 댓글)
- `approved` (BOOLEAN, NOT NULL): 찬성여부 (true: 추천, false: 반대)

**특징**:
- 감사 필드의 `created_by`로 투표자 식별
- 중복 투표 방지는 애플리케이션 레벨에서 처리

---

#### `tb_view` (뷰)
게시글 조회수를 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `article_id` (VARCHAR(36), FK → tb_article): 게시글 식별자

**인덱스**:
- `(article_id)`: 게시글별 조회수 집계

**특징**:
- 조회 이벤트마다 레코드 생성 (COUNT로 조회수 계산)
- 감사 필드로 조회자 및 시각 추적

---

### 6. 공통 코드 도메인

#### `tb_code` (코드)
시스템 공통 코드를 계층 구조로 관리합니다.

**주요 컬럼**:
- `id` (VARCHAR(36), PK): 식별자
- `parent_id` (VARCHAR(36), FK → tb_code): 부모 코드 식별자
- `code` (VARCHAR(255)): 코드값
- `name` (VARCHAR(255)): 코드명
- `description` (VARCHAR(255)): 설명
- `sequence` (INTEGER): 정렬 순서

**인덱스**:
- `(parent_id)`: 계층 구조 조회

**특징**:
- 계층 구조로 코드 그룹 관리
- 정렬 순서 지원

---

## 공통 패턴 분석

### 1. 감사(Audit) 필드
모든 테이블(tb_remember_me_token 제외)에 공통적으로 포함된 필드:

```sql
created_by VARCHAR(255)           -- 생성자
created_date TIMESTAMP(6)         -- 생성일시
created_ip VARCHAR(45)            -- 생성 IP
last_modified_by VARCHAR(255)    -- 수정자
last_modified_date TIMESTAMP(6)  -- 수정일시
last_modified_ip VARCHAR(45)     -- 수정 IP
version INTEGER NOT NULL         -- 낙관적 잠금(Optimistic Locking)
```

**특징**:
- JPA Auditing 패턴 적용
- IP 주소 추적으로 보안 감사 지원
- 버전 필드로 동시성 제어

---

### 2. 계층 구조 패턴
여러 테이블에서 자기 참조 구조 사용:

- `tb_code` (parent_id → tb_code)
- `tb_menu` (parent_id → tb_menu)
- `tb_site` (parent_id → tb_site)
- `tb_category` (parent_id → tb_category)
- `tb_reply` (parent_id → tb_reply)

**특징**:
- 무한 depth 계층 구조 지원
- 재귀 쿼리로 전체 트리 조회 가능

---

### 3. 식별자 전략
모든 테이블에서 `VARCHAR(36)` 타입의 UUID 사용:

```sql
id VARCHAR(36) NOT NULL PRIMARY KEY
```

**장점**:
- 분산 환경에서 충돌 없는 ID 생성
- 보안성 향상 (순차 ID 노출 방지)

**단점**:
- 인덱스 크기 증가
- 정렬 성능 저하 가능성

---

### 4. 소프트 삭제 패턴
`exposed` 필드를 사용한 논리적 삭제:

- `tb_article.exposed`
- `tb_reply.exposed`
- `tb_category.exposed`

**특징**:
- 물리적 삭제 대신 노출 여부로 제어
- 데이터 복구 가능
- 감사 추적 유지

---

## 관계도 요약

### 핵심 관계

```
tb_user ──┬─ tb_role_mapping ── tb_role
          │                   └─ tb_site
          │
          └─ tb_notification

tb_site ──┬─ tb_category ── tb_article ──┬─ tb_reply
          │                              ├─ tb_attachment
          │                              ├─ tb_vote
          │                              └─ tb_view
          │
          └─ tb_role_mapping

tb_menu ── tb_menu_role ── tb_role

tb_role ──┬─ tb_permission
          ├─ tb_menu_role
          └─ tb_role_mapping
```

---

## 데이터베이스 설계 특징

### 장점

1. **멀티 테넌시 지원**: 사이트별 독립적인 데이터 관리
2. **유연한 권한 관리**: 역할 기반 + ACL 기반 하이브리드
3. **확장성**: 계층 구조로 무한 확장 가능
4. **감사 추적**: 모든 변경 이력 추적
5. **타입 안전성**: ENUM 타입 사용 (category.type)

### 개선 가능 영역

1. **성능 최적화**:
  - 조회수 테이블(tb_view)은 레코드 수가 급증할 수 있음
  - 집계 테이블 또는 캐싱 고려 필요

2. **정규화**:
  - `tb_site.tags`, `tb_article.tags`는 정규화 고려
  - 별도 태그 테이블 + 매핑 테이블 구조 검토

3. **인덱스 최적화**:
  - 복합 인덱스 추가 검토 (예: category_id + created_date)
  - Full-text search 인덱스 고려 (article.content, reply.content)

4. **파티셔닝**:
  - 대용량 테이블(tb_view, tb_notification)은 파티셔닝 고려

---

## 기술 스택 추론

DDL 분석을 통해 추론되는 기술 스택:

- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate (감사 필드, 버전 필드 패턴)
- **Security**: Spring Security (역할, 권한, Remember Me)
- **Architecture**: Multi-tenancy, Domain-Driven Design

---

## 결론

Seesaw 프로젝트의 데이터베이스는 **멀티 사이트 CMS/커뮤니티 플랫폼**을 위한 설계로, 다음과 같은 특징을 가집니다:

- ✅ 엔터프라이즈급 감사 추적
- ✅ 유연한 권한 관리 시스템
- ✅ 확장 가능한 계층 구조
- ✅ 멀티 테넌시 지원
- ✅ 소프트 삭제 패턴

향후 성능 최적화와 정규화 개선을 통해 더욱 견고한 시스템으로 발전할 수 있을 것으로 보입니다.

---

**문서 작성일**: 2025-11-21  
**DDL 파일**: `.agent/ddl.sql` (848 lines)  
**총 테이블 수**: 14개
