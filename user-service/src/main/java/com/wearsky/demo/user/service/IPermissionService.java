package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.domain.query.PermissionQuery;
import com.wearsky.demo.user.domain.vo.PermissionPageVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IPermissionService extends IService<Permission> {

    PermissionPageVO queryPermission(PermissionQuery permissionQuery);
}
