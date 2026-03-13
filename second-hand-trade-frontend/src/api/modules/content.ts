import http from '..'

/**
 * 分页查询文化资讯
 */
export const getCulturalContents = (current: number, size: number, category?: string) => {
  return http.get('/cultural-contents', {
    params: { current, size, category }
  })
}

/**
 * 获取推荐的文化资讯
 */
export const getRecommendContents = (current: number, size: number) => {
  return http.get('/cultural-contents/recommend', {
    params: { current, size }
  })
}

/**
 * 获取文化资讯详情
 */
export const getCulturalContentDetail = (id: number) => {
  return http.get(`/cultural-contents/${id}`)
}
