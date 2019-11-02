package com.mall.service;

import com.mall.common.ServerResponse;
import com.mall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> selectQuestion(String username);
    ServerResponse<String> answerCheck(String username,String question,String answer);
    ServerResponse<String> rssetPassword(String username,String newPassword,String token);
    ServerResponse<String > rssetPasswordOnline(User user,String oldPassword,String newPassWord);
    ServerResponse<User> updateInformation(User user);
}
