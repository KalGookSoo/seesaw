// 환경 변수 및 API 엔드포인트 설정
export const IS_PRODUCTION = import.meta.env.PROD || false

export const API_BASE_URL = IS_PRODUCTION ? 'https://www.seesaw.me.kr/api' : 'http://localhost:9000/api'
