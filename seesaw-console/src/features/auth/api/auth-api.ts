import { apiClient } from '@/shared/api/axios-instance';
import { API_ENDPOINTS } from '@/shared/config/environment';
import { SignInCredentials, AuthResponse } from '@/entities/user/model/types';

// 로그인 API 호출
export const loginUser = async (credentials: SignInCredentials): Promise<AuthResponse> => {
  const response = await apiClient.post<AuthResponse>(API_ENDPOINTS.SIGN_IN, credentials);
  return response.data;
};

// 토큰 갱신 API 호출
export const refreshToken = async (refreshToken: string) => {
  const response = await apiClient.post(API_ENDPOINTS.REFRESH_TOKEN, { refreshToken });
  return response.data;
};

// 로그아웃 API 호출
export const logoutUser = async () => {
  return await apiClient.post(API_ENDPOINTS.SIGN_OUT);
};

// 현재 사용자 정보 가져오기
export const getCurrentUser = async () => {
  const response = await apiClient.get('/user/me');
  return response.data;
};
