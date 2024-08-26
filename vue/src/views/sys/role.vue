<template>
  <div>
    <!-- 搜索栏 -->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.roleName" placeholder="角色名称" clearable />
          <el-button type="primary" round icon="el-icon-search" @click="getRoleByCondition">查询</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button type="primary" icon="el-icon-plus" circle @click="openRoleWin(null)" />
        </el-col>
      </el-row>
    </el-card>
    <!-- 搜索结果 -->
    <el-card>
      <el-table :data="roleList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="80">
          <template slot-scope="scope">
            {{ (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="roleId" label="角色ID" />
        <el-table-column prop="roleName" label="角色名称" />

        <el-table-column prop="roleDesc" label="角色描述" />

        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" size="mini" circle @click="openRoleWin(scope.row.roleId)" />
            <el-button type="danger" icon="el-icon-delete" size="mini" circle @click="deleteRoleById(scope.row)" />
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

    <!-- 角色表单 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible" @close="clearForm">
      <el-form ref="roleFormRef" :model="roleForm" :rules="rules">
        <el-form-item label="角色名称" :label-width="formLabelWidth" prop="roleName">
          <el-input v-model="roleForm.roleName" autocomplete="off" clearable />
        </el-form-item>
        <el-form-item label="角色描述" :label-width="formLabelWidth" prop="roleDesc">
          <el-input v-model="roleForm.roleDesc" autocomplete="off" clearable />
        </el-form-item>
        <el-form-item label="权限设置" :label-width="formLabelWidth" prop="menuIdList">
          <el-tree ref="menuRef" :data="menuList" :props="menuProps" show-checkbox default-expand-all node-key="menuId" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveRole">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>

import roleApi from '@/api/roleManger'
import menuApi from '@/api/menuManger'
export default {
  data() {
    return {
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 5
      },
      roleList: [],
      title: '',
      dialogFormVisible: false,
      roleForm: {},
      formLabelWidth: '120px',
      rules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        roleDesc: [
          { required: true, message: '请输入角色描述', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ]
      },
      menuProps: {
        children: 'children',
        label: 'title'
      },
      menuList: []
    }
  },

  created() {
    this.getRoleByCondition()
    this.getAllMenu()
  },
  methods: {
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize
      this.getRoleByCondition()
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo
      this.getRoleByCondition()
    },
    getRoleByCondition() {
      roleApi.getRoleByCondition(this.searchModel).then(response => {
        console.log(response)
        this.roleList = response.data.rows
        this.total = response.data.total
      })
    },

    openRoleWin(id) {
      if (id == null) {
        this.title = '新增角色'
      } else {
        this.title = '修改角色'

        roleApi.getRoleById(id).then(response => {
          this.roleForm = response.data
          this.$refs.menuRef.setCheckedKeys(response.data.menuIdList)
        })
      }

      this.dialogFormVisible = true
    },
    clearForm() {
      this.roleForm = {}
      this.$refs.roleFormRef.clearValidate()

      this.$refs.menuRef.setCheckedKeys([])
    },
    saveRole() {
      this.$refs.roleFormRef.validate(valid => {
        if (valid) {
          const checkedKeys = this.$refs.menuRef.getCheckedKeys()
          const halfCheckedKeys = this.$refs.menuRef.getHalfCheckedKeys()
          this.roleForm.menuIdList = checkedKeys.concat(halfCheckedKeys)

          roleApi.saveRole(this.roleForm).then(response => {
            this.$notify({
              title: '成功',
              message: response.message,
              type: 'success'
            })

            this.clearForm()
            this.dialogFormVisible = false

            this.getRoleByCondition()
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    deleteRoleById(role) {
      this.$confirm(`您确认删除角色 ${role.roleName} ?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          roleApi.deleteRoleById(role.roleId).then(response => {
            this.$message({
              type: 'success',
              message: response.message
            })
            this.getRoleByCondition()
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    getAllMenu() {
      menuApi.getAllMenu().then(response => {
        this.menuList = response.data
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
.el-dialog .el-tree {
    width: 80%;
}
</style>
