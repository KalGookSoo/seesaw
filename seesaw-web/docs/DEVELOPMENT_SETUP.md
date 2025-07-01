# 개발 환경 설정

이 문서는 SEESAW WEB 프로젝트의 로컬 개발 환경, 데이터베이스 설정 및 빌드 방법을 설명합니다.

## 로컬 개발 환경 설정

### 로컬환경 프로그램 인자 (IntelliJ 기준)

로컬 개발 환경에서 프로젝트를 실행할 때 다음 프로그램 인자를 설정해야 합니다:

```
--spring.thymeleaf.prefix=file:///$PROJECT_DIR$/seesaw-web/src/main/resources/templates/
--spring.web.resources.static-locations=file:///$PROJECT_DIR$/seesaw-web/src/main/resources/static/
--spring.messages.basename=file:///$PROJECT_DIR$/seesaw-web/src/main/resources/messages/
```

### IntelliJ에서 설정하는 방법

1. Run/Debug Configurations 메뉴를 엽니다.
2. 실행할 구성을 선택합니다.
3. Program arguments 필드에 위의 인자들을 추가합니다.
4. Apply 버튼을 클릭하여 설정을 저장합니다.

### 환경변수 설정

프로젝트에서 사용되는 주요 환경변수에 대한 설명입니다.

#### 설정 파일

- [application.properties](../src/main/resources/application-local.properties)
- [datasource.properties](../src/main/resources/datasource.properties)
- [datasource-local.properties](../src/main/resources/datasource-local.properties)
- [datasource-prod.properties](../src/main/resources/datasource-prod.properties)
- [datasource-test.properties](../src/main/resources/datasource-test.properties)

#### 프로필 설정

`spring.profiles.active` 의 값은 local, test, prod 등으로 나누어 놓았습니다.
dev, stage 혹은 개인에 맞게 커스텀하려고한다면 application.properties, datasource.properties 파일을 쌍으로 추가하여 확장 가능합니다.

#### 프로그램 인자 설정

커맨드라인 인자로 다음의 값을 할당합니다. properties 파일을 수정하여도 좋습니다.
```shell
--spring.thymeleaf.prefix=file:///$PROJECT_DIR$/seesaw-web/src/main/resources/templates/
--spring.web.resources.static-locations=file:///$PROJECT_DIR$/seesaw-web/src/main/resources/static/
--spring.messages.basename=file:///$PROJECT_DIR$/seesaw-web/src/main/resources/messages/
--site.domain.name=YOUR_SITE_DOMAIN
--spring.security.oauth2.client.registration.naver.client-id=YOUR_CLIENT_ID
--spring.security.oauth2.client.registration.naver.client-secret=YOUR_CLIENT_SECRET
--spring.security.oauth2.client.registration.naver.redirect-uri=YOUR_REDIRECT_URI
--kr.me.seesaw.filepath=YOUR_PATH
--spring.datasource.url=YOUR_URL
--spring.datasource.username=YOUR_USERNAME
--spring.datasource.password=YOUR_PASSWORD
```

## 데이터베이스 설정

### 초기 설정

1. [데이터베이스 계정 생성 및 테이블 생성](database/init.sql)
2. [테이블 데이터 삽입](./database/initial_data-1.0.0.sql)
3. [파일 다운로드(첨부파일)](https://nas.nxcoa.com)
   - 첨부파일을 받지않아도 실행은 되지만 기존 데이터 중 파일데이터를 찾을 수 없습니다.

### 데이터베이스 연결 설정

데이터베이스 연결 설정은 `datasource.properties` 파일에서 관리됩니다. 자세한 내용은 위의 [환경변수 설정](#환경변수-설정) 섹션을 참조하세요.

## 빌드 가이드

### 빌드 명령어

프로젝트를 빌드하려면 다음 명령어를 사용합니다:

```shell
gradlew clean bootJar -PappVersion=1.0.0
```

이 명령어는 프로젝트를 정리하고, Spring Boot 실행 가능한 JAR 파일을 생성합니다. `-PappVersion` 파라미터는 애플리케이션 버전을 지정합니다.

### 빌드 결과물

빌드가 성공적으로 완료되면 `build/libs` 디렉토리에 JAR 파일이 생성됩니다.

### 버전 관리

프로젝트의 버전 관리에 대한 자세한 내용은 [소프트웨어 버전 관리 가이드](versioning.md)를 참조하세요.

## 관련 문서

- [소프트웨어 버전 관리 가이드](versioning.md)
