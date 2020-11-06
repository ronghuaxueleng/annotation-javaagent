import request from '@/utils/request'

export function getAllUrls() {
  return request({
    url: '/codebuider/getAllUrls',
    method: 'get'
  })
}

export function save(json) {
  return request({
    url: '/codebuider/save',
    method: 'post',
    data: json
  })
}
