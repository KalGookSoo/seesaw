import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/features/auth/model/hooks';

interface ProtectedRouteProps {
  redirectPath?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  redirectPath = '/login',
}) => {
  const { isAuthenticated, isLoading } = useAuth();

  // 로딩 중일 때 표시할 내용
  if (isLoading) {
    return <div>Loading...</div>;
  }

  // 인증되지 않은 경우 리디렉션
  if (!isAuthenticated) {
    return <Navigate to={redirectPath} replace />;
  }

  // 인증된 경우 자식 라우트 렌더링
  return <Outlet />;
};

export default ProtectedRoute;
