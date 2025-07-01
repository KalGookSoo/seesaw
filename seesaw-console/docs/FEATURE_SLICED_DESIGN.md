## FSD(Feature-Sliced Design)란?

- **기능(Feature) 단위로 코드 분리**  
  (예: auth, profile, article 등 실제 비즈니스 도메인 중심)
- 각 기능(Feature) 내부에서 또 **Layer(계층)**로 세분화  
  - `entities`, `features`, `widgets`, `pages`, `shared` 등

---

## 대표적인 FSD 디렉토리 구조

```plaintext
src/
├── app/           # 앱 설정, 엔트리 포인트, 공통 providers
├── entities/      # 핵심 비즈니스 엔터티(모델, CRUD 등)
├── features/      # 사용자 액션, 비즈니스 로직(로그인, 댓글 추가 등)
├── widgets/       # 여러 features/entities 조합된 UI 블록(예: 헤더, 사이드바)
├── pages/         # 라우팅 단위의 페이지, 여러 widgets/features 조합
├── shared/        # 유틸리티, 공통 컴포넌트, 타입, 훅 등
```

---

### 각 폴더 설명

- **app/**  
  앱의 진입점, 라우팅, 전역 설정(Context, Redux Provider 등)
- **entities/**  
  도메인 모델(예: User, Article) 및 해당 모델의 CRUD 기능
- **features/**  
  사용자의 특정 액션, 비즈니스 피처 단위(예: 로그인, 좋아요 버튼)
- **widgets/**  
  화면의 독립적인 UI 블록(예: Header, Sidebar)
- **pages/**  
  라우팅 단위(전체 페이지), 여러 widgets, features 조합
- **shared/**  
  Button, Input, hooks, utils, constants 등 **전역적으로 쓰이는** 코드

---

## 예시 구조

```plaintext
src/
├── app/
│   └── App.tsx
│   └── providers/
├── entities/
│   └── user/
│       └── model/
│       └── ui/
│   └── article/
│       └── model/
│       └── ui/
├── features/
│   └── login/
│       └── ui/
│       └── model/
│   └── add-comment/
│       └── ui/
│       └── model/
├── widgets/
│   └── Header/
│   └── Sidebar/
├── pages/
│   └── HomePage/
│   └── ArticlePage/
├── shared/
│   └── ui/
│       └── Button/
│       └── Input/
│   └── lib/
│   └── config/
│   └── api/
```

---

## FSD 구조의 장점

- **도메인/기능 단위로 분리** → 코드 확장, 유지보수 쉬움
- **레이어별 역할 명확화** → 의존성 관리가 쉬움
- **대규모/팀 개발에 적합** → 충돌 줄고, 협업 효율 증가

---

## 참고자료

- [공식 문서 (feature-sliced.design)](https://feature-sliced.design/)
- [FSD 깃허브](https://github.com/feature-sliced)

궁금한 부분이나 구체적인 예시, 적용법이 필요하면 언제든 요청해 주세요!
