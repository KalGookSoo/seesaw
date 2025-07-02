import React from 'react';
import { useAuth } from '@/features/auth/model/hooks';

const ProfilePage: React.FC = () => {
  const { user, logout } = useAuth();

  if (!user) {
    return <div>사용자 정보를 불러오는 중...</div>;
  }

  return (
    <div className="profile-page">
      <h1>프로필</h1>
      <div className="profile-info">
        <div className="avatar">
          {user.avatar ? (
            <img src={user.avatar} alt={`${user.name}의 아바타`} />
          ) : (
            <div className="avatar-placeholder">{user.name.charAt(0)}</div>
          )}
        </div>

        <div className="user-details">
          <h2>{user.name}</h2>
          <p>이메일: {user.email}</p>
          {/* 추가 사용자 정보 */}
        </div>
      </div>

      <div className="actions">
        <button onClick={() => logout()}>로그아웃</button>
      </div>
    </div>
  );
};

export default ProfilePage;
