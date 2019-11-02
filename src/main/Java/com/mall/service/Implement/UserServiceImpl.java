package com.mall.service.Implement;

import com.mall.common.Const;
import com.mall.common.ServerResponse;
import com.mall.common.TokenCache;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    //登陆
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.userNameCheck(username);
        if (resultCount == 0){
            return  ServerResponse.CreateFailMessage("没有找到该用户");
        }
        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.loginCheck(username,md5password);
        if (user ==null){
            return ServerResponse.CreateFailMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.CreateSuccess("登陆成功",user);
    }


    //注册
    public ServerResponse<String> register(User user){
        ServerResponse response = this.checkValid(user.getUsername(),Const.USER_NAME);
        if (!response.isSuccess()){
            return response;
        }
        response = this.checkValid(user.getEmail(),Const.USER_EMAIL);
        if (!response.isSuccess()){
            return response;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount ==0) {
            return ServerResponse.CreateFailMessage("创建用户失败");
        }
        return ServerResponse.CreateSuccess("注册成功");
    }
    //校验
    public ServerResponse<String> checkValid(String str,String type){
        int resultCount;
        if (StringUtils.isNotBlank(type)){
            if (type.equals(Const.USER_NAME)){
                resultCount = userMapper.userNameCheck(str);
                if (resultCount>0){
                    return ServerResponse.CreateFailMessage("用户名已被注册");
                }
            }
            if (type.equals(Const.USER_EMAIL)){
                resultCount = userMapper.userEmailCheck(str);
                if (resultCount>0){
                    return ServerResponse.CreateFailMessage("邮箱已被注册");
                }
            }
        }else {
            return ServerResponse.CreateFailMessage("参数错误");
        }
        return ServerResponse.CreateSuccess("校验成功");
    }

    //问题
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse response = this.checkValid(username, Const.USER_NAME);
        if (response.isSuccess()) {
            return ServerResponse.CreateFailMessage("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.CreateSuccess(question);
        }
        return ServerResponse.CreateFailMessage("问题为空");
    }

    //验证答案
    public ServerResponse<String > answerCheck(String username,String question,String answer){
        int resultCount = userMapper.answerCheck(username,question,answer);
        if (resultCount>0){
            String token = UUID.randomUUID().toString();
            TokenCache.setValue(TokenCache.TOKEN_PREFIX+username,token);
            return ServerResponse.CreateSuccess(token);
        }

        return ServerResponse.CreateFailMessage("答案错误");
    }

    //忘记密码时重置密码
    public ServerResponse<String > rssetPassword (String username,String newPassword,String token){
        if (StringUtils.isBlank(token)){
            return ServerResponse.CreateFailMessage("转递的token失效");
        }
        String serverToken = TokenCache.getValue(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(serverToken)){
            return ServerResponse.CreateFailMessage("token或许已经过期");
        }else {
            if (StringUtils.equals(serverToken,token)){
                String md5NewPassword = MD5Util.MD5EncodeUtf8(newPassword);
                userMapper.setNewPassword(username,md5NewPassword);
                return ServerResponse.CreateSuccess("修改成功");
            }
        }
        return ServerResponse.CreateFailMessage("token验证失败");
    }

    //登陆时重置密码
    public ServerResponse<String > rssetPasswordOnline (User user,String oldPassword,String newPassWord){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount == 0){
            return ServerResponse.CreateFailMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassWord));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.CreateSuccess("密码更新成功");
        }
        return ServerResponse.CreateFailMessage("密码更新失败");
    }

    //得到用户信息

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.CreateFailMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.CreateSuccess(user);

    }
    //更新用户信息
    public ServerResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.CreateFailMessage("email已存在");
        }

        User updateUser = new User();
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.CreateSuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.CreateFailMessage("更新个人信息失败");
    }

}
