export type User = {
  id: string
  created_date: string
  expired_date: string
  username: string
  email: string
  email_id: string
  email_domain: string
  credentials_expired_date: string
  version: number
  created_by: string | null
  created_ip: string | null
  last_modified_by: string | null
  last_modified_date: string | null
  last_modified_ip: string | null
  contact_number: string | null
  locked_date: string | null
  name: string | null
  password: string | null
}