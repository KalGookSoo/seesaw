import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import {
  selectUser,
  selectIsAuthenticated,
  selectAuthLoading,
} from '@/entities/user/model/selectors';
import { signOut } from './thunks';
import { AppDispatch } from '@/app/store';

// 인증 상태 및 사용자 정보 훅
export const useAuth = () => {
  const user = useSelector(selectUser);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const isLoading = useSelector(selectAuthLoading);
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await dispatch(signOut());
    navigate('/login');
  };

  return {
    user,
    isAuthenticated,
    isLoading,
    logout: handleLogout,
  };
};
