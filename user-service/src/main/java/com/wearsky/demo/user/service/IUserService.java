package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.dto.LoginDTO;
import com.wearsky.demo.user.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wearsky.demo.user.domain.query.UserQuery;
import com.wearsky.demo.user.domain.vo.UserPageVO;

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

}
