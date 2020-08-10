# 秒杀系统设计
初版

## 秒杀活动的特点
- 库存有限，读多写少
- 不能超卖，否则带来经济损失和信誉受损
- 提防黄牛黑产
- 用户不能重复购买

使用缓存技术，应对读多写少的业务场景
分布式缓存有 redis memcache
本地缓存有 ehcache hashmap

由于木桶效应，分布式系统最薄弱的一环是mysql数据库，可以使用mq异步将生成的订单落到mysql中

## docker安装运行环境
### mysql
本机上准备3个同级目录 conf conf.d data
conf下存放配置文件my.cnf
```
[mysqld]
user=mysql
character-set-server=utf8
default-authentication-plugin=mysql_native_password
secure_file_priv=/var/lib/mysql
expire_logs_days=7
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
max_connections=10

[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

```
启动命令
```
docker run --restart=always --privileged=true -d -v /Users/hedefu/Documents/DEVELOPER/Docker/mysql@5.7/data/:/var/lib/mysql -v /Users/hedefu/Documents/DEVELOPER/Docker/mysql@5.7/conf.d:/etc/mysql/conf.d -v /Users/hedefu/Documents/DEVELOPER/Docker/mysql@5.7/conf/my.cnf:/etc/mysql/my.cnf -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=123456 mysql:5.7
```
登录mysql：
先进入容器 `docker exec -ti mysql bash` 再使用mysql命令： mysql -uroot -p

### redis
启动命令
```
docker run -p 6379:6379 --name redis -v /Users/hedefu/Documents/DEVELOPER/Docker/redis/conf/redis.conf:/etc/redis/redis.conf -v /Users/hedefu/Documents/DEVELOPER/Docker/redis/data:/data -d redis redis-server /etc/redis/redis.conf
```
进入docker中的redis控制台：  `docker exec -it redis redis-cli`

### kafka
需要先安装zookeeper，使用docker-compose安装多容器的app

github上的wurstmeister/docker-kafka提供了kafka的docker-compose安装文件
`git clone --depth 1 https://github.com/wurstmeister/kafka-docker.git`
需要修改docker-compose.xml文件 10.10.14.129
原始内容：
```
# 原始内容
# version: '2'
# services:
#   zookeeper:
#     image: wurstmeister/zookeeper
#     ports:
#       - "2181:2181"
#   kafka:
#     build: .
#     ports:
#       - "9092"
#     environment:
#       KAFKA_ADVERTISED_HOST_NAME: 10.10.14.129
#       KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
#     volumes:
#       - /var/run/docker.sock:/var/run/docker.sock
```
修改为下述内容，IP地址需要修改，冒号后如果有字符需要带上一个空格
```
version: '2'
services:
    zoo1:
        image: wurstmeister/zookeeper
        restart: unless-stopped
        hostname: zoo1
        ports:
            - "2181:2181"
        container_name: zookeeper
    kafka1:
        image: wurstmeister/kafka
        ports:
            - "9092:9092"
        environment:
            KAFKA_ADVERTISED_HOST_NAME: 10.10.14.129 ##修改宿主机IP
            KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://10.10.14.129:9092  ##修改宿主机IP
            KAFKA_LISTENERS:PLAINTEXT: //0.0.0.0:9092
            KAFKA_ZOOKEEPER_CONNECT: "zoo1:2181"
            KAFKA_ADVERTISED_PORT: 9092
            KAFKA_BROKER_ID: 1
            KAFKA_OFFSET_TOPIC_REPLICATION_FACTOR: 1
        depends_on:
            - zoo1
        container_name: kafa1
    kafka2:
        image: wurstmeister/kafka
        ports:
            - "9093:9092"
        environment:
            KAFKA_ADVERTISED_HOST_NAME: 10.10.14.129 ##修改宿主机IP
            KAFKA_ADVERTISED_LISTENERS:PLAINTEXT: //10.10.14.129:9092  ##修改宿主机IP
            KAFKA_ZOOKEEPER_CONNECT: "zoo1:2181"
            KAFKA_ADVERTISED_PORT: 9093
            KAFKA_BROKER_ID: 2
            KAFKA_OFFSET_TOPIC_REPLICATION_FACTOR: 1
        depends_on:
            - zoo1
        container_name: kafa2
    kafka-manager:
        image: sheepkiller/kafka-manager
        environment:
            ZK_HOSTS: 10.10.14.129 ##修改宿主IP
        ports:
            - "9000:9000"       ##暴露端口
```
cd到github上下载的docker-kafka目录下 `docker-compose up -d` 执行安装


## 收集技术点列表
- JDK各种版本的区别，优点
    JDK8的优点：长期支持的免费版本 lambda表达式 函数式编程  API的优化 增加g1垃圾回收器
- 各种缓存的特点
redis 支持的数据类型多 有成熟的集群方案 可持久化  单线程IO多路复用 性能高
memcache 只支持string 不支持持久化 集群方案需要自己实现 多线程，线程不安全
- 各种mq的区别