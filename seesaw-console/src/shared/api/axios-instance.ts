import axios from 'axios';
import { API_BASE_URL } from '@/shared/config/environment';

// 기본 axios 인스턴스 생성
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 설정
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 응답 인터셉터 설정
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // 토큰 만료 에러 (401) 처리 및 토큰 갱신 로직
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // 스토어에서 직접 접근하지 않고 로컬스토리지의 리프레시 토큰 사용
        const refreshToken = localStorage.getItem('refreshToken');

        if (!refreshToken) {
          // 리프레시 토큰이 없으면 로그아웃 처리
          window.dispatchEvent(new CustomEvent('auth:logout'));
          return Promise.reject(error);
        }

        // 토큰 갱신 요청
        const response = await axios.post(`${API_BASE_URL}/refresh-token`, {
          refreshToken,
        });

        // 새 토큰 저장
        const { accessToken, refreshToken: newRefreshToken } = response.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        // 원래 요청 재시도
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        // 토큰 갱신 실패 시 로그아웃 처리
        window.dispatchEvent(new CustomEvent('auth:logout'));
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);
