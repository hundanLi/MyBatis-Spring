## MyBatis与Spring整合

### 1.1 快速入门 

[参考文档](http://mybatis.org/spring/zh/index.html)

MyBatis可以用纯Java代码配置的方式与Spring整合。这里使用Druid作为数据源实现。

1. 依赖添加

        <dependency>
          <groupId>org.mybatis</groupId>
          <artifactId>mybatis-spring</artifactId>
          <version>2.0.5</version>
        </dependency>
        <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-context</artifactId>
          <version>5.2.8.RELEASE</version>
        </dependency>
        <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-jdbc</artifactId>
          <version>5.2.8.RELEASE</version>
        </dependency>
        <dependency>
          <groupId>org.springframework.batch</groupId>
          <artifactId>spring-batch-infrastructure</artifactId>
          <version>4.2.4.RELEASE</version>
        </dependency>
        <dependency>
          <groupId>org.mybatis</groupId>
          <artifactId>mybatis</artifactId>
          <version>3.5.5</version>
        </dependency>
        <dependency>
          <groupId>com.alibaba</groupId>
          <artifactId>druid</artifactId>
          <version>1.1.23</version>
        </dependency>
        <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <version>1.18.12</version>
        </dependency>
        <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>8.0.21</version>
        </dependency>

2. 配置

druid.properties

    druid.url=jdbc:mysql://localhost:3306/mybatis_learning?serverTimezone=Asia/Shanghai
    druid.username=root
    druid.password=root

MybatisConfig.java

    @Configuration
    public class MybatisConfig {
    
    	// 数据源
        @Bean
        public DataSource dataSource() throws IOException {
            InputStream inputStream = Resources.getResourceAsStream("druid.properties");
            Properties props = new Properties();
            props.load(inputStream);
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.configFromPropety(props);
            return dataSource;
        }
    
    	// 部分Mybatis配置
        @Bean
        public org.apache.ibatis.session.Configuration mybatisConfiguration() {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.addMappers("com.tcl.mapper");
            return configuration;
        }
    
    
        // SqlSessionFactory
        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setConfiguration(mybatisConfiguration());
            factoryBean.setDataSource(dataSource());
            return factoryBean.getObject();
        }
    
    	// 将mapper加入IOC容器
        @Bean
        public BlogMapper userMapper() throws Exception {
            SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory());
            return template.getMapper(BlogMapper.class);
        }
    }
    

核心逻辑：需要通过SqlSessionFactoryBean来创建SqlSessionFactory实例，MyBatis的所有配置都可以通过这个SqlSessionFactoryBean类来配置。SqlSessionTemplate就是SqlSession的实现类，可以无缝替代DefaultSqlSession，它是线程安全的，可以被所有Mapper映射器实例共享。

3. 实体类和mapper接口

    @Data
    public class Blog {
    
        private Long id;
    
        private String title;
    
        private String content;
    
        private String author;
    
        private Date createTime;
    
        private Date updateTime;
    }

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="com.tcl.mapper.BlogMapper">
    
        <select id="selectAll" resultType="com.tcl.entity.Blog">
            select * from blog;
        </select>
    </mapper>

4. 测试查询

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



### 1.2 事务管理

MyBatis-Spring 借助了 Spring 中的 DataSourceTransactionManager 来实现事务管理。

一旦配置好了 Spring 的事务管理器，你就可以在 Spring 中按你平时的方式来配置事务。并且支持 @Transactional 注解和 AOP 风格的配置。在事务处理期间，一个单独的 SqlSession 对象将会被创建和使用。当事务完成时，这个 session 会以合适的方式提交或回滚。

标准配置：

    @Bean
    public DataSourceTransactionManager transactionManager() {
      return new DataSourceTransactionManager(dataSource());
    }

注意：为事务管理器指定的 DataSource 必须和用来创建 SqlSessionFactoryBean 的是同一个数据源。



### 1.3 注入映射器

有两种方式将mapper接口的映射器实例注入IOC容器中：

- 手动注册
  方式1：
          @Bean
          public BlogMapper blogMapper1() throws Exception {
              SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory());
              return template.getMapper(BlogMapper.class);
          }
  方式二：
          @Bean
          public BlogMapper blogMapper2() throws Exception {
              MapperFactoryBean<BlogMapper> factoryBean = new MapperFactoryBean<>(BlogMapper.class);
              factoryBean.setSqlSessionFactory(sqlSessionFactory());
              return factoryBean.getObject();
          }
  
- 自动发现
  使用@MapperScan扫描并注册所有映射器 。
      @Configuration
      @MapperScan(basePackages = "com.tcl.mapper")
      public class MybatisConfig {
      }


