import request from './request'

export function register(data) {
  return request.post('/user-service/user', data)
}

export function login(data) {
  return request.post('/user-service/user/login', data)
}

export function getUserInfo() {
  return request.get('/user-service/user/me')
}

export function getUserById(id) {
  return request.get(`/user-service/user/${id}`)
}

export function updateUser(data) {
  return request.put('/user-service/user', data)
}
