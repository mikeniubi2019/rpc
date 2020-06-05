#rpc
本rpc为本人学习项目，设计架构图如下所示（点击可打开链接）：
[![设计架构图]](https://github.com/mikeniubi2019/rpc/blob/master/img/%E6%9C%AA%E5%91%BD%E5%90%8D%E6%96%87%E4%BB%B6.png "设计架构图")
#### # 项目亮点：
1，独立实现了一个简单的容器管理，读取配置文件，扫描注解配置的类，反射生成代理类，自动完成依赖注入。
2，项目组件化，面向接口编程，实现了低耦合高内聚系统。比如扫描接口，注册服务接口，容器上下文接口，负载均衡接口，编解码器接口等等。二次开发人员可以轻松实现自己的实现类并注册进容器里。
3，性能较高，基于netty实现底层通信，可以动态切换编解码器以及业务接口。接收到数据后，投入disruptor高性能内存队列，业务从队列取消息。实现了解耦合以及防止单个线程阻塞造成的性能低下。
4，小型分布式系统，高可用/高伸缩性，服务端动态注册服务到zookeeper服务中心。客户端从zookeeper拉取服务列表并缓存本地。实现心跳机制，如遇服务器下线，则动态把不可用服务器从服务列表剔除。
5，安全性，服务端实现限流服务，客户端实现超时服务，负载均衡接口以及熔断策略，二次开发人员可以实现自己的策略并注册进容器里。
6，客户端实现了简单的缓存功能。
7，启用版本号控制，服务端可以根据客户端传进来的版本号来调用相对应版本号的服务。
8，使用简单，使用注解扫描，只需分别在客户端/服务端编写一个配置文件，并在服务提供类和服务调用类上打上注解，即可使用！

#### 运行环境：
jdk1.8
其他条件暂时未知
#### 服务端结构图：
![](https://github.com/mikeniubi2019/rpc/blob/master/img/%E6%9C%8D%E5%8A%A1%E7%AB%AF%E7%BB%93%E6%9E%84%E5%9B%BE.png)
#### 客户端结构图：
![](https://github.com/mikeniubi2019/rpc/blob/master/img/%E5%AE%A2%E6%88%B7%E7%AB%AF%E7%BB%93%E6%9E%84%E5%9B%BE.png)

### 使用方法：
1，为了保证服务稳定性，客户端和服务端需要依赖相同的服务api。
2，服务端导入rpc-serve的jar包，客户端导入rpc-client的jar包。
3，服务端添加一个properties配置文件（现阶段可以配置项为）：
```
address=localhost //服务器ip
port=8061 //开放的端口号
basePackage=com.mike.rpc.service //扫描的包名
registIp=localhost //zookeeper注册中心地址
registPort=2181 //zookeeper端口号
weight=3 //当前服务器权重
```
编写一个实现api接口的服务实现类
并在类上打上注解 
```
@ServiceProvicer(beanName = "//你的服务名",version = "版本号 默认为v1.0")
```
编写一个有main方法的启动类，在main方法里启动服务：

```
AnnotationContex annotationContex = new AnnotationContex("你的配置文件.properties");
```
如此即可启动服务！！！

4，客户端启动方法：
配置文件：
```
basePackage=com.mike.rpc.service //扫描的包名
registIp=localhost //zookeeper注册中心地址
registPort=2181 //zookeeper端口号
```
在你想使用api接口的类上打上注解：
```
@ServiceProvicer(cache = "true" 默认开启，可不配置)
```
然后在此类的依赖api字段上打上注解：
```
@ServiceProx(beanName = "服务名称" )
```
项目会自动为你生成远程调用的实现类，你直接使用此api方法即可！
如果你需要熔断处理，则编写一个熔断方法，并打上注解：
```
@Fallback
```
如果服务器不可用执行熔断策略，自动调用此回退方法。
编写一个有main方法的启动类，在main方法里启动服务：

```
AnnotationContex annotationContex = new AnnotationContex("你的配置文件.properties");
Service service = annotationContex.getServices("服务名+版本号 例如TestServicev1.0");//获取服务
TestServiceProx testService = (TestServiceProx)service.getInstant();//获取服务实例
```
如此即可启动服务！！！

