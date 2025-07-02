import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { signIn } from '@/features/auth/model/thunks';
import { selectAuthLoading, selectAuthError } from '@/entities/user/model/selectors';
import { AppDispatch } from '@/app/store';

const LoginForm: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const isLoading = useSelector(selectAuthLoading);
  const error = useSelector(selectAuthError);

  const [credentials, setCredentials] = useState({
    email: '',
    password: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await dispatch(signIn(credentials)).unwrap();
      // 로그인 성공 후 처리 (예: 리디렉션)
    } catch (error) {
      // 에러는 리듀서에서 처리됨
    }
  };

  return (
    <div className="login-form">
      <h2>로그인</h2>
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="email">이메일</label>
          <input
            type="email"
            id="email"
            name="email"
            value={credentials.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="password">비밀번호</label>
          <input
            type="password"
            id="password"
            name="password"
            value={credentials.password}
            onChange={handleChange}
            required
          />
        </div>
        <button type="submit" disabled={isLoading}>
          {isLoading ? '로그인 중...' : '로그인'}
        </button>
      </form>
    </div>
  );
};

export default LoginForm;
