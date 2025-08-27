import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import LoginForm from '@/features/auth/ui/LoginForm';
import { useAuth } from '@/features/auth/model/hooks';

const LoginPage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // 이미 로그인한 경우 대시보드로 리디렉션
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, navigate]);

  return (
    <div className="login-page">
      <div className="login-container">
        <LoginForm />
        <div className="additional-options">
          <a href="/forgot-password">비밀번호를 잊으셨나요?</a>
          <a href="/register">계정이 없으신가요? 회원가입</a>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
