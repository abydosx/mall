package com.mall.dao;

import com.mall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int userNameCheck(String username);

    int userEmailCheck(String email);

    User loginCheck(@Param("username") String username, @Param("password")String password);

    String selectQuestion(String username);

    int answerCheck(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    int setNewPassword(@Param("username") String username,@Param("newPassword")String newPassword);

    int checkEmailByUserId(@Param("email") String email,@Param("id") Integer id );

    int checkPassword(@Param("oldPassword")String oldPassword,@Param("id") Integer id);


}