import { AppSidebar } from '@/components/app-sidebar'
import { Link } from 'react-router-dom'
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator
} from '@/components/ui/breadcrumb'
import { Separator } from '@/components/ui/separator'
import { SidebarInset, SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar'

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'

import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'

import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious
} from '@/components/ui/pagination'

import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger
} from '@/components/ui/dialog'

import { EllipsisHorizontalIcon } from '@heroicons/react/24/solid'
import { Label } from '@/components/ui/label.tsx'
import { Input } from '@/components/ui/input.tsx'

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

import { useState } from 'react'

export default function SiteList() {
  const sites = []
  const attachments = []
  const codes = []
  const distributions = codes.filter((code) => code.name === 'SITE_DISTRIBUTION')

  const [distributionFirstDepthId, setDistributionFirstDepthId] = useState(null)
  const [distributionSecondDepthId, setDistributionSecondDepthId] = useState(null)
  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    debugger
  }

  const handleChange = (currentDepth: number) => (value: string) => {
    if (currentDepth === 1) {
      setDistributionFirstDepthId(value)
    } else if (currentDepth === 2) {
      setDistributionSecondDepthId(value)
    }
  }

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="bg-background sticky top-0 flex h-16 shrink-0 items-center gap-2 border-b px-4">
          <SidebarTrigger className="-ml-1" />
          <Separator orientation="vertical" className="mr-2 h-4" />
          <Breadcrumb>
            <BreadcrumbList>
              <BreadcrumbItem>
                <BreadcrumbLink asChild>
                  <Link to="/">Dashboard</Link>
                </BreadcrumbLink>
              </BreadcrumbItem>
              <BreadcrumbSeparator />
              <BreadcrumbItem>
                <BreadcrumbLink asChild>
                  <Link to="/site">사이트 관리</Link>
                </BreadcrumbLink>
              </BreadcrumbItem>
              <BreadcrumbSeparator />
              <BreadcrumbItem>
                <BreadcrumbPage>사이트 목록</BreadcrumbPage>
              </BreadcrumbItem>
            </BreadcrumbList>
          </Breadcrumb>
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4">
          <h1 className="text-2xl font-bold">사이트 목록</h1>
          <div className="flex flex-1 flex-col gap-4 p-4">
            {sites.map((site) => {
              const profileImage = attachments.find((attachment) => attachment.reference_id)
              return (
                <div
                  key={site.id}
                  className="aspect-video h-24 w-full rounded-lg bg-white border shadow-sm p-4 flex flex-col hover:shadow-md transition-shadow"
                >
                  <div className="flex items-center space-x-4">
                    {/* Profile Image */}
                    <div className="relative h-12 w-12 rounded-full overflow-hidden bg-muted flex items-center justify-center">
                      {profileImage ? (
                        <Avatar>
                          <AvatarImage
                            src={`http://localhost:8080/api/attachments/${profileImage.id}`}
                            alt={profileImage.name}
                          />
                          <AvatarFallback>CN</AvatarFallback>
                        </Avatar>
                      ) : (
                        <div className="text-2xl font-bold text-muted-foreground">{site.name.charAt(0)}</div>
                      )}
                    </div>

                    {/* Site Info */}
                    <div className="flex-1 min-w-0">
                      <h3 className="text-base font-semibold truncate">{site.name}</h3>
                      <p className="text-sm text-muted-foreground truncate">{site.domain_name}</p>
                    </div>

                    {/* More Options Button */}
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <form onSubmit={handleSubmit}>
                            <Dialog>
                              <DialogTrigger asChild>
                                <Button variant="ghost" size="icon" className="h-8 w-8">
                                  <EllipsisHorizontalIcon />
                                  <span className="sr-only">More options</span>
                                </Button>
                              </DialogTrigger>
                              <DialogContent className="sm:max-w-md">
                                <DialogHeader>
                                  <DialogTitle>홈페이지 필수 정보 / 검색 정보</DialogTitle>
                                  <DialogDescription>
                                    <span className="inline">홈페이지 필수 정보</span> <span>* 필수 입력사항</span>
                                  </DialogDescription>
                                </DialogHeader>

                                <div className="grid w-full max-w-sm items-center gap-3">
                                  <Label htmlFor="name">홈페이지명</Label>
                                  <Input
                                    type="text"
                                    id="name"
                                    defaultValue={site.name}
                                    placeholder="홈페이지명"
                                    required
                                    className="w-full"
                                  />
                                  <Label htmlFor="domainName">인터넷주소</Label>
                                  <Input
                                    type="text"
                                    id="domainName"
                                    defaultValue={site.domain_name}
                                    placeholder="인터넷주소"
                                    required
                                    className="w-full"
                                  />
                                  <Label htmlFor="profileImage">대표이미지</Label>
                                  <Input id="profileImage" type="file" required className="w-full" />
                                  <Label htmlFor="email">홈페이지설명</Label>
                                  <Input
                                    type="text"
                                    id="email"
                                    defaultValue={site.description}
                                    placeholder="홈페이지설명"
                                    required
                                    className="w-full"
                                  />
                                  <Label htmlFor="option-one">분류</Label>
                                  <div className="flex flex-col md:flex-row md:gap-2 space-y-2 md:space-y-0">
                                    <Select onValueChange={handleChange(1)}>
                                      <SelectTrigger className="w-full">
                                        <SelectValue />
                                      </SelectTrigger>
                                      <SelectContent>
                                        {distributions &&
                                          distributions.length &&
                                          distributions
                                            .filter((distribution) => distribution.parent_id === null)
                                            .map((distribution) => (
                                              <SelectItem key={distribution.id} value={distribution.id}>
                                                {distribution.description}
                                              </SelectItem>
                                            ))}
                                      </SelectContent>
                                    </Select>
                                    <Select onValueChange={handleChange(2)}>
                                      <SelectTrigger className="w-full">
                                        <SelectValue />
                                      </SelectTrigger>
                                      <SelectContent>
                                        {distributionFirstDepthId &&
                                          distributions
                                            .filter(
                                              (distribution) => distribution.parent_id === distributionFirstDepthId
                                            )
                                            .map((distribution) => (
                                              <SelectItem key={distribution.id} value={distribution.id}>
                                                {distribution.description}
                                              </SelectItem>
                                            ))}
                                      </SelectContent>
                                    </Select>
                                    <Select>
                                      <SelectTrigger className="w-full">
                                        <SelectValue />
                                      </SelectTrigger>
                                      <SelectContent>
                                        {distributionSecondDepthId &&
                                          distributions
                                            .filter(
                                              (distribution) => distribution.parent_id === distributionSecondDepthId
                                            )
                                            .map((distribution) => (
                                              <SelectItem key={distribution.id} value={distribution.id}>
                                                {distribution.description}
                                              </SelectItem>
                                            ))}
                                      </SelectContent>
                                    </Select>
                                  </div>
                                </div>

                                <DialogFooter className="sm:justify-start">
                                  <DialogClose asChild>
                                    <Button type="button" variant="secondary">
                                      닫기
                                    </Button>
                                  </DialogClose>
                                  <Button type="submit" variant="default">
                                    저장
                                  </Button>
                                </DialogFooter>
                              </DialogContent>
                            </Dialog>
                          </form>
                        </TooltipTrigger>
                        <TooltipContent>
                          <p>More options</p>
                        </TooltipContent>
                      </Tooltip>
                    </TooltipProvider>
                  </div>
                </div>
              )
            })}
          </div>
          <div>
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious href="#" />
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink href="#">1</PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink href="#" isActive>
                    2
                  </PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink href="#">3</PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationEllipsis />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext href="#" />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
