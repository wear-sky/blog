package com.wearsky.demo.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wearsky.demo.user.domain.dto.LoginDTO;
import com.wearsky.demo.user.domain.entity.User;
import com.wearsky.demo.user.domain.query.UserQuery;
import com.wearsky.demo.user.domain.vo.UserPageVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wearsky
 * @since 2026-05-24
 */
public interface IUserService extends IService<User> {

    Boolean register(User user);

    String login(LoginDTO loginDTO);

    UserPageVO queryUser(UserQuery query);

    Boolean deleteUserById(Long id);

    List<User> getbyids(List<Long> ids);
}
