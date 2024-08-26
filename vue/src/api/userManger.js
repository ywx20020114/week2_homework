import request from '@/utils/request'

export default {
  getUserByCondition(searchModel) {
    return request({
      url: '/sys/user/getUserByCondition',
      method: 'get',
      params: {
        pageNo: searchModel.pageNo,
        pageSize: searchModel.pageSize,
        username: searchModel.username,
        phone: searchModel.phone
      }
    })
  },
  addUser(user) {
    return request({
      url: '/sys/user/addUser',
      method: 'post',
      data: user
    })
  },
  updateUser(user) {
    return request({
      url: '/sys/user/updateUser',
      method: 'put',
      data: user
    })
  },
  saveUser(user) {
    if (user.id == null && user.id === undefined) {
      return this.addUser(user)
    }
    return this.updateUser(user)
  },
  getUserById(id) {
    return request({

      url: `/sys/user/getUserById/${id}`,
      method: 'get'
    })
  },
  deleteUserById(id) {
    return request({

      url: `/sys/user/deleteUserById/${id}`,
      method: 'delete'
    })
  }
}
