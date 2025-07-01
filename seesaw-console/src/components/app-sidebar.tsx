import * as React from 'react'
import { ChevronRight } from 'lucide-react'
import { Link } from 'react-router-dom'

import { SearchForm } from '@/components/search-form'
import { VersionSwitcher } from '@/components/version-switcher'
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible'
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail
} from '@/components/ui/sidebar'

// version을 '전체'로 선택한 경우 모든 사이트를 일괄적으로 관리할 수 있도록 한다.
// version을 '전체'로 선택하지 않은 경우 해당 site에 필터링된 정보만 관리할 수 있도록 한다.
const data = {
  versions: ['전체', '@사이트명1@', '@사이트명2@', '@사이트명3@'],
  navMain: [
    {
      title: '사이트 관리',
      url: '/site',
      items: [
        {
          title: '사이트 목록',
          url: '/site/list'
        }
      ]
    },
    {
      title: '회원 관리',
      url: '/member',
      items: [
        {
          title: '회원 목록',
          url: '/member/list'
        }
      ]
    },
    {
      title: '컨텐츠 관리',
      url: '/content',
      items: [
        {
          title: '메뉴 관리',
          url: '/menu'
        },
        {
          title: '카테고리 관리',
          url: '/category'
        },
        {
          title: '게시글 관리',
          url: '/article'
        },
        {
          title: '컨텐츠 관리',
          url: '/content'
        }
      ]
    },
    {
      title: '환경변수 관리',
      url: '/code',
      items: [
        {
          title: '코드 관리',
          url: '/code'
        }
      ]
    }
  ]
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar {...props}>
      <SidebarHeader>
        <VersionSwitcher versions={data.versions} defaultVersion={data.versions[0]} />
        <SearchForm />
      </SidebarHeader>
      <SidebarContent className="gap-0">
        {/* We create a collapsible SidebarGroup for each parent. */}
        {data.navMain.map((item) => (
          <Collapsible key={item.title} title={item.title} defaultOpen className="group/collapsible">
            <SidebarGroup>
              <SidebarGroupLabel
                asChild
                className="group/label text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground text-sm"
              >
                <CollapsibleTrigger>
                  {item.title}{' '}
                  <ChevronRight className="ml-auto transition-transform group-data-[state=open]/collapsible:rotate-90" />
                </CollapsibleTrigger>
              </SidebarGroupLabel>
              <CollapsibleContent>
                <SidebarGroupContent>
                  <SidebarMenu>
                    {item.items.map((item) => (
                      <SidebarMenuItem key={item.title}>
                        <SidebarMenuButton asChild isActive={item.isActive}>
                          <Link to={item.url}>{item.title}</Link>
                        </SidebarMenuButton>
                      </SidebarMenuItem>
                    ))}
                  </SidebarMenu>
                </SidebarGroupContent>
              </CollapsibleContent>
            </SidebarGroup>
          </Collapsible>
        ))}
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  )
}
