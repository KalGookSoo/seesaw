# Redux Toolkit: 커스텀 훅 파일/폴더 구조 가이드

## 1. 커스텀 훅을 쓰는 이유와 장점

Redux Toolkit과 TypeScript를 함께 사용할 때 커스텀 훅(`useAppSelector`, `useAppDispatch`)을 만드는 것은 아래와 같은 이유와 장점이 있습니다.

### 1.1 타입 안전성 보장

- 기본 `useSelector`, `useDispatch`는 타입 정보를 알지 못함
- 커스텀 훅에 타입을 지정해두면 컴포넌트에서 **타입을 별도 지정할 필요 없음**
- 타입스크립트의 자동완성, 타입 검사 모두 지원

### 1.2 코드 간결성과 일관성

- 반복적으로 타입 지정하지 않고 코드를 더 깔끔하게 작성
- 팀원 모두 동일한 방식으로 Redux store에 접근

### 1.3 확장성

- 훅 내부에 로깅, 권한 체크 등 부가 기능을 넣어 확장 가능

### 1.4 재사용성

- 한 번만 정의해두면 여러 컴포넌트에서 재사용 가능

---

## 2. 커스텀 훅 예시

```tsx
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import type { RootState, AppDispatch } from './store';

export const useAppDispatch: () => AppDispatch = useDispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;

```

---

## 3. 슬라이스가 많아질 때 폴더/파일 구조 예시

Slice(슬라이스) 수가 많아질수록, 아래와 같은 구조로 관리하는 것이 유지보수와 확장에 효과적입니다.

```
src/
├── app/
│   ├── store.ts              # store 설정 및 루트 리듀서 결합
│   └── hooks.ts              # 커스텀 훅 (useAppDispatch, useAppSelector)
├── features/
│   ├── counter/
│   │   └── counterSlice.ts
│   ├── auth/
│   │   └── authSlice.ts
│   ├── user/
│   │   └── userSlice.ts
│   ├── posts/
│   │   └── postsSlice.ts
│   └── ... (기타 slice 폴더)
├── components/
│   └── ... (React 컴포넌트)
└── index.tsx

```

- **app/**: 글로벌 store와 커스텀 훅 등 공통 상태 관리 파일
- **features/**: slice별로 디렉토리 분리 (관심사 기반)

---

## 4. 주요 파일 예시

### 4.1 store.ts

```tsx
import { configureStore } from '@reduxjs/toolkit';
import counterReducer from '../features/counter/counterSlice';
import authReducer from '../features/auth/authSlice';
import userReducer from '../features/user/userSlice';
import postsReducer from '../features/posts/postsSlice';
// ...다른 slice import

export const store = configureStore({
  reducer: {
    counter: counterReducer,
    auth: authReducer,
    user: userReducer,
    posts: postsReducer,
    // ...다른 slice 추가
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

```

### 4.2 각 슬라이스 예시 (counterSlice.ts)

```tsx
import { createSlice } from '@reduxjs/toolkit';

interface CounterState {
  value: number;
}

const initialState: CounterState = { value: 0 };

const counterSlice = createSlice({
  name: 'counter',
  initialState,
  reducers: {
    increment(state) {
      state.value += 1;
    },
    decrement(state) {
      state.value -= 1;
    },
  }
});

export default counterSlice.reducer;
export const { increment, decrement } = counterSlice.actions;

```

---

## 5. 컴포넌트에서 사용 예시

```tsx
import { useAppSelector, useAppDispatch } from '../app/hooks';
import { increment, decrement } from '../features/counter/counterSlice';

function Counter() {
  const counter = useAppSelector((state) => state.counter.value);
  const dispatch = useAppDispatch();

  return (
    <div>
      <button onClick={() => dispatch(increment())}>+</button>
      <span>{counter}</span>
      <button onClick={() => dispatch(decrement())}>-</button>
    </div>
  );
}

export default Counter;

```

---

## 6. 결론

- **커스텀 훅은 타입 안전성, 코드 일관성·간결성, 확장성, 재사용성 등 여러 장점이 있습니다.**
- **폴더 구조는 features/슬라이스별 폴더, app/store.ts & hooks.ts 패턴이 확장성과 유지보수에 매우 효율적입니다.**
- 이 구조와 패턴은 실제로 대규모 프로젝트에서 널리 사용됩니다.
