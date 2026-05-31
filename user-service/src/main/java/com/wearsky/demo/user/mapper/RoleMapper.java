package com.wearsky.demo.user.mapper;

import com.wearsky.demo.user.domain.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wearsky
 * @since 2026-05-24
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<String> selectCodeByUserId(@Param("userId") Long userId);
}
