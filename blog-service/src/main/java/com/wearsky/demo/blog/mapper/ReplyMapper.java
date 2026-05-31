package com.wearsky.demo.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wearsky.demo.blog.domain.entity.Reply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {
}