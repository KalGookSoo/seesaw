# Database ERD 분석 문서

## 개요

이 문서는 저장소의 PostgreSQL DDL과 이후에 반영된 스키마 변경 사항을 기준으로 정리한 데이터베이스 구조 문서입니다.

- 기준 DDL: `seesaw-web/docs/database/tables-1.8.0.sql`
- 추가 변경: `tb_event.location_detail` 컬럼 추가 (개발·운영 DB 반영은 사용자 확인)
- 기준일: 2026-11-25
- 테이블 수: **26개**
- 공통 식별자: 대부분 `VARCHAR(36)` UUID
- ORM: JPA/Hibernate

기준 DDL은 1.8.0 시점의 스냅샷이며 이후 변경 사항을 모두 포함하지 않습니다. 실제 데이터베이스의 스키마와 변경 이력은 환경별로 확인해야 합니다.

## 테이블 목록

| 테이블명 | 한글명 | 설명 |
|---|---|---|
| `tb_attachment` | 첨부파일 | 게시글 등 여러 도메인에 연결되는 파일 |
| `tb_code` | 코드 | 계층형 공통 코드 |
| `tb_menu` | 메뉴 | 계층형 사이트 메뉴 |
| `tb_remember_me_token` | 자동 로그인 토큰 | Remember Me 토큰 |
| `tb_role` | 역할 | 사용자 역할 |
| `tb_menu_role` | 메뉴 역할 매핑 | 메뉴와 역할의 다대다 매핑 |
| `tb_site` | 사이트 | 멀티 사이트 정보 |
| `tb_category` | 카테고리 | 사이트별 게시판·콘텐츠 분류 |
| `tb_article` | 게시글 | 게시글과 콘텐츠 블록 |
| `tb_notification` | 알림 | 사용자 인앱 알림 |
| `tb_notification_preference` | 알림 수신 설정 | 채널별 알림 수신 설정 |
| `tb_notification_subscription` | 게시판 알림 구독 | 카테고리별 새 글 구독 |
| `tb_notification_delivery` | 알림 전달 기록 | 인앱·웹 푸시·이메일 전달 상태 |
| `tb_reply` | 댓글 | 게시글 댓글과 대댓글 |
| `tb_user` | 계정 | 사용자 계정 |
| `tb_role_mapping` | 역할 매핑 | 사용자·역할·사이트 매핑 |
| `tb_vote` | 투표 | 게시글·댓글에 대한 찬성/반대 상태 |
| `tb_permission` | 권한 | ACL 권한 비트마스크 |
| `tb_view` | 조회 기록 | 게시글 조회 이벤트 |
| `tb_event` | 캘린더 이벤트 | 일정 게시글의 캘린더 정보 |
| `tb_event_attendees` | 이벤트 참석자 | RFC 일정 참석자 정보 |
| `tb_journal` | 저널 | RFC 저널 정보 |
| `tb_journal_descriptions` | 저널 설명 | 저널 설명 컬렉션 |
| `tb_todo` | 할 일 | RFC 할 일 정보 |
| `tb_event_attendee` | 이벤트 참석자 | 별도 참석자 컬렉션 매핑 |
| `tb_journal_description` | 저널 설명 | 별도 설명 컬렉션 매핑 |

> `tb_event_attendees`와 `tb_event_attendee`, `tb_journal_descriptions`와 `tb_journal_description`은 현재 DDL에 모두 존재합니다. 중복 여부를 판단하지 않고 현행 스키마 그대로 기록합니다.

## 도메인별 구조

### 1. 사용자·인증·권한

#### `tb_user` — 계정

- PK: `id`
- 계정 식별: `username` UNIQUE NOT NULL
- 인증 정보: `password`, `email_id`, `email_domain`
- 상태 정보: `expired_date`, `locked_date`, `credentials_expired_date`
- 사용자 정보: `name`, `contact_number`

#### `tb_role` — 역할

- PK: `id`
- `name` UNIQUE
- `alias`

#### `tb_role_mapping` — 역할 매핑

