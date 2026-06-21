# ⚖️ SEESAW Project

> **Modern CMS Framework powered by Java Spring Boot & Next.js**

SEESAW는 현대적인 콘텐츠 관리 시스템(CMS) 및 웹 애플리케이션 프레임워크입니다. 멀티 모듈 아키텍처와 Git Submodules 구조를 채택하여 대규모 서비스의 확장성과 관리 효율성을 극대화하도록 설계되었습니다.

---

## 🚀 Project Overview

SEESAW는 "균형과 유연함"을 핵심 가치로 삼아, 핵심 도메인 로직을 공유하면서도 다양한 엔드포인트(API, Web, Console)를 독립적으로 운영할 수 있는 환경을 제공합니다.

### Key Pillars
- **Decoupled Architecture**: 비즈니스 로직(Core), 데이터 제공(API), 사용자 인터페이스(Web), 관리 도구(Console)의 명확한 분리
- **Security First**: JWT, OAuth2, CSP sandbox 등 현대적인 보안 표준 준수
- **Enterprise-Ready**: Spring Cloud Config를 통한 중앙 집중식 설정 관리 및 자동화된 CI/CD 파이프라인

---

## 🛠 Tech Stack

### Backend Ecosystem
- **Core**: Spring Boot 3.4.x, Java 17, Gradle
- **Persistence**: Spring Data JPA (PostgreSQL / H2)
- **Security**: Spring Security, JWT, OAuth2, ACL
- **External Support**: Spring Cloud Config, Jsoup, Apache POI

### Frontend Ecosystem
- **Admin Console**: Next.js 15, React 19, TypeScript, Tailwind CSS, Radix UI
- **Web Portal**: Spring MVC, Thymeleaf, Bootstrap 5, Toast UI (Editor/Calendar)

---

## 📂 Project Structure & Modules

각 모듈은 독립적인 리포지토리로 관리되며 Git Submodule로 연결되어 있습니다.

### 1. [📦 seesaw-core](https://github.com/KalGookSoo/seesaw-core)
프로젝트의 기반이 되는 핵심 모듈입니다.
- 도메인 엔티티(JPA), 리포지토리, 공통 유틸리티(파일 처리, 엑셀, 보안 정제) 및 비즈니스 모델(Model/Command) 정의

### 2. [⚖️ seesaw-api](https://github.com/KalGookSoo/seesaw-api)
Headless 환경을 위한 RESTful API 서버입니다.
- JWT 기반 인증 및 인가, OpenAPI/Swagger 문서화, 모바일/콘솔용 데이터 제공

### 3. [🌐 seesaw-web](https://github.com/KalGookSoo/seesaw-web)
Thymeleaf 기반의 사용자 포털 웹 애플리케이션입니다.
- 소셜 로그인(OAuth2), 게시글/정적 콘텐츠 관리, 동적 첨부파일 미리보기 지원

### 4. [🖥️ seesaw-console](https://github.com/KalGookSoo/seesaw-console)
Next.js 기반의 현대적인 관리자 대시보드입니다.
- 콘텐츠 전반에 대한 시각적 관리, 반응형 디자인 시스템 적용

### 5. [🔐 seesaw-config](https://github.com/KalGookSoo/seesaw-config) (Private)
Spring Cloud Config 기반의 중앙 집중식 설정 관리 모듈입니다.
- 환경별(Dev, Prod) 설정 보안 관리 및 무중단 설정 업데이트 지원

---

## 📱 Client Applications

SEESAW API를 소비하는 다양한 클라이언트 생태계를 구축하고 있습니다.

### [💬 seesaw-chat](https://github.com/KalGookSoo/seesaw-chat)
React Native(Expo) 기반의 모바일 애플리케이션입니다.
- SEESAW API를 활용한 실시간 콘텐츠 조회 및 인터랙션 제공
- 크로스 플랫폼(iOS, Android) 지원

---

## ⚙️ CI/CD & Quality Control

GitHub Actions를 통해 안정적인 개발 주기를 보장합니다.

- **CI**: `api-ci.yml`, `web-ci.yml`, `console-ci.yml` (빌드 및 테스트 자동화)
- **CD**: `deploy-seesaw-api.yml`, `deploy-seesaw-web.yml` (자동 배포 파이프라인)
- **Quality**: `qodana_code_quality.yml` (정적 코드 분석을 통한 코드 품질 유지)

---

## 🏁 Quick Start

### 1. Repository Setup
```bash
# 서브모듈을 포함하여 클론
git clone --recursive https://github.com/KalGookSoo/seesaw.git

# 이미 클론한 경우 서브모듈 초기화
git submodule update --init --recursive
```

### 2. Backend Execution
```bash
# API 서버 실행 (포트 8080)
./gradlew :seesaw-api:bootRun

# Web 서버 실행 (포트 8081)
./gradlew :seesaw-web:bootRun
```

### 3. Admin Console Execution
```bash
cd seesaw-console
npm install
npm run dev
```

---

## 📄 License
This project is licensed under the terms of the MIT license.
