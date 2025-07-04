import { useSelector, useDispatch } from 'react-redux';
import { 
  selectIsAuthenticated, 
  selectIsLoading, 
  selectError, 
  selectUser, 
  selectHasRequiredRole,
  signIn,
  signOut
} from '@/app/store/authenticationSlice';
import type { SignInCommand } from '@/app/store/authenticationSlice';

// Redux를 사용하는 useAuth 훅
export const useAuth = () => {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const isLoading = useSelector(selectIsLoading);
  const error = useSelector(selectError);
  const user = useSelector(selectUser);
  const hasRequiredRole = useSelector(selectHasRequiredRole);

  return {
    isAuthenticated,
    isLoading,
    error,
    user,
    signIn: (command: SignInCommand) => dispatch(signIn(command)),
    signOut: () => dispatch(signOut()),
    logout: () => dispatch(signOut()),
    hasRequiredRole: () => hasRequiredRole
  };
};
