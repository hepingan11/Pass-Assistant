# Pass Assistant微服务版

单体架构见master分支

**技术架构**：

- JDK17

- Springcloud alibaba 2023  
- Springboot 3.3.4 
- Nacos2.2.1
- Mybatis-plus
- Springcloud Gateway
- SpringAi
- OpenFegin
- Sa-token
- Redis

## 运行教程：

### 1.下载项目：

```bash
git clone https://gitee.com/hepingan11/gptjava/tree/cloud/
```

推荐使用Idea打开项目

修改项目nacos地址：ctrl+shift+r搜索127.0.0.1:8848，将其换成你的nacos地址；

### 2.修改nacos配置

添加以下配置

|      | Data Id                     | Group         |
| :--- | :-------------------------- | :------------ |
|      | pa-system-service-prod.yml  | DEFAULT_GROUP |
|      | pa-gateway-service-prod.yml | DEFAULT_GROUP |
|      | pa-ai-service-prod.yml      | DEFAULT_GROUP |
|      | pa-common-prod.yml          | DEFAULT_GROUP |

主要修改各个配置的mysql数据库和redis连接配置，不知道该填啥的默认留空就行,因为许多配置都在前端可以配置

**pa-gateway-service-prod.yml**网关：

```yml
server:
  port: 8625 # 网关端口
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/super_bot?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
    username: root
    password: 123456
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    # redis配置
    redis:
      database: 2
      host: 127.0.0.1
      port: 6379
      password: ''
  application:
    name: pa-gateway-service 
  cloud:
    nacos:
      server-addr: 124.222.16.248:8848 # nacos地址
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: 
              - "http://localhost:8080"  
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 360000
        # 解决options请求被拦截的问题
        add-to-simple-url-handler-mapping: true
      discovery:
        locator:
          enabled: true
      routes:
        - id: pa-system-service
          uri: lb://pa-system-service
          predicates:
            - Path=/admin/**,/auth/**,/user/**
        - id: pa-ai-service      
          uri: lb://pa-ai-service
          predicates:
            - Path=/chat/**,/draw/**
        - id: pa-tools-service
          uri: lb://pa-tools-service
          predicates:
            - Path=/link/**,/code/**,/photo/**
sa-token:
  token-name: token
  active-timeout: -1
  is-concurrent: true
  is-share: false
  is-log: false
  # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
  token-style: uuid
```



**pa-system-service-prod.yml**：

```yml
config:
  incentiveFrequency: 100
  signInFrequency: 10
  videoFrequency: 10
  openAiUrl:
  openKey:
  deepseekKey:
  doubaoKey:
  claudeKey:
  glmKey:
  gptTextImageFrequency:
  gptFrequency:
  gptPlusFrequency:

  
server:
  port: 8081
# 超级管理员账号密码
root:
  # 邮箱
  email: root
  # 密码
  password: '1234'

ali-oss:
  endpoint: 'oss-cn-hangzhou.aliyuncs.com'
  accessKey: ''
  secretKey: ''
  bucketName: 'img-'
  domain: 'https://img-.oss-hangzhou.aliyuncs.com'
  

 
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
    cache-enabled: false
    jdbc-type-for-null: 'null'

spring:
  servlet:
    multipart:
      max-file-size: 2048MB
      max-request-size: 2048MB
  mail:
    host: smtp.qq.com
    username: 1641436566@qq.com
    password: ''
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
    port: 587
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/super_bot?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
    username: root
    password: 123456
  data:
    redis:
      database: 2
      host: 127.0.0.1
      port: 6379
      password: ''
sa-token:
  token-name: token
  active-timeout: -1
  is-concurrent: true
  is-share: false
  is-log: false
  # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
  token-style: uuid

hupijiao:
  appid: ''
  appsecret: ''
  webUrl: 'https://hepingan.top'
```

**pa-ai-service-prod.yml:**

```yml
server:
  port: 8082

config:
  openAiUrl:
  openKey:
  deepseekKey:
  doubaoKey:
  claudeKey:
  glmKey:
  gptTextImageFrequency:
  gptFrequency:
  gptPlusFrequency:

control:
  proxyIp:
  proxyPort:
  sensitiveWords:
  enableSensitive:
  enableGptPlus:
  enableProxy:

ali-oss:
  endpoint: 'oss-cn-hangzhou.aliyuncs.com'
  accessKey: ''
  secretKey: ''
  bucketName: 'img-'
  domain: 'https://img-.oss-hangzhou.aliyuncs.com'
  

 
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
    cache-enabled: false
    jdbc-type-for-null: 'null'

spring:
  servlet:
    multipart:
      max-file-size: 2048MB
      max-request-size: 2048MB
  ai:
    openai:
      base-url: https://api.deepseek.com
      api-key: sk-
      chat:
        options:
          model: deepseek-chat
    zhipuai:
      api-key: ''
    chat:
      memory:
        repository:
          jdbc:
            platform: mysql
            initialize-schema: never
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/super_bot?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
    username: root
    password: 123456
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    # redis配置
    redis:
      database: 2
      host: 127.0.0.1
      port: 6379
      password: ''

sa-token:
  token-name: token
  active-timeout: -1
  is-concurrent: true
  is-share: false
  is-log: false
  # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
  token-style: uuid
```

**pa-common-prod.yml**

```yml
# 阿里OSS
ali-oss:
  endpoint: 'oss-cn-hangzhou.aliyuncs.com'
  accessKey: ''
  secretKey: ''
  bucketName: 'img-'
  domain: 'https://img-.oss-hangzhou.aliyuncs.com'
  
spring:
  servlet:
    multipart:
      max-file-size: 2048MB
      max-request-size: 2048MB
 
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
    cache-enabled: false
    jdbc-type-for-null: 'null'

sa-token:
  token-name: token
  active-timeout: -1
  is-concurrent: true
  is-share: false
  is-log: false
  # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
  token-style: uuid
```

**pa-tools-service.yml**

```yml
server:
    port: 8083
#虎皮椒支付配置
hupijiao:
  appid: ''
  appsecret: ''
  webUrl: 'https://hepingan.top'

ali-oss:
  endpoint: 'oss-cn-hangzhou.aliyuncs.com'
  accessKey: ''
  secretKey: ''
  bucketName: 'img-'
  domain: 'https://img-.oss-hangzhou.aliyuncs.com'
  

 
mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    #    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
    cache-enabled: false
    jdbc-type-for-null: 'null'

spring:
  servlet:
    multipart:
      max-file-size: 2048MB
      max-request-size: 2048MB
  mail:
    host: smtp.qq.com
    username: 1641436566@qq.com
    password: ''
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
    port: 587
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/super_bot?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
    username: root
    password: 123456
  data:
    redis:
      database: 2
      host: 127.0.0.1
      port: 6379
      password: ''
  application:
      name: pa-tools-service
      
sa-token:
  token-name: token
  active-timeout: -1
  is-concurrent: true
  is-share: false
  is-log: false
  # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
  token-style: uuid

config:
  linkTopImg: 
```

