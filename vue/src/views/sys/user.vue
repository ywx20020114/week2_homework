<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">

          <el-input v-model="searchModel.username" placeholder="用户名" clearable />
          <el-input v-model="searchModel.phone" placeholder="电话" clearable />
          <el-button type="primary" round icon="el-icon-search" @click="getUserByCondition">查询</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button type="primary" icon="el-icon-plus" circle @click="openUserWin(null)" />
        </el-col>
      </el-row>
    </el-card>
    <!-- 搜索结果 -->
    <el-card>
      <el-table :data="userList" stripe style="width: 100%">

        <el-table-column type="index" label="#" width="80">
          <template slot-scope="scope">

            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="id" label="用户ID" width="180" />
        <el-table-column prop="username" label="用户名" width="180" />

        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status == 1">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" size="mini" circle @click="openUserWin(scope.row.id)" />
            <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="deleteUserById(scope.row)" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页功能 -->
    <el-pagination
      :current-page="searchModel.pageNo"
      :page-sizes="[5, 10, 15, 20]"
      :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 用户表单 -->

    <el-dialog :title="title" :visible.sync="dialogFormVisible" @close="clearForm">
      <el-form ref="userFormRef" :model="userForm" :rules="rules">
        <el-form-item label="用户名" :label-width="formLabelWidth" prop="username">
          <el-input v-model="userForm.username" autocomplete="off" clearable />
        </el-form-item>
        <el-form-item v-if="userForm.id == null || userForm.id == undefined" label="密码" :label-width="formLabelWidth" prop="password">
          <el-input v-model="userForm.password" type="password" autocomplete="off" clearable />
        </el-form-item>
        <el-form-item label="手机号码" :label-width="formLabelWidth" prop="phone">
          <el-input v-model="userForm.phone" autocomplete="off" clearable />
        </el-form-item>
        <el-form-item label="用户状态" :label-width="formLabelWidth">
          <el-switch v-model="userForm.status" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="用户角色" :label-width="formLabelWidth">
          <el-checkbox-group v-model="userForm.roleIdList" :min="0" :max="2">
            <el-checkbox v-for="role in roleList" :key="role.roleId" :label="role.roleId">{{ role.roleDesc }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="邮箱" :label-width="formLabelWidth" prop="email">
          <el-input v-model="userForm.email" autocomplete="off" clearable />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveUser">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>

import userApi from '@/api/userManger'
import roleApi from '@/api/roleManger'
export default {
  data() {
    var checkPhone = (rule, value, callback) => {
      var reg = /^1(3\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\d|9[0-35-9])\d{8}$/
      if (!reg.test(value)) {
        return callback(new Error('手机号码格式错误'))
      }
      callback()
    }
    var checkEmail = (rule, value, callback) => {
      var reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+$/
      if (!reg.test(value)) {
        return callback(new Error('邮件格式错误'))
      }
      callback()
    }
    return {
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 5
      },
      userList: [],
      title: '',
      dialogFormVisible: false,
      userForm: {
        roleIdList: []
      },
      formLabelWidth: '120px',
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { validator: checkPhone, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur' }
        ]
      },
      roleList: []
    }
  },

  created() {
    this.getUserByCondition()
    this.getAllRole()
  },
  methods: {
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getUserByCondition()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getUserByCondition()
    },
    getUserByCondition() {
      userApi.getUserByCondition(this.searchModel).then(response => {
        console.log(response)
        this.userList = response.data.rows
        this.total = response.data.total
      })
    },

    openUserWin(id) {
      if (id == null) {
        this.title = '新增用户'
      } else {
        this.title = '修改用户'

        userApi.getUserById(id).then(response => {
          this.userForm = response.data
        })
      }

      this.dialogFormVisible = true
    },
    clearForm() {
      this.userForm = { roleIdList: [] }
      this.$refs.userFormRef.clearValidate()
    },
    saveUser() {
      this.$refs.userFormRef.validate(valid => {
        if (valid) {
          userApi.saveUser(this.userForm).then(response => {
            this.$notify({
              title: '成功',
              message: response.message,
              type: 'success'
            })

            this.clearForm()
            this.dialogFormVisible = false

            this.getUserByCondition()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    deleteUserById(user) {
      this.$confirm(`您确认删除用户 ${user.username} ?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          userApi.deleteUserById(user.id).then(response => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.getUserByCondition()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    getAllRole() {
      roleApi.getAllRole().then(response => {
        this.roleList = response.data
        console.log(this.roleList)
      })
    }
  }
}
</script>
<style>
#search .el-input {
    width: 200px;
    margin-right: 10px;
}
.el-dialog .el-input {
    width: 80%;
}
.el-dialog .el-checkbox-group {
    width: 80%;
}
</style>
