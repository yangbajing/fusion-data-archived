# MassData

*反映式*海量数据平台（mass-rdp, Reactive MassData asset management Platform）

- Java 8+
- Scala 2.12+
- Akka 2.5+

## 文档

```
./sbt mass-docs/paradox
google-chrome mass-docs/target/paradox/site/main/index.html
```

## 编译，构建

```
./sbt -Dbuild.env=prod "project mass-console" universal:packageZipTarball  \
  "project mass-data" assembly \
  "project mass-engine" universal:packageZipTarball \
  "project mass-console" universal:packageZipTarball \
```

## 项目结构

- [mass-docs](mass-docs)：使用 [Lightbend Paradox](https://developer.lightbend.com/docs/paradox/latest/) 编写的文档。在线访问地址：[http://www.yangbajing.me/mass-data/doc/](http://www.yangbajing.me/mass-data/doc/)
- [mass-functest](mass-functest)：功能测试项目，支持 multi-jvm 测试（单机上启动多个JVM实例模拟集群）
- [mass-console](mass-console)：管理控制台、监控、业务流程编排
- [mass-broker](mass-broker)：mass 执行Broker节点
- [mass-connector](mass-connector): 基于Akka Stream的数据连接器
- [mass-core-ext](mass-core-ext)：mass 核心库扩展
- [mass-ipc](mass-pic)：mass 通信协议、规范 ？需要
- [mass-core](mass-core)：mass 核心库
- [mass-common](mass-common)：一些工具类、库

orchestration 编排

## 开发

#### 启动数据库

```
sudo docker-compose up --build -d
```

#### 访问数据库

**Postgres**

```
sudo docker run -it --rm --net scripts_default --link scripts_mass-postgres_1:postgres postgres:10.4 psql -h postgres -U massdata -d massdata
```
或
```
sudo docker run -it --rm --net host postgres:10.4 psql -h 127.0.0.1 -U massdata -d massdata
```

**MySQL**

```
sudo docker run -it --rm --net scripts_default --link scripts_mass-mysql_1:mysql mysql:5.7 mysql -h mysql -u root -D mysql -p
```
或
```
sudo docker run -it --rm --net host mysql:5.7 mysql -h 127.0.0.1 -u root -D mysql -p
```

**SQL Server**

```
sudo docker run -it --rm --net host microsoft/mssql-server-linux:2017-latest \
    /opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P Massdata.2018
```
