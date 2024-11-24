package com.nick.sys.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nick.common.vo.Result;
import com.nick.sys.entity.User;
import com.nick.sys.service.IUserRoleService;
import com.nick.sys.service.IUserService;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nick
 * @since 2024-10-25
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin
public class UserController {
    @Autowired
    private IUserService userService;


    @GetMapping("/all")
    public Result<List<User>>  getAllUser(){
        List<User> userList  = userService.list();
        return Result.success(userList, "查询成功");
    }

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User user){
        Map<String,Object> data = userService.login(user);
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20002,"用户名或密码错误");
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@Param("token") String token){
        //跟俊token获取用户信息，redis
        Map<String, Object> data = userService.getUserInfo(token);
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20002,"用户登录信息无效，请重新登陆");
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("X-Token") String token){
        userService.logout(token);
        return Result.success("注销成功");
    }

    @GetMapping("/list")
    public Result<Map<String,Object>> getUserList(@RequestParam(value = "username", required = false) String username,
                                              @RequestParam(value = "phone", required = false) String phone,
                                              @RequestParam(value = "pageNo") Long pageNo,
                                              @RequestParam(value = "pageSize") Long pageSize){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(username),User::getUsername,username);
        wrapper.eq(StringUtils.hasLength(phone),User::getPhone,phone);
//        wrapper.orderByDesc(User::getId);


        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNo, pageSize);
        userService.page(page,wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);

    }

    @GetMapping("/favorite")
    public Result<Map<String,Object>> getUserFavoriteList(@RequestParam(value = "username",required = false) String username,
                                                          @RequestParam(value = "phone",required = false) String phone,
                                                          @RequestParam(value = "pageNo") Long pageNo,
                                                          @RequestParam(value = "pageSize") Long pageSize,
                                                          @RequestParam(value = "status", required = false) Integer status){
        LambdaQueryWrapper<User> wrapper =  new LambdaQueryWrapper<>();

        wrapper.eq(StringUtils.hasLength(username),User::getUsername,username);
        wrapper.eq(StringUtils.hasLength(phone),User::getPhone,phone);
        wrapper.eq(User::getStatus,status);
        wrapper.orderByDesc(User::getId);

        Page<User> page =  new Page<>(pageNo,pageSize);
        userService.page(page,wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }

    @PostMapping
    public Result<?> addUser(@RequestBody User user){
        userService.save(user);
        return Result.success("新增用户成功");
    }

    @PutMapping
    public Result<?> updateUser(@RequestBody User user){
        user.setPassword(null);
        userService.updateById(user);
        return Result.success("修改用户成功");
    }


    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Integer id){
        User user = userService.getById(id);
        return Result.success(user);
    }

    @DeleteMapping("/{id}")
    public Result<User> deleteUserById(@PathVariable("id") Integer id){
        userService.removeById(id);
        return Result.success("删除用户成功");
    }

    @GetMapping("/export")
    public void exportUser(@RequestParam(required = false) String username,
                           @RequestParam(required = false) String name,
                           HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        List<User> userList = userService.list();

        writer.write(userList,true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=user.xlsx");
        ServletOutputStream outputStream =  response.getOutputStream();
        writer.flush(outputStream,true);
        writer.close();
        outputStream.flush();
        outputStream.close();
    }

    @PostMapping("/import")
    public Result<?> importUser(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<User> userList = reader.readAll(User.class);
        //写入数据到数据库
        try {
            userService.saveBatch(userList);
        } catch (Exception e) {
            return Result.fail("数据导入失败");
        } finally {
        }
        return Result.success();
    }
}
