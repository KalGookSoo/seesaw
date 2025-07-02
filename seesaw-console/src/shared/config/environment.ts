// 환경 변수 및 API 엔드포인트 설정
export const IS_PRODUCTION = import.meta.env.PROD || false;

export const API_BASE_URL = IS_PRODUCTION
  ? 'https://www.seesaw.me.kr/api'
  : 'http://localhost:9000/api';

export const API_ENDPOINTS = {
  SIGN_IN: `${API_BASE_URL}/sign-in`,
  REFRESH_TOKEN: `${API_BASE_URL}/refresh-token`,
  SIGN_OUT: `${API_BASE_URL}/sign-out`,
};