- PK: `id`
- FK: `user_id → tb_user`, `role_id → tb_role`, `site_id → tb_site`
- 인덱스: `(role_id)`, `(user_id, site_id)`

#### `tb_permission` — 권한

- PK: `id`
- `role_id`, `target_id`, `mask`
- `mask`는 ACL 권한 비트마스크입니다.
- 현재 DDL에는 `role_id`, `target_id`의 외래키가 정의되어 있지 않습니다.

#### `tb_remember_me_token` — 자동 로그인 토큰

- PK: `series`
- `username`, `token`, `last_used`
- 감사 필드는 없습니다.

### 2. 사이트·메뉴·카테고리

#### `tb_site` — 사이트

- PK: `id`
- 자기참조 FK: `parent_id → tb_site`
- 사이트 정보: `domain_name` UNIQUE, `name`, `description`, `intro`, `content`
- 운영 정보: `address`, `zipcode`, `contact_number`, `distribution_code`, `tags`
- 노출 설정: `image_exposed`, `search_engine_exposed`
- 인덱스: `(parent_id)`

#### `tb_category` — 카테고리

- PK: `id`
- FK: `parent_id → tb_category`, `site_id → tb_site`
- `name`, `description`, `sequence`
- 노출 설정: `exposed`, `site_exposed`, `site_exposed_order`
- `type`: `NONE`, `STATIC_CONTENT`, `BOARD`, `QNA`, `SCHEDULE`, `STORE`, `BUSINESS`
- 인덱스: `(parent_id)`, `(site_id)`

#### `tb_menu` — 메뉴

- PK: `id`
- 자기참조 FK: `parent_id → tb_menu`
- `name`, `uri`, `sequence`
- 인덱스: `(parent_id)`

#### `tb_menu_role` — 메뉴 역할 매핑

- PK: `id`
- FK: `menu_id → tb_menu`, `role_id → tb_role`
- 인덱스: `(menu_id)`, `(role_id)`

#### `tb_code` — 공통 코드

- PK: `id`
- 자기참조 FK: `parent_id → tb_code`
- `name`, `description`, `sequence`
- 인덱스: `(parent_id)`

### 3. 콘텐츠·상호작용

#### `tb_article` — 게시글

- PK: `id`
- 자기참조 FK: `parent_id → tb_article`
- FK: `category_id → tb_category`
- `title`, `content`, `type`
- `type`: `MAP`, `HTML`, `CAROUSEL`, `BUTTON_GROUP`, `IMAGE`, `TABLE`, `VIDEO`
- 노출·고정: `exposed`, `fixed`, `fixed_order`
- 인덱스: `(parent_id)`, `(category_id)`

#### `tb_reply` — 댓글

- PK: `id`
- FK: `parent_id → tb_reply`, `article_id → tb_article`
- `content`, `exposed`
- 인덱스: `(parent_id)`, `(article_id)`

#### `tb_attachment` — 첨부파일

- PK: `id`
- 범용 연결 키: `reference_id`
- 파일 정보: `name`, `original_name`, `path_name`, `mime_type`, `size`
- `reference_id`는 게시글 등 연결 대상의 식별자를 저장하며, 현재 DDL에는 대상 테이블 외래키가 없습니다.

#### `tb_view` — 조회 기록

- PK: `id`
- FK: `article_id → tb_article`
- 인덱스: `(article_id)`
- 게시글 조회 이벤트를 누적해 조회수를 계산합니다.

#### `tb_vote` — 투표

- PK: `id`
- 범용 연결 키: `reference_id`
- `approved`: `true`는 찬성, `false`는 반대
- 투표자: 공통 감사 필드의 `created_by`
- 인덱스: `(reference_id, approved)`
- UNIQUE: `(created_by, reference_id)`
- `reference_id`는 게시글 또는 댓글을 가리키며, 현재 DDL에는 대상 테이블 외래키가 없습니다.

### 4. Notification

#### `tb_notification` — 인앱 알림

