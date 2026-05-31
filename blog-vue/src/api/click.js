import request from './request'

export function getBlogLikeCount(blogId) {
  return request.get(`/click-service/click/like/blog/${blogId}`)
}

export function getBlogDislikeCount(blogId) {
  return request.get(`/click-service/click/dislike/blog/${blogId}`)
}

export function getReplyLikeCounts(replyIds) {
  return request.get('/click-service/click/like/reply', {
    params: { replyIds }
  })
}

export function getReplyDislikeCounts(replyIds) {
  return request.get('/click-service/click/dislike/reply', {
    params: { replyIds }
  })
}
