package com.mall.service.Implement;

import com.mall.common.ServerResponse;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int userCount = userMapper.userNameCheck(username);
        if (userCount == 0){
            return  ServerResponse.CreateFailMessage("没有找到该用户");
        }
        User user = userMapper.loginCheck(username,password);
        if (user ==null){
            return ServerResponse.CreateFailMessage("密码错误");
        }
        return ServerResponse.CreateSuccess("登陆成功",user);
    }
}
