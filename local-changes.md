# Git 변경 사항 분석 및 커밋 메시지 요청

## 1. 현재 작업 상태 (git status)
```text
On branch main
Your branch is up to date with 'origin/main'.

Changes to be committed:
  (use "git restore --staged <file>..." to unstage)
	new file:   docs/CODING_CONVENTION.md
	new file:   docs/CONVENTIONAL_COMMITS.md
	new file:   docs/GITHUB_TEMPLATES.md
	new file:   docs/LAYERED_ARCHITECTURE.md
	new file:   docs/PACKAGE_ANALYSIS.md


```

## 2. 상세 변경 내용 (git diff)
아래는 스테이징된 변경 사항과 아직 스테이징되지 않은 모든 변경 내용입니다.

### [Staged Changes]
```diff
diff --git a/docs/CODING_CONVENTION.md b/docs/CODING_CONVENTION.md
new file mode 100644
index 00000000..0306b606
--- /dev/null
+++ b/docs/CODING_CONVENTION.md
@@ -0,0 +1,62 @@
+# 코딩 컨벤션 (Coding Convention)
+
+chat-demo 프로젝트는 일관된 유지보수성과 예측 가능한 변경을 위해 아래 코딩 컨벤션을 따릅니다.
+
+## 1. DTO 설계 규칙
+
+- DTO는 `record`를 기본으로 사용합니다.
+- DTO 생성은 `@Builder` 패턴을 사용합니다.
+- 엔티티를 DTO로 변환할 때는 정적 팩터리(`from`)를 권장합니다.
+- 예시: `UserResponse`는 `record + @Builder`를 사용합니다.
+
+## 2. 엔티티 연관관계 규칙
+
+- 엔티티 설계 시 `OneToMany` 직접 매핑을 지양합니다.
+- 목록 조회는 연관 컬렉션 탐색 대신 `IN` 조건 기반 조회 후 애플리케이션 레이어에서 메모리 조인으로 조합합니다.
+- `OneToMany`에 대응되는 집계/목록 모델은 엔티티가 아닌 DTO에서 처리합니다.
+- 목적:
+    - 순환참조 위험 감소 (양방향 연관으로 인한 구조 결합 방지)
+    - 불필요한 컬렉션 로딩 방지
+    - N+1 및 영속성 컨텍스트 복잡도 최소화
+    - 조회/응답 모델 분리
+
+## 3. Repository 규칙
+
+- Repository 인터페이스는 `JpaRepository`가 아닌 `org.springframework.data.repository.Repository`를 확장합니다.
+- 필요한 메서드만 명시적으로 선언해 저장소 책임을 제한합니다.
+
+## 4. 엔티티 수정 규칙
+
+- 엔티티 수정(상태 변경)이 목적일 때는 `findById`보다 `getReferenceById` 사용을 우선하여 불필요한 SELECT를 방지합니다.
+- 트랜잭션 내 더티 체킹(Dirty Checking) 기반 갱신을 기본 전략으로 사용합니다.
+
+## 5. 예외 처리 규칙
+
+- 예외 응답 변환은 `@ExceptionHandler` 또는 전역 `ControllerAdvice`에서 처리합니다.
+- `Controller`와 `Service`는 예외 포매팅/응답 바디 구성에 직접 관여하지 않습니다.
+- 서비스는 도메인 관점의 예외를 발생시키고, 응답 정책은 예외 처리 계층에서 일괄 관리합니다.
+
+## 6. 서비스/컨텍스트 컴포넌트 규칙
+
+- **비즈니스 로직:** 비즈니스 프로세스와 상태 변경 로직은 `Service` 컴포넌트에서 담당합니다.
+- **HTTP 의존 분리:** `Service` 레이어는 HTTP/서블릿 및 웹 보안 구현에 직접 의존하지 않습니다.
+    - 금지 대상: `HttpSession`, `SecurityContext`, `HttpServletRequest`, `HttpServletResponse` 등 웹 요청/응답 객체 직접 참조
+    - 처리 원칙: 필요한 사용자/권한/요청 정보는 컨트롤러 또는 `Context` 컴포넌트에서 해석 후 서비스 메서드 파라미터로 전달
+- **교차 관심사 분리 (Context):** 권한 확인, 현재 사용자 조회 등 반복되는 조회 및 정보 제공 책임은 별도의 `Context` 컴포넌트로 분리하여 서비스 레이어의 복잡도를 낮춥니다.
+    - **의존성 규칙:** `Context`는 다른 `Service`를 참조하지 않고 오직 `Repository` 또는 보안 컨텍스트만 참조하여 순환 참조를 방지합니다.
+- **@RequestScope를 통한 효율화:**
+    - 요청 단위 데이터 일관성과 성능을 위해 `Context` 컴포넌트에 `@RequestScope`를 적용합니다. 주입 안정성을 위해 `proxyMode` 설정을 포함합니다.
+    - **명시적 메모이제이션(Memoization):** 동일 요청 내 중복 쿼리를 방지하기 위해 내부 필드(Map, 변수 등)를 활용한 캐싱 로직을 반드시 구현합니다.
+- **보안 선언부 관리:**
+    - `@PreAuthorize`에서 `Context` 빈을 직접 호출하여 권한을 체크합니다. (예: `@PreAuthorize("@chatRoomContext.isMember(#roomId, principal.username)")`)
+    - 이때 IDE의 정적 분석 기능을 활용할 수 있도록 빈 이름을 명확하게 관리하고, 메서드 시그니처를 간결하게 유지합니다.
+- **목적:**
+    - 비즈니스 로직과 권한/조회 로직의 명확한 계층 분리
+    - 서비스 로직의 HTTP 기술 종속 제거로 테스트 용이성 및 변경 내성 강화
+    - 불필요한 레이어(Evaluator 등) 추가 없이 IDE의 지원을 받으며 생산성 극대화
+    - 요청 내 중복 조회 최소화로 DB 부하 감소
+
+## 7. 기타 일반 규칙
+
+- 명시되지 않은 코딩 스타일은 Java Language Specification과 Spring Framework 관례를 따릅니다.
+- 네이밍, 패키지 구조, 애너테이션 사용은 기존 코드베이스의 일관성을 우선합니다.
diff --git a/docs/CONVENTIONAL_COMMITS.md b/docs/CONVENTIONAL_COMMITS.md
new file mode 100644
index 00000000..f1ee6b54
--- /dev/null
+++ b/docs/CONVENTIONAL_COMMITS.md
@@ -0,0 +1,176 @@
+# Conventional Commits 1.0.0
+
+## 요약
+
+Conventional Commits 명세는 커밋 메시지에 대한 가벼운 규칙입니다.
+명시적인 커밋 히스토리를 만들기 위한 간단한 규칙을 제공하여
+자동화된 도구를 더 쉽게 작성할 수 있게 합니다.
+이 규칙은 커밋 메시지에서 기능, 수정사항, 주요 변경사항을 설명함으로써
+[SemVer](http://semver.org)와 잘 어울립니다.
+
+커밋 메시지는 다음과 같은 구조로 작성해야 합니다:
+
+---
+```
+<타입>[선택적 범위]: <설명>
+
+[선택적 본문]
+
+[선택적 꼬리말]
+```
+---
+
+커밋은 라이브러리 사용자에게 의도를 전달하기 위해 다음과 같은 구조적 요소를 포함합니다:
+
+1. **fix:** *타입* `fix`의 커밋은 코드베이스의 버그를 패치합니다(이는 시맨틱 버전 관리의 [`PATCH`](http://semver.org/#summary)와 관련이 있습니다).
+2. **feat:** *타입* `feat`의 커밋은 코드베이스에 새로운 기능을 도입합니다(이는 시맨틱 버전 관리의 [`MINOR`](http://semver.org/#summary)와 관련이 있습니다).
+3. **BREAKING CHANGE:** `BREAKING CHANGE:` 꼬리말이 있거나 타입/범위 뒤에 `!`가 붙은 커밋은 주요 API 변경을 의미합니다(시맨틱 버전 관리의 [`MAJOR`](http://semver.org/#summary)와 관련이 있습니다). BREAKING CHANGE는 모든 *타입*의 커밋에 포함될 수 있습니다.
+4. `fix:`와 `feat:` 이외의 *타입*도 허용됩니다. 예를 들어, [@commitlint/config-conventional](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional)([Angular 규칙](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines) 기반)은 `build:`, `chore:`, `ci:`, `docs:`, `style:`, `refactor:`, `perf:`, `test:` 등을 권장합니다.
+5. `BREAKING CHANGE: <설명>` 이외의 *꼬리말*도 제공될 수 있으며 [git trailer 형식](https://git-scm.com/docs/git-interpret-trailers)과 유사한 규칙을 따릅니다.
+
+Conventional Commits 명세에서 추가 타입은 의무사항이 아니며, 시맨틱 버전 관리에 암시적인 영향을 미치지 않습니다(BREAKING CHANGE를 포함하지 않는 한).
+커밋의 타입에 범위를 제공하여 추가적인 문맥 정보를 제공할 수 있으며, 괄호 안에 포함됩니다. 예: `feat(parser): 배열 파싱 기능 추가`.
+
+## 예시
+
+### 설명과 breaking change 꼬리말이 있는 커밋 메시지
+
+```
+feat: 제공된 설정 객체가 다른 설정을 확장할 수 있도록 허용
+
+BREAKING CHANGE: 설정 파일의 `extends` 키가 이제 다른 설정 파일을 확장하는 데 사용됩니다
+```
+
+### breaking change에 주의를 끌기 위한 `!`가 있는 커밋 메시지
+
+```
+feat!: 제품이 배송될 때 고객에게 이메일 전송
+```
+
+### 범위와 breaking change에 주의를 끌기 위한 `!`가 있는 커밋 메시지
+
+```
+feat(api)!: 제품이 배송될 때 고객에게 이메일 전송
+```
+
+### `!`와 BREAKING CHANGE 꼬리말이 모두 있는 커밋 메시지
+
+```
+chore!: Node 6 지원 중단
+
+BREAKING CHANGE: Node 6에서 사용할 수 없는 JavaScript 기능을 사용합니다.
+```
+
+### 본문이 없는 커밋 메시지
+
+```
+docs: CHANGELOG의 철자 수정
+```
+
+### 범위가 있는 커밋 메시지
+
+```
+feat(lang): 폴란드어 추가
+```
+
+### 여러 단락의 본문과 여러 꼬리말이 있는 커밋 메시지
+
+```
+fix: 요청 경합 방지
+
+요청 ID와 최신 요청에 대한 참조를 도입합니다. 최신 요청 이외의
+들어오는 응답을 무시합니다.
+
+경합 문제를 완화하는 데 사용되었지만 이제 불필요한 타임아웃을
+제거합니다.
+
+Reviewed-by: Z
+Refs: #123
+```
+
+## 명세
+
+이 문서에서 "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", "OPTIONAL"이라는 키워드는 [RFC 2119](https://www.ietf.org/rfc/rfc2119.txt)에 설명된 대로 해석됩니다.
+
+1. 커밋은 `feat`, `fix` 등의 명사로 구성된 타입으로 시작해야 하며, 그 뒤에 선택적 범위, 선택적 `!`, 필수 콜론과 공백이 따라옵니다.
+2. 타입 `feat`는 애플리케이션이나 라이브러리에 새로운 기능을 추가할 때 사용해야 합니다.
+3. 타입 `fix`는 애플리케이션의 버그를 수정할 때 사용해야 합니다.
+4. 타입 뒤에 범위를 제공할 수 있습니다. 범위는 괄호로 둘러싸인 코드베이스의 섹션을 설명하는 명사여야 합니다. 예: `fix(parser):`
+5. 설명은 타입/범위 접두사 뒤의 콜론과 공백 바로 뒤에 와야 합니다. 설명은 코드 변경에 대한 짧은 요약입니다. 예: *fix: 문자열에 여러 공백이 포함된 경우 배열 파싱 문제*.
+6. 짧은 설명 뒤에 더 긴 커밋 본문을 제공할 수 있으며, 코드 변경에 대한 추가 문맥 정보를 제공합니다. 본문은 설명 뒤에 빈 줄로 시작해야 합니다.
+7. 커밋 본문은 자유 형식이며 여러 개의 줄바꿈으로 구분된 단락으로 구성될 수 있습니다.
+8. 본문 뒤에 하나 이상의 꼬리말을 제공할 수 있으며, 각 꼬리말은 본문 뒤에 빈 줄로 시작해야 합니다. 각 꼬리말은 단어 토큰으로 구성되어야 하며, 그 뒤에 `:<공백>` 또는 `<공백>#` 구분자가 오고, 문자열 값이 따라옵니다(이는 [git trailer 규칙](https://git-scm.com/docs/git-interpret-trailers)에서 영감을 받았습니다).
+9. 꼬리말의 토큰은 공백 문자 대신 `-`를 사용해야 합니다. 예: `Acked-by`(이는 다단락 본문과 꼬리말 섹션을 구분하는 데 도움이 됩니다). `BREAKING CHANGE`는 토큰으로 사용될 수 있는 예외입니다.
+10. 꼬리말의 값은 공백과 줄바꿈을 포함할 수 있으며, 다음 유효한 꼬리말 토큰/구분자 쌍이 관찰될 때 파싱이 종료됩니다.
+11. 주요 변경사항은 커밋의 타입/범위 접두사나 꼬리말 항목으로 표시되어야 합니다.
+12. 꼬리말로 포함된 경우, 주요 변경사항은 대문자 텍스트 BREAKING CHANGE로 시작하고 콜론, 공백, 설명이 따라와야 합니다. 예: *BREAKING CHANGE: 환경 변수가 이제 설정 파일보다 우선합니다*.
+13. 타입/범위 접두사에 포함된 경우, 주요 변경사항은 `:`의 바로 앞에 `!`로 표시되어야 합니다. `!`가 사용된 경우, 꼬리말 섹션에서 `BREAKING CHANGE:`를 생략할 수 있으며, 커밋 설명이 주요 변경사항을 설명하는 데 사용됩니다.
+14. `feat`와 `fix` 이외의 타입도 커밋 메시지에 사용할 수 있습니다. 예: *docs: 참조 문서 업데이트*.
+15. Conventional Commits를 구성하는 정보 단위는 대소문자를 구분하지 않아야 하지만, 대문자여야 하는 BREAKING CHANGE는 예외입니다.
+16. BREAKING-CHANGE는 꼬리말의 토큰으로 사용될 때 BREAKING CHANGE와 동의어여야 합니다.
+
+## Conventional Commits를 사용하는 이유
+
+* CHANGELOG를 자동으로 생성합니다.
+* 의미론적 버전 범프를 자동으로 결정합니다(커밋된 변경 유형에 기반).
+* 팀원, 대중 및 기타 이해관계자에게 변경 사항의 성격을 전달합니다.
+* 빌드 및 게시 프로세스를 트리거합니다.
+* 더 구조화된 커밋 히스토리를 탐색할 수 있게 함으로써 사람들이 프로젝트에 기여하기 쉽게 만듭니다.
+
+## FAQ
+
+### 초기 개발 단계에서 커밋 메시지를 어떻게 다루어야 하나요?
+
+제품을 이미 출시한 것처럼 진행하는 것이 좋습니다. 일반적으로 *누군가*는, 그것이 동료 개발자일지라도, 여러분의 소프트웨어를 사용하고 있습니다. 그들은 무엇이 수정되었고, 무엇이 깨졌는지 등을 알고 싶어할 것입니다.
+
+### 커밋 제목의 타입은 대문자인가요 소문자인가요?
+
+어떤 대소문자도 사용할 수 있지만, 일관성을 유지하는 것이 가장 좋습니다.
+
+### 커밋이 여러 커밋 타입에 부합하는 경우 어떻게 해야 하나요?
+
+가능하면 돌아가서 여러 커밋으로 나누는 것이 좋습니다. Conventional Commits의 이점 중 하나는 더 조직화된 커밋과 PR을 만들도록 유도한다는 것입니다.
+
+### 이것이 빠른 개발과 빠른 반복을 방해하지 않나요?
+
+무질서한 방식으로 빠르게 움직이는 것을 방지합니다. 다양한 기여자가 있는 여러 프로젝트에서 장기적으로 빠르게 움직일 수 있도록 도와줍니다.
+
+### Conventional Commits가 개발자들이 제공된 타입에 맞춰 생각하게 되어 커밋 타입을 제한하게 만들 수 있나요?
+
+Conventional Commits는 수정사항과 같은 특정 유형의 커밋을 더 많이 만들도록 권장합니다. 그 외에도, Conventional Commits의 유연성은 팀이 자체 타입을 만들고 시간이 지남에 따라 그 타입을 변경할 수 있게 합니다.
+
+### 이것이 SemVer와 어떤 관련이 있나요?
+
+`fix` 타입 커밋은 `PATCH` 릴리스로 변환되어야 합니다. `feat` 타입 커밋은 `MINOR` 릴리스로 변환되어야 합니다. 타입에 관계없이 커밋에 `BREAKING CHANGE`가 있는 경우 `MAJOR` 릴리스로 변환되어야 합니다.
+
+### Conventional Commits 명세에 대한 확장을 어떻게 버전 관리해야 하나요? 예: `@jameswomack/conventional-commit-spec`
+
+SemVer를 사용하여 이 명세에 대한 자체 확장을 릴리스하는 것을 권장합니다(그리고 이러한 확장을 만들 것을 권장합니다!).
+
+### 실수로 잘못된 커밋 타입을 사용한 경우 어떻게 해야 하나요?
+
+#### 명세에 맞는 타입이지만 올바른 타입이 아닌 경우, 예: `fix` 대신 `feat`
+
+실수를 병합하거나 릴리스하기 전에, `git rebase -i`를 사용하여 커밋 히스토리를 편집하는 것을 권장합니다. 릴리스 후에는 사용하는 도구와 프로세스에 따라 정리 방법이 달라집니다.
+
+#### 명세에 맞지 않는 타입을 사용한 경우, 예: `feat` 대신 `feet`
+
+최악의 경우, Conventional Commits 명세를 충족하지 않는 커밋이 들어가도 세상이 끝나는 것은 아닙니다. 단지 그 커밋이 명세를 기반으로 하는 도구에 의해 누락된다는 것을 의미합니다.
+
+### 모든 기여자가 Conventional Commits 명세를 사용해야 하나요?
+
+아니요! Git에서 스쿼시 기반 워크플로우를 사용하는 경우, 리드 유지 관리자는 병합될 때 커밋 메시지를 정리할 수 있어 일반 커미터에게 추가 작업이 필요하지 않습니다.
+일반적인 워크플로우는 Git 시스템이 풀 리퀘스트의 커밋을 자동으로 스쿼시하고 리드 유지 관리자가 병합을 위한 적절한 Git 커밋 메시지를 입력할 수 있는 양식을 제공하는 것입니다.
+
+### Conventional Commits는 되돌리기 커밋을 어떻게 처리하나요?
+
+코드를 되돌리는 것은 복잡할 수 있습니다: 여러 커밋을 되돌리고 있나요? 기능을 되돌리는 경우, 다음 릴리스가 패치여야 하나요?
+
+Conventional Commits는 되돌리기 동작을 명시적으로 정의하려고 노력하지 않습니다. 대신 도구 작성자가 되돌리기를 처리하는 논리를 개발하기 위해 *타입*과 *꼬리말*의 유연성을 사용하도록 맡깁니다.
+
+한 가지 권장사항은 `revert` 타입을 사용하고 되돌려지는 커밋 SHA를 참조하는 꼬리말을 사용하는 것입니다:
+
+```
+revert: 국수 사건에 대해 다시는 언급하지 말자
+Refs: 676104e, a215868
+```
\ No newline at end of file
diff --git a/docs/GITHUB_TEMPLATES.md b/docs/GITHUB_TEMPLATES.md
new file mode 100644
index 00000000..32b37b4c
--- /dev/null
+++ b/docs/GITHUB_TEMPLATES.md
@@ -0,0 +1,88 @@
+# GitHub 템플릿 가이드
+
+이 문서는 chat-demo 프로젝트에서 사용되는 GitHub 템플릿에 대한 가이드를 제공합니다.
+
+## 템플릿 구조
+
+```
+.github/
+├── ISSUE_TEMPLATE/
+│   ├── bug_report.md      # 버그 리포트 템플릿
+│   ├── feature_request.md # 기능 요청 템플릿
+│   ├── general.md         # 일반 이슈 템플릿
+│   └── config.yml         # 이슈 템플릿 설정
+├── PULL_REQUEST_TEMPLATE.md # PR 템플릿
+└── RELEASE_TEMPLATE.md    # 릴리스 노트 템플릿
+```
+
+## 이슈 템플릿
+
+### 버그 리포트 (`bug_report.md`)
+버그를 보고할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
+- 버그 설명
+- 재현 방법
+- 기대한 동작
+- 실제 동작
+- 스크린샷 (가능한 경우)
+- 환경 정보
+- 추가 정보
+- 가능한 해결책
+
+### 기능 요청 (`feature_request.md`)
+새로운 기능이나 개선 사항을 제안할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
+- 기능 요청 설명
+- 관련 문제
+- 원하는 해결책
+- 대안 고려사항
+- 사용 사례
+- 추가 정보
+- 구현 아이디어 (선택사항)
+
+### 일반 이슈 (`general.md`)
+버그나 기능 요청이 아닌 일반적인 이슈를 생성할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
+- 이슈 설명
+- 현재 상황
+- 기대하는 결과
+- 해결 방안 (선택사항)
+- 추가 정보
+- 체크리스트
+
+## 풀 리퀘스트 템플릿
+
+풀 리퀘스트를 생성할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
+- PR 유형
+- 변경 사항 설명
+- 관련 이슈
+- 테스트 정보
+- 체크리스트
+- 배포 고려사항
+- 스크린샷 (선택사항)
+- 추가 정보
+
+## 릴리스 노트 템플릿
+
+릴리스 노트를 작성할 때 사용하는 템플릿입니다. 다음 정보를 포함해야 합니다:
+- 버전 정보 (버전 번호, 릴리스 날짜)
+- 주요 변경 사항 (새로운 기능, 개선 사항, 버그 수정)
+- 상세 변경 내역 (기술적 변경 사항, API 변경 사항)
+- 관련 이슈 및 PR
+- 배포 정보 (배포 요구사항, 배포 단계)
+- 알려진 이슈
+- 사용 가이드
+- 테스트 결과
+- 추가 정보
+
+## 템플릿 사용 방법
+
+1. **이슈 생성**: GitHub 저장소의 "Issues" 탭에서 "New issue" 버튼을 클릭하면 사용 가능한 템플릿 목록이 표시됩니다.
+2. **PR 생성**: "Pull requests" 탭에서 "New pull request" 버튼을 클릭하고 PR을 생성하면 자동으로 PR 템플릿이 로드됩니다.
+3. **릴리스 노트 작성**: 
+   - GitHub 저장소의 "Releases" 탭에서 "Publish release" 버튼을 클릭합니다.
+   - 태그 버전(예: v1.0.1)을 입력하고, 타겟 브랜치(예: prod)를 선택합니다.
+   - 릴리스 제목을 입력합니다.
+   - 릴리스 설명 부분에 `.github/RELEASE_TEMPLATE.md` 파일의 내용을 복사하여 붙여넣고 필요한 정보를 작성합니다.
+   - 작성이 완료되면 "Publish release" 버튼을 클릭하여 릴리스를 게시합니다.
+
+## 템플릿 수정
+
+템플릿을 수정하려면 해당 파일을 직접 편집하면 됩니다. 모든 템플릿은 마크다운 형식으로 작성되어 있으며, 프로젝트의 요구사항에 맞게 자유롭게 수정할 수 있습니다.
\ No newline at end of file
diff --git a/docs/LAYERED_ARCHITECTURE.md b/docs/LAYERED_ARCHITECTURE.md
new file mode 100644
index 00000000..2419980e
--- /dev/null
+++ b/docs/LAYERED_ARCHITECTURE.md
@@ -0,0 +1,99 @@
+# 레이어드 아키텍처 (Layered Architecture)
+
+chat-demo 프로젝트는 관심사의 분리와 유지보수성 향상을 위해 표준 레이어드 아키텍처를 따릅니다.
+
+## 아키텍처 다이어그램
+
+```mermaid
+graph TD
+    subgraph PresentationLayer [Presentation Layer]
+        Controller[API Controller]
+    end
+
+    subgraph InterfaceLayer [DTO]
+        RequestDTO[Request DTO]
+        ResponseDTO[Response DTO]
+        Search[Search Object]
+    end
+
+    subgraph ApplicationLayer [Application Layer]
+        Service["<<interface>>\nService"]
+        ServiceImpl[DefaultService]
+    end
+
+    subgraph DomainLayer [Domain Layer]
+        Domain["Domain (Entity)"]
+        Repository["<<interface>>\nRepository"]
+    end
+
+    subgraph InfrastructureLayer [Infrastructure Layer]
+        RepositoryImpl[Repository Implementation]
+        JpaRepository["<<interface>>\nJpaRepository"]
+    end
+
+    %% 흐름 정의
+    Controller --> RequestDTO
+    Controller --> Search
+    Controller --> Service
+    
+    Service -.-> ServiceImpl
+    
+    ServiceImpl --> RequestDTO
+    ServiceImpl --> ResponseDTO
+    ServiceImpl --> Domain
+    ServiceImpl --> Repository
+    
+    Repository -.-> RepositoryImpl
+    RepositoryImpl --> JpaRepository
+    RepositoryImpl --> Domain
+
+    %% 스타일링
+    style Controller fill:#c35b5b,stroke:#333,stroke-width:2px,color:#fff
+    style Service fill:#4682b4,stroke:#333,stroke-width:2px,color:#fff
+    style ServiceImpl fill:#4682b4,stroke:#333,stroke-width:2px,color:#fff
+    style Domain fill:#3cb371,stroke:#333,stroke-width:2px,color:#fff
+    style Repository fill:#3cb371,stroke:#333,stroke-width:2px,color:#fff
+    style RepositoryImpl fill:#daa520,stroke:#333,stroke-width:2px,color:#fff
+    style JpaRepository fill:#daa520,stroke:#333,stroke-width:2px,color:#fff
+    style RequestDTO fill:#808080,stroke:#333,stroke-dasharray: 5 5,color:#fff
+    style ResponseDTO fill:#808080,stroke:#333,stroke-dasharray: 5 5,color:#fff
+    style Search fill:#808080,stroke:#333,stroke-dasharray: 5 5,color:#fff
+```
+
+## 레이어별 역할
+
+### 1. Presentation Layer (컨트롤러)
+- **구성 요소**: `*ApiController`
+- **역할**:
+    - HTTP 요청 수신 및 응답 반환
+    - 사용자 권한 검증 (`@PreAuthorize`)
+    - 입력 데이터 검증 (`@Valid`)
+    - 서비스 메서드 호출 및 결과 반환
+
+### 2. Application Layer (서비스)
+- **구성 요소**: `*Service` (인터페이스), `Default*Service` (구현체)
+- **역할**:
+    - 비즈니스 로직 수행 및 트랜잭션 관리
+    - **입력**: `Command` 또는 `Search` 객체 사용
+    - **출력**: `Model` 객체 반환 (엔티티 직접 노출 금지)
+    - 엔티티와 모델 간의 변환 작업
+
+### 3. Domain Layer (도메인)
+- **구성 요소**: `Entity`, `Repository` (인터페이스)
+- **역할**:
+    - 비즈니스 핵심 규칙 및 상태 관리
+    - 데이터 액세스를 위한 추상화 인터페이스 제공
+    - 모든 엔티티는 `BaseEntity`를 상속받음
+
+### 4. Infrastructure Layer (인프라)
+- **구성 요소**: `JpaRepository`, `RepositoryImpl`
+- **역할**:
+    - 실제 데이터베이스 액세스 구현
+    - 외부 시스템 연동 (파일 시스템, 메일 서버 등)
+
+## 주요 원칙
+
+- **불변 모델**: `Model` 객체는 `final` 클래스이며 불변성을 유지해야 합니다.
+- **명확한 입력/출력**: 서비스 레이어의 입력은 `Command`, 출력은 `Model`을 사용하는 것을 원칙으로 합니다.
+- **의존성 방향**: 상위 레이어에서 하위 레이어로의 단방향 의존성을 유지합니다.
+- **지연 로딩**: JPA 연관 관계는 항상 `LAZY` 로딩을 사용합니다.
diff --git a/docs/PACKAGE_ANALYSIS.md b/docs/PACKAGE_ANALYSIS.md
new file mode 100644
index 00000000..fd1d8857
--- /dev/null
+++ b/docs/PACKAGE_ANALYSIS.md
@@ -0,0 +1,2 @@
+# 패키지 구조 분석
+

```

### [Unstaged Changes]
```diff
수정된 변경 사항이 없습니다.
```

---
## 3. 요청 사항
위 변경 사항을 분석하여 다음 조건에 맞는 Git 커밋 메시지를 추천해줘.
- Conventional Commits 규격을 따를 것 (feat, fix, refactor, chore 등)
- 한글로 작성할 것
- 변경된 핵심 내용을 요약하여 리스트 형태로 포함할 것
- 변경 내용을 추측하지 말고 사실 기반으로 작성할 것
