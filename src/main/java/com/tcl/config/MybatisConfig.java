package com.tcl.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.tcl.mapper.BlogMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author li
 * @version 1.0
 * @date 2020/8/13 13:03
 */
@Configuration
@MapperScan(basePackages = "com.tcl.mapper")
public class MybatisConfig {


    /** 数据源
     * @return DataSource
     */
    @Bean
    public DataSource dataSource() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("druid.properties");
        Properties props = new Properties();
        props.load(inputStream);
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.configFromPropety(props);
        return dataSource;
    }


    /**
     * @return Mybatis常用配置
     */
    @Bean
    public org.apache.ibatis.session.Configuration mybatisConfiguration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMappers("com.tcl.mapper");
        return configuration;
    }


    /**
     * @return 会话工厂
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setConfiguration(mybatisConfiguration());
        factoryBean.setDataSource(dataSource());
        return factoryBean.getObject();
    }


    /**
     * @return 映射接口实例
     */
//    @Bean
    public BlogMapper blogMapper1() throws Exception {
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory());
        return template.getMapper(BlogMapper.class);
    }


//    @Bean
    public BlogMapper blogMapper2() throws Exception {
        MapperFactoryBean<BlogMapper> factoryBean = new MapperFactoryBean<>(BlogMapper.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory());
        return factoryBean.getObject();
    }


    /**
     * @return Spring管理器事务
     */
    @Bean
    public DataSourceTransactionManager transactionManager() throws IOException {
        return new DataSourceTransactionManager(dataSource());
    }

}
