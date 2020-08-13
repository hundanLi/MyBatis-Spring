package com.tcl.mapper;

import com.tcl.entity.Blog;

import java.util.List;

/**
 * @author li
 * @version 1.0
 * @date 2020/8/13 13:18
 */
public interface BlogMapper {

    List<Blog> selectAll();
}
