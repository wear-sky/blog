import request from './request'

export function getReplyTree(blogId) {
  return request.get(`/blog-service/reply/tree/${blogId}`)
}

export function createReply(data) {
  return request.post('/blog-service/reply', data)
}

export function deleteReply(id) {
  return request.delete(`/blog-service/reply/${id}`)
}

export function likeReply(id) {
  return request.post(`/blog-service/reply/${id}/like`)
}

export function dislikeReply(id) {
  return request.post(`/blog-service/reply/${id}/dislike`)
}

export function undoLikeReply(id) {
  return request.post(`/blog-service/reply/${id}/undoLike`)
}

export function undoDislikeReply(id) {
  return request.post(`/blog-service/reply/${id}/undoDislike`)
}
