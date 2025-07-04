import { type JSX } from 'react'

export function Fallback({ children }: any): JSX.Element {
  return <div>{children}</div>
}
