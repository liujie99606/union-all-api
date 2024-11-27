# 多平台返利系统

本项目是一个基于Spring Boot的多平台返利系统，支持淘宝联盟、京东联盟、拼多多多多客等主流电商平台的返利对接。

## 系统特点

* 支持主流电商平台返利对接
* 完整的订单追踪系统
* 灵活的分佣配置
* 实时的数据统计分析
* 支持多级分销体系
* 支持Docker一键部署

## 技术架构

* Java 后端：`master` 分支为 JDK 8 + Spring Boot 2.5
* 后端采用 Spring Boot 多模块架构、MySQL + MyBatis Plus、Redis + Redisson
* 数据库使用 MySQL
* 消息队列使用 Event、Redis 等
* 容器化部署支持 Docker + Docker Compose

## Docker一键部署

1. 安装必要的依赖
```bash
# CentOS
yum install -y docker docker-compose

# Ubuntu
apt-get install docker docker-compose
```

2. 启动服务
```bash
# 进入部署目录
cd docker/deploy

# 启动服务
docker-compose up -d
```

3. 验证部署
```bash
# 查看容器状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

### 服务访问
- 接口文档: http://localhost:48080/doc.html

## 主要功能模块

### 订单追踪系统
- 实时订单同步
- 订单状态追踪
- 售后订单处理
- 订单佣金计算

### 佣金管理
- 多级分销配置
- 佣金自动结算
- 提现管理
- 账户流水记录

## SDK配置说明

### 京东联盟SDK
```shell
mvn install:install-file -Dfile=你的文件路径/union-all-api/union-web/src/main/resources/lib/open-api-sdk-2.0-2024-09-27.jar  -DgroupId=open.api.sdk -DartifactId=open-api-sdk -Dversion=2.0 -Dpackaging=jar
```

### 淘宝联盟SDK
```shell
mvn install:install-file -Dfile=你的文件路径/union-all-api/union-web/src/main/resources/lib/taobao-sdk-NEW_JAVA.jar  -DgroupId=tao.bao.sdk -DartifactId=taobao-sdk-java-auto -Dversion=1.1 -Dpackaging=jar
```

### 拼多多多多客SDK
```shell
mvn install:install-file -Dfile=你的文件路径/union-all-api/union-web/src/main/resources/lib/pop-sdk-1.18.41-all.jar  -DgroupId=pop.sdk -DartifactId=pop-sdk -Dversion=1.18.41 -Dpackaging=jar
```

## 开发环境要求

- JDK: 8+
- Maven: 3.6+
- MySQL: 5.7+
- Redis: 5.0+
- Docker: 18.0+ (可选)
- Docker Compose: 1.29+ (可选)

## 部分功能展示
### 查询返利
<img src="images/%E6%9F%A5%E8%AF%A2%E8%BF%94%E5%88%A9.jpg" alt="查询返利" width="250">

### 提现
<img src="images/%E6%8F%90%E7%8E%B0.jpg" alt="提现" width="250">

### 签到和余额
<img src="images/%E7%AD%BE%E5%88%B0%E5%92%8C%E4%BD%99%E9%A2%9D.jpg" alt="签到和余额" width="250">


## 项目合作

我们是专业的返利系统开发团队，提供完整的返利系统解决方案，团队包含：
- 专业项目经理
- 架构师
- 前端工程师
- 后端工程师
- 测试工程师
- 运维工程师

### 可提供的服务
- 返利系统定制开发
- 电商平台对接
- 分销体系搭建
- 支付系统对接
- 数据统计分析
- 运营支持服务

如需项目合作，请联系微信：
<img src="images/%E5%BE%AE%E4%BF%A1%E5%8F%B7.jpg" alt="微信号" width="350">