- PK: `id`
- FK: `site_id → tb_site`, `recipient_id → tb_user`
- 발생자: `actor_username`
- 알림 정보: `type`, `title`, `message`
- 이동 대상: `target_type`, `target_id`, `target_url`
- 이벤트 멱등성: `source_event_id`
- 읽음·만료: `read_at`, `expires_at`
- 인덱스: `(site_id, recipient_id, read_at, created_date)`, `(expires_at)`
- UNIQUE: `(recipient_id, source_event_id, type)`
- `in_app`은 이메일이나 웹 푸시가 아닌 서비스 화면 내부 알림함·알림 벨을 의미합니다.
- 알림 타입: `ARTICLE_CREATED`, `REPLY_CREATED`, `REPORT_CREATED`, `INQUIRY_CREATED`, `ARTICLE_UPDATED`, `ARTICLE_DELETED`, `SCHEDULE_UPDATED`, `SCHEDULE_REMINDER`

#### `tb_notification_preference` — 알림 수신 설정

- PK: `id`
- FK: `site_id → tb_site`, `user_id → tb_user`
- `type`
- 채널 설정: `in_app_enabled`(서비스 내부 알림), `web_push_enabled`(웹 푸시), `email_enabled`(이메일)
- UNIQUE: `(site_id, user_id, type)`
- 인덱스: `(site_id, user_id)`

#### `tb_notification_subscription` — 게시판 알림 구독

- PK: `id`
- FK: `site_id → tb_site`, `user_id → tb_user`, `category_id → tb_category`
- `enabled`
- UNIQUE: `(site_id, user_id, category_id)`
- 인덱스: `(site_id, category_id, enabled)`

#### `tb_notification_delivery` — 알림 전달 기록

- PK: `id`
- FK: `notification_id → tb_notification`
- 채널: `IN_APP`, `WEB_PUSH`, `EMAIL`
- 상태: `PENDING`, `SENT`, `FAILED`, `SKIPPED`
- 재시도·결과: `attempt_count`, `delivered_at`, `next_attempt_at`, `failure_reason`
- UNIQUE: `(notification_id, channel)`
- 인덱스: `(status, next_attempt_at)`

### 5. 캘린더·RFC 컴포넌트

#### `tb_event` — 캘린더 이벤트

