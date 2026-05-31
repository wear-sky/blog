package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.domain.query.RoleQuery;
import com.wearsky.demo.user.domain.vo.RolePageVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IRoleService extends IService<Role> {

    RolePageVO queryRole(RoleQuery roleQuery);
}
