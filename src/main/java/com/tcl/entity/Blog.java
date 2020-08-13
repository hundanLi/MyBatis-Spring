package com.tcl.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author li
 * @version 1.0
 * @date 2020/8/13 13:18
 */
@Data
public class Blog {

    private Long id;

    private String title;

    private String content;

    private String author;

    private Date createTime;

    private Date updateTime;
}
