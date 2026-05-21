export interface R<T = any> {
  code: number
  msg: string
  data: T
}

export interface PageResult<T = any> {
  total: number
  pageNum: number
  pageSize: number
  list: T[]
}

export interface TableResult<T = any> {
  total: number
  records: T[]
  list?: T[]
  pageNum?: number
}