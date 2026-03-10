import request from '@/api'
import type { ApiResponse, PageResult } from '@/types'

export interface CulturalContent {
  id: number
  title: string
  summary: string
  content: string
  coverImage: string
  category: string
  tags: string[]
  authorId: number
  authorName: string
  viewCount: number
  likeCount: number
  commentCount: number
  favoriteCount: number
  isTop: boolean
  isRecommend: boolean
  isPublished: boolean
  publishedAt: string
  createdAt: string
  updatedAt: string
}

export interface CulturalContentCreateParams {
  title: string
  summary?: string
  content: string
  coverImage?: string
  category?: string
  tags?: string[]
  isPublished?: boolean
}

/**
 * 获取文化资讯列表
 */
export function getCulturalContents(params: {
  category?: string
  keyword?: string
  current: number
  size: number
}) {
  return request<ApiResponse<PageResult<CulturalContent>>>({
    url: '/cultural-contents',
    method: 'get',
    params,
  })
}

/**
 * 获取推荐资讯
 */
export function getRecommendContents(params: {
  current: number
  size: number
}) {
  return request<ApiResponse<PageResult<CulturalContent>>>({
    url: '/cultural-contents/recommend',
    method: 'get',
    params,
  })
}

/**
 * 获取资讯详情
 */
export function getContentDetail(id: number) {
  return request<ApiResponse<CulturalContent>>({
    url: `/cultural-contents/${id}`,
    method: 'get',
  })
}

/**
 * 创建资讯
 */
export function createContent(data: CulturalContentCreateParams) {
  return request<ApiResponse<CulturalContent>>({
    url: '/cultural-contents',
    method: 'post',
    data,
  })
}

/**
 * 更新资讯
 */
export function updateContent(id: number, data: CulturalContentCreateParams) {
  return request<ApiResponse<void>>({
    url: `/cultural-contents/${id}`,
    method: 'put',
    data,
  })
}

/**
 * 删除资讯
 */
export function deleteContent(id: number) {
  return request<ApiResponse<void>>({
    url: `/cultural-contents/${id}`,
    method: 'delete',
  })
}
