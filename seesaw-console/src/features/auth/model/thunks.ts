import { createAsyncThunk } from '@reduxjs/toolkit';
import { apiClient } from '@/shared/api/axios-instance';
import { API_ENDPOINTS } from '@/shared/config/environment';
import {
  authRequest,
  authSuccess,
  authFailure,
  logout,
  updateTokens,
} from '@/entities/user/model/slice';
import {
  SignInCredentials,
  AuthResponse,
} from '@/entities/user/model/types';

// 로그인 Thunk
export const signIn = createAsyncThunk<void, SignInCredentials>(
  'auth/signIn',
  async (credentials, { dispatch }) => {
    try {
      dispatch(authRequest());

      const response = await apiClient.post<AuthResponse>(
        API_ENDPOINTS.SIGN_IN,
        credentials
      );

      const { user, accessToken, refreshToken } = response.data;

      // 토큰을 로컬 스토리지에 저장
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      // 리덕스 스토어에 상태 업데이트
      dispatch(
        authSuccess({
          user,
          accessToken,
          refreshToken,
        })
      );
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || '로그인에 실패했습니다.';
      dispatch(authFailure(errorMessage));
      throw error;
    }
  }
);

// 토큰 갱신 Thunk
export const refreshAccessToken = createAsyncThunk<void, string>(
  'auth/refreshToken',
  async (refreshToken, { dispatch }) => {
    try {
      const response = await apiClient.post<{ accessToken: string; refreshToken: string }>(
        API_ENDPOINTS.REFRESH_TOKEN,
        { refreshToken }
      );

      const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data;

      // 새 토큰 저장
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);

      dispatch(
        updateTokens({
          accessToken: newAccessToken,
          refreshToken: newRefreshToken,
        })
      );
    } catch (error) {
      // 토큰 갱신 실패 시 로그아웃
      dispatch(signOut());
      throw error;
    }
  }
);

// 로그아웃 Thunk
export const signOut = createAsyncThunk<void>(
  'auth/signOut',
  async (_, { dispatch, getState }) => {
    try {
      // 백엔드에 로그아웃 요청 (토큰 무효화)
      await apiClient.post(API_ENDPOINTS.SIGN_OUT);
    } catch (error) {
      console.error('로그아웃 요청 실패:', error);
    } finally {
      // 로컬 스토리지에서 토큰 제거
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');

      // Redux 상태 초기화
      dispatch(logout());
    }
  }
);

// 세션 복원 Thunk (앱 초기화 시 사용)
export const restoreSession = createAsyncThunk<void>(
  'auth/restoreSession',
  async (_, { dispatch }) => {
    try {
      const accessToken = localStorage.getItem('accessToken');
      const refreshToken = localStorage.getItem('refreshToken');

      if (!accessToken || !refreshToken) {
        return;
      }

      // 토큰으로 사용자 정보 가져오기
      const response = await apiClient.get('/user/me');

      if (response.data) {
        dispatch(
          authSuccess({
            user: response.data,
            accessToken,
            refreshToken,
          })
        );
      }
    } catch (error) {
      // 세션 복원 실패 시 로컬 스토리지 초기화
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }
);
