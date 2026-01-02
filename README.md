# SEESAW (시소)

SEESAW는 Java Spring Boot와 Next.js를 기반으로 구축된 현대적인 콘텐츠 관리 시스템(CMS) 및 웹 애플리케이션 프레임워크입니다. 이 프로젝트는 멀티 모듈 및 Git Submodules 구조를 채택하여 코드의 재사용성과 관리 효율성을 극대화했습니다.

## 🚀 프로젝트 개요

SEESAW 프로젝트는 핵심 도메인 로직을 공유하면서, 다양한 엔드포인트(API, Web, Console)를 독립적으로 운영할 수 있도록 설계되었습니다.

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.4.3
- **Language**: Java 17
- **Build Tool**: Gradle
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security, JWT, OAuth2
- **Database**: H2 (Development), MariaDB/MySQL (Production)

### Frontend (Admin Console)
- **Framework**: Next.js 15.1.0
- **Library**: React 19, Tailwind CSS, Radix UI
- **Language**: TypeScript

## 📂 프로젝트 구조 및 서브모듈

본 프로젝트는 Git Submodules를 통해 각 모듈이 독립적인 리포지토리로 관리됩니다.

### 1. [seesaw-core](./seesaw-core)
프로젝트의 심장부로, 모든 모듈에서 공유하는 핵심 비즈니스 로직과 도메인 모델을 포함합니다.
- **도메인 엔티티**: User, Article, Category, Site, Attachment 등
- **데이터 레이어**: JPA 리포지토리 및 Querydsl 기반 검색
- **공통 서비스**: 비즈니스 로직 인터페이스 및 기본 구현체
- **유틸리티**: Excel 처리, 파일 입출력, HTML 새니타이징(Jsoup), 밸리데이션 등

### 2. [seesaw-api](./seesaw-api)
모바일 앱이나 외부 서비스와의 연동을 위한 RESTful API 서버입니다.
- **컨트롤러**: REST API 엔드포인트 제공
- **보안**: JWT 기반 인증 및 인가 설정
- **문서화**: OpenAPI/Swagger 지원

### 3. [seesaw-web](./seesaw-web)
사용자에게 직접 보여지는 웹 애플리케이션 모듈입니다.
- **MVC**: 컨트롤러 및 뷰 템플릿 처리
- **인증**: 소셜 로그인(OAuth2) 지원
- **인터셉터**: 다국어(I18n) 및 컨텍스트 관리

### 4. [seesaw-console](./seesaw-console)
관리자를 위한 강력한 웹 대시보드입니다. Next.js를 기반으로 구축되었습니다.
- **CMS 관리**: 게시물, 카테고리, 사이트 설정 관리
- **UI/UX**: 현대적인 디자인 시스템 및 반응형 대시보드
- **실시간 데이터**: 최신 React 기능을 활용한 빠른 인터랙션

## ⚙️ CI/CD 및 품질 관리

GitHub Actions를 통해 자동화된 테스트 및 배포 파이프라인이 구축되어 있습니다.

- **CI (Continuous Integration)**:
  - `api-ci.yml`: API 모듈 테스트 및 빌드
  - `web-ci.yml`: Web 모듈 테스트 및 빌드
  - `console-ci.yml`: Console 모듈 빌드 및 검증
- **CD (Continuous Deployment)**:
  - `deploy-seesaw-api.yml`: API 서버 자동 배포
  - `deploy-seesaw-web.yml`: Web 서버 자동 배포
- **Code Quality**:
  - `qodana_code_quality.yml`: Qodana를 이용한 정적 코드 분석

## 🏁 시작하기

### 저장소 클론 (서브모듈 포함)
```bash
git clone --recursive https://github.com/KalGookSoo/seesaw.git
```

### 서브모듈 업데이트
```bash
git submodule update --init --recursive
```

### 백엔드 빌드 및 실행
```bash
./gradlew :seesaw-api:bootRun  # API 서버 실행
./gradlew :seesaw-web:bootRun  # Web 서버 실행
```

### 어드민 콘솔 실행
```bash
cd seesaw-console
npm install
npm run dev
```
