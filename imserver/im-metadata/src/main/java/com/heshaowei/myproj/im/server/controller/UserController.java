package com.heshaowei.myproj.im.server.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.heshaowei.myproj.bean.response.Result;
import com.heshaowei.myproj.im.server.dto.LoginUserDTO;
import com.heshaowei.myproj.im.server.model.User;
import com.heshaowei.myproj.im.server.repository.UserRepository;
import com.heshaowei.myproj.im.server.utils.GsonUtil;
import com.heshaowei.myproj.im.server.utils.JwtUtils;
import com.heshaowei.myproj.im.server.utils.LoginUserUtil;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public Result<LoginUserDTO> login(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        User matched = this.userRepository.findOne(Example.of(user)).orElse(null);
        if(null == matched){
            return Result.error("用户不存在！");
        }

        return Result.success(createLoginUser(matched));
    }

    private LoginUserDTO createLoginUser(User user){
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setId(user.getId().toString());
        loginUser.setCreateTime(user.getId().getDate());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setPhone(user.getPhone());
        Map<String, Object> map = Maps.newHashMap();
        map.put("loginUser", loginUser);
        String token = JwtUtils.create(loginUser.getUsername(), map);
        loginUser.setToken(token);
        return loginUser;
    }

    @PostMapping("/register")
    public Result<LoginUserDTO> register(@RequestBody User user){
        this.userRepository.save(user);
        return Result.success(createLoginUser(user));
    }

    @GetMapping("/list")
    public Result<List> list(HttpServletRequest request, String query){
        PageRequest pr = PageRequest.of(0, 10);
        List<User> users = this.userRepository.queryByKeyWords(LoginUserUtil.getUsername(request), query, pr);

        return Result.success(users);
    }

    @PutMapping("/updateById")
    public Result updateById(@RequestBody User user){
        if(null != user.getId()) {
            User update = this.userRepository.findById(user.getId()).orElse(null);
            if(null != update) {
                if(null != user.getNickname()) {
                    update.setNickname(user.getNickname());
                }
                if(null != user.getAvatar()) {
                    update.setAvatar(user.getAvatar());
                }
                if(null != user.getPhone()) {
                    update.setPhone(user.getPhone());
                }
                if(null != user.getPassword()) {
                    update.setPassword(user.getPassword());
                }
                this.userRepository.save(update);
            }
            return Result.success();
        }
        return Result.error("没有用户ID");
    }
}
