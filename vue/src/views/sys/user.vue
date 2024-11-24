<template>
    <div>
        <!-- 搜索栏 -->
        <el-card id="search">
            <el-row>
                <el-col :span="20">
                    <el-input v-model="searchModel.username" placeholder="Name" clearable></el-input>
                    <el-input v-model="searchModel.phone" placeholder="telephone" clearable></el-input>
                    <el-button @click="getUserList" type="primary" round icon="el-icon-search">Search</el-button>
                    <el-button @click="getUserFavoriteList" type="primary" round icon="el-icon-star-on">收藏</el-button>
                    <el-upload action="http://localhost:9999/user/import" :headers="getUploadHeaders" ref="upload"
                        :on-success="handleImport" style="display: inline-block; margin: 10px;" :show-file-list="false">
                        <el-button type="primary" icon="el-icon-upload">Import</el-button>
                    </el-upload>
                    <el-button @click="exportUser" type="primary" icon="el-icon-share">Export</el-button>
                </el-col>
                <el-col :span="4" align="right">

                    <el-button @click="openEditUI(null)" type="primary" icon="el-icon-plus" circle></el-button>
                </el-col>
            </el-row>
        </el-card>

        <!-- 结果列表 -->
        <el-card>
            <el-table :data="userList" stripe style="width: 100%">
                <el-table-column label="#" width="80">
                    <template slot-scope="scope">
                        <!-- (pageNo-1) * pageSize + index + 1 -->
                        {{
                            (searchModel.pageNo - 1) * searchModel.pageSize + scope.$index + 1
                        }}
                    </template>
                </el-table-column>
                <el-table-column prop="username" label="name" width="180">
                </el-table-column>
                <el-table-column prop="phone" label="phone" width="180">
                </el-table-column>
                <el-table-column prop="status" label="Favorite" width="180">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status == 1">favorite</el-tag>
                        <el-tag v-else type="danger">normal</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="email" label="email"> </el-table-column>
                <el-table-column label="operate" width="300">
                    <template slot-scope="scope">
                        <el-dropdown split-button type="medium">
                            more phone
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item>11111111111</el-dropdown-item>
                                <el-dropdown-item>11111411312</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                        <el-button @click="openEditUI(scope.row.id)" type="primary" icon="el-icon-edit" size="mini"
                            circle></el-button>
                        <el-button @click="deleteUser(scope.row)" type="danger" icon="el-icon-delete" size="mini"
                            circle></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 分页组件 -->
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="searchModel.pageNo" :page-sizes="[5, 10, 20, 50]" :page-size="searchModel.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>

        <!-- 用户信息编辑对话框 -->
        <el-dialog @close="clearForm" :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="useForm" ref="userFormRef" :rules="rules">
                <el-form-item label="User name" prop="username" :label-width="formLabelWidth">
                    <el-input v-model="useForm.username" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="User phone" prop="phone" :label-width="formLabelWidth">
                    <el-input v-model="useForm.phone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="User status" :label-width="formLabelWidth">
                    <el-switch v-model="useForm.status" :active-value="1" :inactive-value="0">
                    </el-switch>
                </el-form-item>
                <el-form-item label="User email" prop="email" :label-width="formLabelWidth">
                    <el-input v-model="useForm.email" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">concel</el-button>
                <el-button type="primary" @click="saverUser">certain</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>

import userApi from '@/api/userManage'
export default {
    data() {
        var checkEmail = (rule, value, callback) => {
            var reg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
            if (!reg.test(value)) {
                return callback(new Error('The email address format is incorrect'));
            }
            callback();
        };
        return {
            formLabelWidth: '130px',
            useForm: {},
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
                    { required: true, message: 'Please enter a username', trigger: 'blur' },
                    { min: 3, max: 50, message: 'Lengths range from 3 to 50 characters', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: 'Please enter a phonenumber', trigger: 'blur' },
                    { min: 6, max: 20, message: 'Lengths range from 6 to 20 characters', trigger: 'blur' }
                ],
                email: [
                    { required: true, message: 'Please enter a email', trigger: 'blur' },
                    { validator: checkEmail, trigger: 'blur' }
                ],
            }
        }
    },
    methods: {
        handleImport(res, file, fileList) {
            if (res.code == '200') {
                this.$message.succeess("操作成功")
            } else {
                this.$message.error(res.msg)
            }
        },
        exportUser() {//批量导出
            const token = this.user?.token;
            window.open(`http://localhost:9999/user/export?token=${token || ''}`);
        },
        getUploadHeaders() {
            return {
                'token': this.user.token || ''
            };
        },
        deleteUser(user) {
            this.$confirm(`Do you confirm the deletion of the user ${user.username} ?`, '提示', {
                confirmButtonText: 'certain',
                cancelButtonText: 'concel',
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
                    message: '已取消删除'
                });
            });
        },
        saverUser() {
            //触发表单验证
            this.$refs.userFormRef.validate((valid) => {
                if (valid) {
                    //提交请求后台
                    userApi.saveUser(this.useForm).then(response => {
                        //成功提示
                        this.$message({
                            message: response.message,
                            type: 'success'
                        });
                        //关闭对话框
                        this.dialogFormVisible = false;
                        //刷新表格
                        this.getUserList();
                    })
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });

        },
        clearForm() {
            this.useForm = {};
            this.$refs.userFormRef.clearValidate();
        },
        openEditUI(id) {
            if (id == null) {
                this.title = "New User";
            }
            else {
                this.title = "Update User";
                //根据id查询用户数据
                userApi.getUserById(id).then(response => {
                    this.useForm = response.data;
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
            userApi.getUserList(this.searchModel).then((response) => {
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
    },
};

</script>

<style>
#search .el-input {
    width: 300px;
    margin-right: 10px;
}

.el-dialog .el-input {
    width: 85%;
}
</style>