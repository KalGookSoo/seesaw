import * as React from 'react'
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
import { ArrowUpDown } from 'lucide-react'
import {
  ChevronDoubleLeftIcon,
  ChevronDoubleRightIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  EllipsisHorizontalIcon,
  FunnelIcon
} from '@heroicons/react/24/outline'

import {
  type ColumnDef,
  type ColumnFiltersState,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  type SortingState,
  useReactTable,
  type VisibilityState
} from '@tanstack/react-table'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { Input } from '@/components/ui/input'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import type { User } from '@/entities/User.ts'


const columns: ColumnDef<User>[] = [
  {
    id: 'select',
    header: ({ table }) => (
      <Checkbox
        checked={table.getIsAllPageRowsSelected() || (table.getIsSomePageRowsSelected() && 'indeterminate')}
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
    enableSorting: false,
    enableHiding: false
  },
  {
    accessorKey: 'username',
    header: ({ column }) => {
      return (
        <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}>
          계정명
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => <div>{row.getValue('username')}</div>,
    enableColumnFilter: true,
    filterFn: 'includesString'
  },
  {
    accessorKey: 'email',
    header: ({ column }) => {
      return (
        <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}>
          이메일
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => {
      const emailId = row.original.email_id
      const emailDomain = row.original.email_domain
      const email = `${emailId}@${emailDomain}`
      return <div>{email}</div>
    },
    enableColumnFilter: true,
    filterFn: 'includesString'
  },
  {
    accessorKey: 'created_date',
    header: ({ column }) => {
      return (
        <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}>
          생성일자
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => {
      const date = new Date(row.getValue('created_date'))
      const formatted = new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      }).format(date)
      return <div>{formatted}</div>
    },
    enableColumnFilter: true,
    filterFn: (row, id, filterValue) => {
      const formattedDate = new Date(row.getValue(id)).toLocaleDateString('ko-KR')
      return formattedDate.toLowerCase().includes(filterValue.toLowerCase())
    }
  },
  {
    accessorKey: 'expired_date',
    header: ({ column }) => {
      return (
        <Button variant="ghost" onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}>
          만료일자
          <ArrowUpDown className="ml-2 h-4 w-4" />
        </Button>
      )
    },
    cell: ({ row }) => {
      const date = new Date(row.getValue('expired_date'))
      const formatted = new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      }).format(date)
      return <div>{formatted}</div>
    },
    enableColumnFilter: true,
    filterFn: (row, id, filterValue) => {
      const formattedDate = new Date(row.getValue(id)).toLocaleDateString('ko-KR')
      return formattedDate.toLowerCase().includes(filterValue.toLowerCase())
    }
  },
  {
    id: 'actions',
    enableHiding: false,
    cell: ({ row }) => {
      const user = row.original
      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <span className="sr-only">메뉴 열기</span>
              <EllipsisHorizontalIcon className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>작업</DropdownMenuLabel>
            <DropdownMenuItem onClick={() => navigator.clipboard.writeText(user.username)}>
              계정명 복사
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => navigator.clipboard.writeText(`${user.email_id}@${user.email_domain}`)}>
              이메일 복사
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem>회원 상세정보</DropdownMenuItem>
            <DropdownMenuItem>회원 정보 수정</DropdownMenuItem>
            <DropdownMenuItem>계정 비활성화</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      )
    }
  }
]

