package com.tcl;


import com.tcl.config.MybatisConfig;
import com.tcl.mapper.BlogMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest {

    ApplicationContext applicationContext;

    @Before
    public void before() {
        applicationContext = new AnnotationConfigApplicationContext(MybatisConfig.class);
    }

    @Test
    public void getStarted() {
        BlogMapper blogMapper = applicationContext.getBean(BlogMapper.class);
        System.out.println(blogMapper);
        blogMapper.selectAll().forEach(System.out::println);
    }
}
