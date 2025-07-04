import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectIsAuthenticated, selectHasRequiredRole } from '@/app/store/authenticationSlice';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireRole?: boolean;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requireRole = true 
}) => {
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const hasRequiredRole = useSelector(selectHasRequiredRole);

  // 인증되지 않은 경우 로그인 페이지로 리디렉션
  if (!isAuthenticated) {
    return <Navigate to="/sign-in" replace />;
  }

  // 역할 확인이 필요하고 사용자가 필요한 역할을 가지고 있지 않은 경우 대시보드로 리디렉션
  if (requireRole && !hasRequiredRole) {
    return <Navigate to="/dashboard" replace />;
  }

  // 사용자가 인증되었고 필요한 역할을 가지고 있음 (또는 역할 확인이 필요하지 않음)
  return <>{children}</>;
};

export const AuthRedirect: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isAuthenticated = useSelector(selectIsAuthenticated);

  // 이미 인증된 경우 대시보드로 리디렉션
  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  // 인증되지 않은 경우 자식 컴포넌트 표시 (로그인 페이지)
  return <>{children}</>;
};
