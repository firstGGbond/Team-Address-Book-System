<template>
  <div>
    <!--搜索栏-->
    <el-card id="search">
      <el-row>
        <el-col :span="20">
          <el-input v-model="searchModel.username" placeholder="username"></el-input>
          <el-input v-model="searchModel.phone" placeholder="phone"></el-input>
          <el-button @click="getUserList" type="primary" round icon="el-icon-search">查询</el-button>
          <el-button @click="getUserFavoriteList" type="primary" round icon="el-icon-search">收藏</el-button>
        </el-col>
        <el-col :span="4" align="right">
          <el-button @click="openEditUI(null)" type="primary" icon="el-icon-plus" circle></el-button>
        </el-col>
      </el-row>
    </el-card>

    <!--结果列表-->
    <el-card>
      <el-table :data="userList" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="80">
        </el-table-column>
        <el-table-column prop="id" label="userID" width="180">
        </el-table-column>
        <el-table-column prop="username" label="Name" width="180">
        </el-table-column>
        <el-table-column prop="phone" label="Phone" width="180">
        </el-table-column>
        <el-table-column prop="status" label="Favorite" width="180">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status == 1">favorite</el-tag>
            <el-tag v-if="scope.row.status == 0" type="danger">normal</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="Email">
        </el-table-column>
        <el-table-column label="Control" width="180">
          <template slot-scope="scope">
            <el-button @click="openEditUI(scope.row.id)" type="primary" icon="el-icon-edit" circle></el-button>
            <el-button @click="deleteUser(scope.row)" type="danger" icon="el-icon-delete" circle></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!--分页组件-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
      :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
      layout="total, sizes, prev, pager, next, jumper" :total="total">
    </el-pagination>

    <!--用户信息对话框-->
    <el-dialog @close="clearForm" :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="userForm" ref="userFormRef" :rules="rules">
        <el-form-item label="用户名" prop="username" :label-width="formLabelWidth">
          <el-input v-model="userForm.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="userForm.id == null || userForm.id == undefined" label="密码" prop="password"
          :label-width="formLabelWidth">
          <el-input type="password" v-model="userForm.password" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" :label-width="formLabelWidth">
          <el-input v-model="userForm.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="收藏" :label-width="formLabelWidth">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0">
          </el-switch>
        </el-form-item>
        <el-form-item label="电子邮件" prop="email" :label-width="formLabelWidth">
          <el-input v-model="userForm.email" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import userApi from '@/api/userManage'
import { status } from 'nprogress';
export default {
  data() {
    var checkEmail = (rule, value, callback) => {
      var reg = /^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\.[a-zA-Z]{2,}$/;
      if (!reg.test(value)) {
        return callback(new Error('邮箱格式错误'));
      }
      callback();
    };
    return {
      formLabelWidth: '130px',
      userForm: {},
      dialogFormVisible: false,
      title: "",
      total: 0,
      searchModel: {
        pageNo: 1,
        pageSize: 10
      },
      userList: [],
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 18, message: '长度在 6 到 18 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入电子邮件', trigger: 'blur' },
          { validator: checkEmail, trigger: 'blur' }
        ],
      }
    }
  },
  methods: {
    deleteUser(user) {
      this.$confirm(`Are you sure you want to delete the user ${user.username} ?`, '提示', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        userApi.deleteUserById(user.id).then(response => {
          this.$message({
            type: 'success',
            message: response.message
          });
          this.getUserList();
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Undelete'
        });
      });
    },
    saveUser() {
      // 触发表单验证
      this.$refs.userFormRef.validate((valid) => {
        if (valid) {
          // 提交请求给后台
          userApi.saveUser(this.userForm).then(response => {
            // 成功提示
            this.$message({
              message: response.message,
              type: 'success'
            })
            // 关闭对话框
            this.dialogFormVisible = false;
            // 刷新表格
            this.getUserList();
          });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    clearForm() {
      this.userForm = {};
      this.$refs.userFormRef.resetFields();
    },
    openEditUI(id) {
      if (id == null) {
        this.title = '新增用户';
      } else {
        this.title = '修改用户';
        userApi.getUserById(id).then(response => {
          this.userForm = response.data;
        })
      }
      this.dialogFormVisible = true;
    },
    handleSizeChange(pageSize) {
      this.searchModel.pageSize = pageSize;
      this.getUserList();
    },
    handleCurrentChange(pageNo) {
      this.searchModel.pageNo = pageNo;
      this.getUserList();
    },
    getUserList() {
      userApi.getUserList(this.searchModel).then(response => {
        this.userList = response.data.rows;
        this.total = response.data.total;
      });
    },
    getUserFavoriteList() {
      userApi.getUserFavoriteList(this.searchModel).then(response => {
        this.userList = response.data.rows;
        this.total = response.data.total;
      });
    }
  },
  created() {
    this.getUserList();
  }
}
</script>

<style>
#search .el-input {
  width: 200px;
  margin-right: 10px;
}

.el-dialog .el-input {
  width: 85%;
}
</style>