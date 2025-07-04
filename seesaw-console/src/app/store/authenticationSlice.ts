import { createSlice, createAsyncThunk, type PayloadAction } from '@reduxjs/toolkit'
import type { AppDispatch, RootState } from '@/app/store/index.ts'

// 타입 정의
export interface SignInCommand {
  username: string
  password: string
}

export interface JsonWebToken {
  token: string
}

export interface Profile {
  token: string
  authorities: string[]
  exp: string
  iat: string
  sub: string
}

// 인증 상태 인터페이스
interface AuthenticationState {
  isAuthenticated: boolean
  profile: Profile | null
  isLoading: boolean
  error: string | null
  user: {
    name: string
    email: string
    avatar?: string
  } | null
}

// 초기 상태
const initialState: AuthenticationState = {
  isAuthenticated: false,
  profile: null,
  isLoading: false,
  error: null,
  user: null
}

// 로그인 비동기 액션
export const signIn = createAsyncThunk(
  'authentication/signIn',
  async (command: SignInCommand, { rejectWithValue }) => {
    try {
      const url: string = `${import.meta.env.VITE_API_URL}/sign-in`
      const requestOptions: RequestInit = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(command)
      }

      const response: Response = await fetch(url, requestOptions)
      if (!response.ok) {
        const errorText = await response.text()
        console.error(errorText)
        return rejectWithValue('인증 실패')
      }

      const jsonWebToken: JsonWebToken = await response.json()
      const profile = {
        token: jsonWebToken.token,
        ...JSON.parse(atob(jsonWebToken.token.split('.')[1]))
      }

      localStorage.setItem('profile', JSON.stringify(profile))
      window.location.href = '/dashboard'
      return profile
    } catch (error) {
      console.error('로그인 오류:', error)
      return rejectWithValue(error instanceof Error ? error.message : '인증 실패')
    }
  }
)

// 인증 상태 복원 액션
export const restoreAuth = createAsyncThunk(
  'authentication/restoreAuth',
  async (_, { dispatch }) => {
    const storedProfile = localStorage.getItem('profile')
    if (storedProfile) {
      const profile: Profile = JSON.parse(storedProfile)
      const isTokenValid = profile.exp && Date.now() / 1000 < Number(profile.exp)

      if (isTokenValid) {
        return profile
      } else {
        localStorage.removeItem('profile')
        return null
      }
    }
    return null
  }
)

// 인증 슬라이스
const authenticationSlice = createSlice({
  name: 'authentication',
  initialState,
  reducers: {
    // 로그아웃 액션
    signOut: (state) => {
      localStorage.removeItem('profile')
      state.isAuthenticated = false
      state.profile = null
      state.user = null
      window.location.href = '/sign-in'
    }
  },
  extraReducers: (builder) => {
    builder
      // 로그인 요청 처리
      .addCase(signIn.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      // 로그인 성공 처리
      .addCase(signIn.fulfilled, (state, action) => {
        state.isAuthenticated = true
        state.profile = action.payload
        state.isLoading = false
        state.error = null
        state.user = action.payload ? {
          name: action.payload.sub,
          email: action.payload.sub
        } : null
      })
      // 로그인 실패 처리
      .addCase(signIn.rejected, (state, action) => {
        state.isAuthenticated = false
        state.profile = null
        state.isLoading = false
        state.error = action.payload as string
        state.user = null
      })
      // 인증 상태 복원 처리
      .addCase(restoreAuth.fulfilled, (state, action) => {
        if (action.payload) {
          state.isAuthenticated = true
          state.profile = action.payload
          state.user = {
            name: action.payload.sub,
            email: action.payload.sub
          }
        }
      })
  }
})

// 선택자 함수들
export const selectIsAuthenticated = (state: RootState) => state.authentication.isAuthenticated
export const selectIsLoading = (state: RootState) => state.authentication.isLoading
export const selectError = (state: RootState) => state.authentication.error
export const selectUser = (state: RootState) => state.authentication.user
export const selectProfile = (state: RootState) => state.authentication.profile

// 필요한 역할을 가지고 있는지 확인하는 선택자
export const selectHasRequiredRole = (state: RootState) => {
  const profile = state.authentication.profile
  if (!state.authentication.isAuthenticated || !profile) return false

  return profile.authorities.some(
    // role => role === 'ROLE_ADMIN' || role === 'ROLE_MANAGER'
    role => role === 'ROLE_ADMIN' || role === 'ROLE_MANAGER' || role === 'ROLE_USER'
  )
}

export const { signOut } = authenticationSlice.actions

export default authenticationSlice.reducer
