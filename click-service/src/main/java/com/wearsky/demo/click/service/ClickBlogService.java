package com.wearsky.demo.click.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wearsky.demo.click.domain.entity.ClickBlog;

public interface ClickBlogService extends IService<ClickBlog> {

    void clickLike(ClickBlog clickBlog);

    void clickDislike(ClickBlog clickBlog);

    Long getClickCount4LikeBlog(Long blogId);

    Long getClickCount4DislikeBlog(Long blogId);
}
