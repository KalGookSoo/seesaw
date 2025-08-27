import './App'
import { createBrowserRouter, type RouteObject, RouterProvider } from 'react-router-dom'
import { lazy, Suspense, useEffect, type JSX } from 'react'
import { useDispatch } from 'react-redux'
import { Fallback } from '@/pages/Fallback'
import { ProtectedRoute, AuthRedirect } from '@/components/ProtectedRoute'
import { restoreAuth } from '@/app/store/authenticationSlice'

// Lazy load all page components
const Dashboard = lazy(() => import('@/pages/dashboard.tsx'))
const SiteList = lazy(() => import('@/pages/site/list.tsx'))
const MemberList = lazy(() => import('@/pages/member/list.tsx'))
const MenuManagement = lazy(() => import('@/pages/menu/index.tsx'))
const CategoryManagement = lazy(() => import('@/pages/category/index.tsx'))
const ArticleManagement = lazy(() => import('@/pages/article/index.tsx'))
const ContentManagement = lazy(() => import('@/pages/content/index.tsx'))
const CodeManagement = lazy(() => import('@/pages/code/index.tsx'))
const SignIn = lazy(() => import('@/pages/SignIn.tsx').then((module) => ({ default: module.SignIn })))

export default function App(): JSX.Element {
  const dispatch = useDispatch()
  const fallback: JSX.Element = <Fallback />

  // 컴포넌트 마운트 시 인증 상태 복원
  useEffect(() => {
    dispatch(restoreAuth())
  }, [dispatch])

  const routes: RouteObject[] = [
    {
      path: '/',
      element: (
        <Suspense fallback={fallback}>
          <AuthRedirect>
            <SignIn />
          </AuthRedirect>
        </Suspense>
      )
    },
    {
      path: '/sign-in',
      element: (
        <Suspense fallback={fallback}>
          <AuthRedirect>
            <SignIn />
          </AuthRedirect>
        </Suspense>
      )
    },
    {
      path: '/dashboard',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute requireRole={false}>
            <Dashboard />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/site/list',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <SiteList />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/member/list',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <MemberList />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/menu',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <MenuManagement />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/category',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <CategoryManagement />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/article',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <ArticleManagement />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/content',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <ContentManagement />
          </ProtectedRoute>
        </Suspense>
      )
    },
    {
      path: '/code',
      element: (
        <Suspense fallback={fallback}>
          <ProtectedRoute>
            <CodeManagement />
          </ProtectedRoute>
        </Suspense>
      )
    }
  ]

  const router = createBrowserRouter(routes)

  return <RouterProvider router={router} />
}