- PK 및 FK: `id → tb_article`
- 일정 정보: `dt_start`, `dt_end`, `dt_stamp`, `duration`, `tzid`
- 표시 정보: `summary`, `description`, `location`, `location_detail`
- `location`: 기존 장소 문자열. 기존 데이터에는 기본 주소와 상세 장소가 함께 저장되어 있을 수 있습니다.
- `location_detail`: 상세 장소(예: 층·호수), `VARCHAR(255)`, NULL 허용. 기존 데이터는 자동 분리하지 않습니다.
- 지도 검색에는 기본 장소인 `location`을 사용하고, 화면 및 캘린더에는 필요에 따라 상세 장소를 함께 표시합니다. 관련 변경은 [#155](https://github.com/KalGookSoo/seesaw/issues/155)에서 진행 중이며, 기본 주소 검색과 읽기 전용 입력은 네이버 Maps 검색 결과 저장 허용 여부 확인 후 연결합니다.
- `sequence`
- `status`: `TENTATIVE`, `CONFIRMED`, `CANCELLED`

#### `tb_event_attendees` — 이벤트 참석자

- FK: `event_id → tb_event`
- `attendee_cn`, `attendee_email`, `attendee_role`, `attendee_partstat`

#### `tb_journal` — 저널

- PK: `id`
- `dt_start`, `dt_stamp`, `sequence`, `summary`
- `status`: `DRAFT`, `FINAL`, `CANCELLED`

#### `tb_journal_descriptions` — 저널 설명 컬렉션

- FK: `journal_id → tb_journal`
- `description`

#### `tb_todo` — 할 일

- PK: `id`
- `dt_start`, `due`, `completed`, `dt_stamp`, `sequence`
- 진행 정보: `percent_complete`, `priority`, `summary`
- `status`: `NEEDS_ACTION`, `COMPLETED`, `IN_PROCESS`, `CANCELLED`

#### `tb_event_attendee` — 이벤트 참석자 컬렉션

- FK: `event_id → tb_event`
- `attendee_cn`, `attendee_email`, `attendee_role`, `attendee_partstat`

#### `tb_journal_description` — 저널 설명 컬렉션

- FK: `journal_id → tb_journal`
- `description`

## 관계도 요약

```text
tb_site
├── tb_category ── tb_article ──┬── tb_reply
│                               ├── tb_view
│                               └── tb_event
├── tb_role_mapping ── tb_user
├── tb_notification ── tb_user (recipient)
├── tb_notification_preference ── tb_user
└── tb_notification_subscription ── tb_user / tb_category

tb_article ── tb_event ── tb_event_attendees / tb_event_attendee
tb_journal ── tb_journal_descriptions / tb_journal_description

tb_menu ── tb_menu_role ── tb_role ── tb_permission
tb_code ── tb_code
tb_vote ── reference_id ── tb_article 또는 tb_reply
tb_attachment ── reference_id ── 게시글 등 연결 대상
tb_notification ── tb_notification_delivery
```

## 공통 설계 패턴

### 감사 필드

대부분의 도메인 테이블은 다음 감사 필드를 공유합니다.

```sql
created_by         varchar(255)
created_date       timestamp(6)
created_ip         varchar(45)
last_modified_by   varchar(255)
last_modified_date timestamp(6)
last_modified_ip   varchar(45)
version            integer not null
```

예외는 `tb_remember_me_token`과 이벤트·저널의 컬렉션 매핑 테이블입니다.

### 계층 구조

- `tb_code.parent_id → tb_code`
- `tb_menu.parent_id → tb_menu`
- `tb_site.parent_id → tb_site`
- `tb_category.parent_id → tb_category`
- `tb_article.parent_id → tb_article`
- `tb_reply.parent_id → tb_reply`

### 범용 참조

`tb_attachment.reference_id`와 `tb_vote.reference_id`는 여러 도메인을 연결하기 위한 범용 식별자입니다. 데이터베이스 외래키 대신 애플리케이션에서 참조 대상의 유효성을 관리합니다.

### 알림 이벤트와 전달

알림은 `tb_notification`에 수신자별 인앱 메시지를 저장하고, `tb_notification_delivery`에서 채널별 전달 상태를 관리합니다.

- 게시글 생성: 구독한 카테고리의 사용자에게 알림
- 댓글 생성: 게시글 작성자에게 알림
- 신고·문의 생성: 관리자·매니저에게 알림
- 웹 푸시·이메일: 수신 설정과 전달 기록에 따라 처리
- `source_event_id`와 UNIQUE 제약으로 중복 이벤트를 방지

## 설계상 확인이 필요한 항목

1. `tb_event_attendees`와 `tb_event_attendee`는 동일한 참석자 구조를 가진 테이블로 보입니다. 하나로 통합할지 결정이 필요합니다.
2. `tb_journal_descriptions`와 `tb_journal_description`도 동일한 설명 구조를 가진 테이블로 보입니다. 사용 중인 테이블을 확인한 뒤 통합 여부를 결정해야 합니다.
3. `tb_attachment.reference_id`, `tb_vote.reference_id`, `tb_permission.target_id`는 외래키가 없어 애플리케이션 레벨 검증이 필요합니다.
4. `tb_vote`의 `created_by`는 공통 감사 필드이므로 사용자 테이블 외래키가 없습니다. 탈퇴 계정·익명 처리 정책을 함께 관리해야 합니다.

## 기술 스택

- 데이터베이스: PostgreSQL
- ORM: JPA/Hibernate
- 인증·인가: Spring Security
- 아키텍처: 멀티 사이트 + 도메인 단위 모듈 구조

---

**문서 기준**: `seesaw-web/docs/database/tables-1.8.0.sql` 및 `tb_event.location_detail` 추가 변경
**현행화일**: 2026-11-25