function DataTable({ users }: { users: User[] }) {
  const [sorting, setSorting] = React.useState<SortingState>([])
  const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([])
  const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({})
  const [rowSelection, setRowSelection] = React.useState({})

  // 이메일 계산 및 사용자 데이터 설정
  // 이메일 계산 및 사용자 데이터 설정
  const usersWithEmail = React.useMemo(() => {
    return users.map((user) => ({
      ...user,
      email: `${user.email_id}@${user.email_domain}`,
      // 날짜 형식 검색을 위한 형식화된 날짜 추가
      created_date_formatted: new Date(user.created_date).toLocaleDateString('ko-KR'),
      expired_date_formatted: new Date(user.expired_date).toLocaleDateString('ko-KR')
    }))
  }, [users])

  const table = useReactTable({
    data: usersWithEmail,
    columns,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    state: {
      sorting,
      columnFilters,
      columnVisibility,
      rowSelection
    },
    initialState: {
      pagination: {
        pageSize: 10
      }
    }
  })
  // 검색 상태 관리
  const [searchColumnId, setSearchColumnId] = React.useState<string>('username')
  const [searchValue, setSearchValue] = React.useState<string>('')

  // 컬럼 이름 한글화 함수
  const getColumnKoreanName = (columnId: string) => {
    switch (columnId) {
      case 'username':
        return '계정명'
      case 'email':
        return '이메일'
      case 'created_date':
        return '생성일자'
      case 'expired_date':
        return '만료일자'
      default:
        return columnId
    }
  }

  // 검색 실행 함수
  const handleSearch = () => {
    if (searchColumnId && searchValue) {
      table.getColumn(searchColumnId)?.setFilterValue(searchValue)
    }
  }

  // 엔터 키 검색 처리
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch()
    }
  }

  return (
    <div className="w-full">
      <div className="flex items-center py-4">
        <div className="flex items-center space-x-2 flex-1">
          {/* 컬럼 선택 드롭다운 */}
          <Select value={searchColumnId} onValueChange={setSearchColumnId}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="검색할 필드" />
            </SelectTrigger>
            <SelectContent>
              {table
                .getAllColumns()
                .filter((column) => column.id !== 'select' && column.id !== 'actions' && column.getCanFilter())
                .map((column) => (
                  <SelectItem key={column.id} value={column.id}>
                    {getColumnKoreanName(column.id)}
                  </SelectItem>
                ))}
            </SelectContent>
          </Select>

          {/* 검색 입력 필드 */}
          <div className="flex flex-1 items-center max-w-sm">
            <Input
              placeholder={`${getColumnKoreanName(searchColumnId)} 검색...`}
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              onKeyDown={handleKeyDown}
              className="w-full"
            />
            <Button variant="ghost" size="icon" onClick={handleSearch} className="ml-2 h-8 w-8">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                viewBox="0 0 16 16"
                className="h-4 w-4"
              >
                <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z" />
              </svg>
              <span className="sr-only">검색</span>
            </Button>
          </div>
        </div>

        {/* 컬럼 표시 설정 드롭다운 */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="ml-auto h-8 w-8">
              <FunnelIcon className="h-4 w-4" />
              <span className="sr-only">표시할 열</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel>표시할 열 선택</DropdownMenuLabel>
            <DropdownMenuSeparator />

            {/* 컬럼 표시 설정 */}
            {table
              .getAllColumns()
              .filter((column) => column.getCanHide())
              .map((column) => {
                return (
                  <DropdownMenuCheckboxItem
                    key={column.id}
                    checked={column.getIsVisible()}
                    onCheckedChange={(value) => column.toggleVisibility(value)}
                  >
                    {getColumnKoreanName(column.id)}
                  </DropdownMenuCheckboxItem>
                )
              })}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
                    </TableHead>
                  )
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow key={row.id} data-state={row.getIsSelected() && 'selected'}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-between space-x-2 py-4">
        <div className="text-muted-foreground flex-1 text-sm">
          {table.getFilteredSelectedRowModel().rows.length}개 / 총 {table.getFilteredRowModel().rows.length}개 선택됨
        </div>
        <div className="flex items-center space-x-6 lg:space-x-8">
          <div className="flex items-center space-x-2">
            <p className="text-sm font-medium">페이지당 행:</p>
            <select
              className="h-8 w-[70px] rounded-md border border-input bg-transparent px-3 py-1 text-sm"
              value={table.getState().pagination.pageSize}
              onChange={(e) => {
                table.setPageSize(Number(e.target.value))
              }}
            >
              {[10, 20, 30, 40, 50].map((pageSize) => (
                <option key={pageSize} value={pageSize}>
                  {pageSize}
                </option>
              ))}
            </select>
          </div>
          <div className="flex w-[100px] items-center justify-center text-sm font-medium">
            {table.getState().pagination.pageIndex + 1} / {table.getPageCount()} 페이지
          </div>
          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => table.firstPage()}
              disabled={!table.getCanPreviousPage()}
              title="처음 페이지"
            >
              <ChevronDoubleLeftIcon className="h-4 w-4" />
              <span className="sr-only">처음</span>
            </Button>
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage()}
              title="이전 페이지"
            >
              <ChevronLeftIcon className="h-4 w-4" />
              <span className="sr-only">이전</span>
            </Button>
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage()}
              title="다음 페이지"
            >
              <ChevronRightIcon className="h-4 w-4" />
              <span className="sr-only">다음</span>
            </Button>
            <Button
              variant="outline"
              size="icon"
              className="h-8 w-8"
              onClick={() => table.lastPage()}
              disabled={!table.getCanNextPage()}
              title="마지막 페이지"
            >
              <ChevronDoubleRightIcon className="h-4 w-4" />
              <span className="sr-only">마지막</span>
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default function MemberList() {
  const users = []

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
                  <Link to="/member">회원 관리</Link>
                </BreadcrumbLink>
              </BreadcrumbItem>
              <BreadcrumbSeparator />
              <BreadcrumbItem>
                <BreadcrumbPage>회원 목록</BreadcrumbPage>
              </BreadcrumbItem>
            </BreadcrumbList>
          </Breadcrumb>
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4">
          <h1 className="text-2xl font-bold">회원 목록</h1>
          <div className="bg-muted/50 min-h-[100vh] flex-1 rounded-xl md:min-h-min p-4">
            <DataTable users={users} />
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
