import request from './request'

export function getBlogList(params) {
  return request.get('/blog-service/blog/query', { params })
}

export function getBlogDetail(id) {
  return request.get(`/blog-service/blog/${id}`)
}

export function createBlog(data) {
  return request.post('/blog-service/blog', data)
}

export function updateBlog(data) {
  return request.put('/blog-service/blog', data)
}

export function deleteBlog(id) {
  return request.delete(`/blog-service/blog/${id}`)
}

export function likeBlog(id) {
  return request.post(`/blog-service/blog/${id}/like`)
}

export function dislikeBlog(id) {
  return request.post(`/blog-service/blog/${id}/dislike`)
}
