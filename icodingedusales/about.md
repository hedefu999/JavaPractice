# 项目笔记

## 并发术语解释
QPS: query per second 每秒查询数
TPS: transaction per second 每秒执行事务数 
平均值 所有请求的平均响应时间 ms
中位数 

## 使用JMeter进行压测
TestPlan > Add > new Thread Group 创建线程组
Number of Threads(user) 并发访问用户数
Ramp-up period(seconds) 多少秒内完成并发模拟
Loop Count 循环次数

Thread Group > Add > Sampler > Http Request 添加HTTP请求

HttpRequest > Add > Listener > View Result Tree

聚合报告
Throughput - 吞吐量

## 使用wurstmeister/kafka搭建kafka集群
修改docker-compose.yml文件
```yml
version: '2'
services:
    zookeeper:
        image: wurstmeister/zookeeper
        hostname: zookeeper
        ports:
            - "2181:2181"
        container_name: zookeeper
    kafka:
        image: wurstmeister/kafka
        ports:
            - "9092"
        environment:
            # KAFKA_ADVERTISED_HOST_NAME: 192.168.1.2 ##修改宿主机IP
            KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://:9092  ##修改宿主机IP
            KAFKA_LISTENERS: PLAINTEXT://:9092
            KAFKA_ZOOKEEPER_CONNECT: "zookeeper:2181"
            # KAFKA_ADVERTISED_PORT: 9092
            # KAFKA_BROKER_ID: 1
            # KAFKA_OFFSET_TOPIC_REPLICATION_FACTOR: 1
        depends_on:
            - zookeeper
        # container_name: kafka 不要自定义名称，否则不能拷贝副本做集群
    kafka_manager:
        image: sheepkiller/kafka-manager
        environment:
            ZK_HOSTS: 192.168.1.2 ##修改宿主IP
        ports:
            - "9000:9000"       ##暴露端口

```
然后启动
`docker-compose up -d` -d表示后台静默启动，不然会把日志带到前台

接着创建kafka副本做集群 `docker-compose up -d --scale kafka=2`

访问kafka-manager管理kafka集群
创建cluster： host填 zookeeper:2181 zookeeper是zk的name
创建topic：

查看kafka版本号：`docker exec [kafkaName] find / -name \*kafka_\* | head -l | grep -o '\kafka[^\n]*`

使用docker命令创建kafka topic： `docker exec [kafkaName] kafka-topics.sh --create --topic [topicName] --partitions 4 --zookeeper zookeeper:2181 --replication-factor 2`

执行命令验证刚才创建的topic: `docker exec [kafkaName] kafka-topics.sh --list --zookeeper zookeeper:2181`

上面的命令list改describe查看详细信息

