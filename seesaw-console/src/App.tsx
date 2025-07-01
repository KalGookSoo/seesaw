import './App'
import { createBrowserRouter, type RouteObject, RouterProvider } from 'react-router-dom'
import Dashboard from '@/pages/dashboard.tsx'
import SiteList from '@/pages/site/list.tsx'
import MemberList from '@/pages/member/list.tsx'
import MenuManagement from '@/pages/menu/index.tsx'
import CategoryManagement from '@/pages/category/index.tsx'
import ArticleManagement from '@/pages/article/index.tsx'
import ContentManagement from '@/pages/content/index.tsx'
import CodeManagement from '@/pages/code/index.tsx'
import { type JSX } from 'react'

export default function App(): JSX.Element {
  const routes: RouteObject[] = [
    { path: '/', element: <Dashboard /> },
    { path: '/site/list', element: <SiteList /> },
    { path: '/member/list', element: <MemberList /> },
    { path: '/menu', element: <MenuManagement /> },
    { path: '/category', element: <CategoryManagement /> },
    { path: '/article', element: <ArticleManagement /> },
    { path: '/content', element: <ContentManagement /> },
    { path: '/code', element: <CodeManagement /> }
  ]

  const router = createBrowserRouter(routes)

  return <RouterProvider router={router} />
}
