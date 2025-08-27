/**
 * @deprecated 이 파일은 더 이상 사용되지 않습니다. 대신 Redux Toolkit을 사용하세요.
 * 인증 관련 기능은 @/app/store/authenticationSlice.ts에 구현되어 있습니다.
 * useAuth 훅은 @/features/auth/model/hooks/index.ts에서 제공됩니다.
 */

import React, { createContext, useContext, useReducer, useEffect } from 'react';

// 타입
export interface Profile {
  token: string;
  authorities: string[];
  exp: string;
  iat: string;
  sub: string;
}

export interface SignInCommand {
  username: string;
  password: string;
}

export interface JsonWebToken {
  token: string;
}

interface AuthState {
  isAuthenticated: boolean;
  profile: Profile | null;
  isLoading: boolean;
  error: string | null;
}

type AuthAction =
  | { type: 'SIGN_IN_REQUEST' }
  | { type: 'SIGN_IN_SUCCESS'; payload: Profile }
  | { type: 'SIGN_IN_FAILURE'; payload: string }
  | { type: 'SIGN_OUT' }
  | { type: 'RESTORE_AUTH' };

interface AuthContextType extends AuthState {
  signIn: (command: SignInCommand) => Promise<void>;
  signOut: () => void;
  logout: () => void;
  hasRequiredRole: () => boolean;
  user?: {
    name: string;
    email: string;
    avatar?: string;
  };
}

// Initial state
const initialState: AuthState = {
  isAuthenticated: false,
  profile: null,
  isLoading: false,
  error: null,
};

// Reducer
const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'SIGN_IN_REQUEST':
      return {
        ...state,
        isLoading: true,
        error: null,
      };
    case 'SIGN_IN_SUCCESS':
      return {
        ...state,
        isAuthenticated: true,
        profile: action.payload,
        isLoading: false,
        error: null,
      };
    case 'SIGN_IN_FAILURE':
      return {
        ...state,
        isAuthenticated: false,
        profile: null,
        isLoading: false,
        error: action.payload,
      };
    case 'SIGN_OUT':
      localStorage.removeItem('profile');
      return {
        ...initialState,
      };
    case 'RESTORE_AUTH':
      const storedProfile = localStorage.getItem('profile');
      if (storedProfile) {
        const profile: Profile = JSON.parse(storedProfile);
        const isTokenValid = profile.exp && Date.now() / 1000 < Number(profile.exp);

        if (isTokenValid) {
          return {
            ...state,
            isAuthenticated: true,
            profile,
          };
        } else {
          localStorage.removeItem('profile');
        }
      }
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Check if user is authenticated on mount
  useEffect(() => {
    dispatch({ type: 'RESTORE_AUTH' });
  }, []);

  // Sign in function
  const signIn = async (command: SignInCommand): Promise<void> => {
    dispatch({ type: 'SIGN_IN_REQUEST' });
    try {
      const url: string = `${import.meta.env.VITE_API_URL}/sign-in`;
      const requestOptions: RequestInit = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(command),
      };

      const response: Response = await fetch(url, requestOptions);
      if (!response.ok) {
        const errorText = await response.text();
        console.error(errorText);
        dispatch({ type: 'SIGN_IN_FAILURE', payload: 'Authentication failed' });
        return;
      }

      const jsonWebToken: JsonWebToken = await response.json();
      const profile = {
        token: jsonWebToken.token,
        ...JSON.parse(atob(jsonWebToken.token.split('.')[1])),
      };

      localStorage.setItem('profile', JSON.stringify(profile));
      dispatch({ type: 'SIGN_IN_SUCCESS', payload: profile });
      window.location.href = '/dashboard';
    } catch (error) {
      console.error('Sign in error:', error);
      dispatch({ 
        type: 'SIGN_IN_FAILURE', 
        payload: error instanceof Error ? error.message : 'Authentication failed' 
      });
    }
  };

  const signOut = (): void => {
    dispatch({ type: 'SIGN_OUT' });
    window.location.href = '/sign-in';
  };

  const hasRequiredRole = (): boolean => {
    if (!state.isAuthenticated || !state.profile) return false;

    return state.profile.authorities.some(
      role => role === 'ROLE_ADMIN' || role === 'ROLE_MANAGER'
    );
  };

  // Alias for signOut for backward compatibility
  const logout = (): void => {
    signOut();
  };

  // Create user object from profile if authenticated
  const user = state.isAuthenticated && state.profile ? {
    name: state.profile.sub,
    email: state.profile.sub,
    // You can add more user properties here if needed
  } : undefined;

  return (
    <AuthContext.Provider
      value={{
        ...state,
        signIn,
        signOut,
        logout,
        hasRequiredRole,
        user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use the auth context
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
