package com.wearsky.demo.click.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wearsky.demo.click.domain.entity.ClickReply;
import com.wearsky.demo.click.domain.entity.ClickReplyCount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClickReplyMapper extends BaseMapper<ClickReply> {

    List<ClickReplyCount> getClickCount4LikeReplies(@Param("replyIds") List<Long> replyIds);

    List<ClickReplyCount> getClickCount4DislikeReplies(@Param("replyIds") List<Long> replyIds);
}
