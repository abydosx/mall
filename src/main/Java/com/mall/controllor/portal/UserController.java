package com.mall.controllor.portal;


import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login (String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> logout (HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.CreateSuccess();
    }

    @RequestMapping(value = "register.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register (User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){

        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user!=null){
            return ServerResponse.CreateSuccess(user);
        }
        return ServerResponse.CreateFailCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要登陆");
    }

    @RequestMapping(value = "getQuestion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "answerCheck.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> answerCheck(String username,String question,String answer){

        return iUserService.answerCheck(username,question,answer);
    }

    @RequestMapping(value = "rssetPassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> rssetPassword(String username,String newPassword,String token){
        return iUserService.rssetPassword(username,newPassword,token);
    }

    @RequestMapping(value = "rssetPasswordOnline.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> rssetPasswordOnline(HttpSession session,String oldPassword,String newPassword){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.CreateFailMessage("用户未登录");
        }
        return iUserService.rssetPasswordOnline(user,oldPassword,newPassword);
    }

    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.CreateFailMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }





}