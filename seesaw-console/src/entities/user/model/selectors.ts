import { RootState } from '@/app/store';

export const selectUser = (state: RootState) => state.user.user;
export const selectIsAuthenticated = (state: RootState) => state.user.isAuthenticated;
export const selectAuthLoading = (state: RootState) => state.user.isLoading;
export const selectAuthError = (state: RootState) => state.user.error;
export const selectAccessToken = (state: RootState) => state.user.accessToken;
export const selectRefreshToken = (state: RootState) => state.user.refreshToken;
