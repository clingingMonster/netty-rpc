# netty-rpc 

基于netty的rpc


## 特性
1.底层是适用的netty 长连接通信
2.注册中心目前只实现了eureka 这一种方式，用户也可以自己扩展
3.客户端采用类似feign的方式
4.服务端还是controller模式 (现在只是简单版本,有些请求还不支持)
5.也实现了负载均衡,同时也支持可以自己拓展



## 版本要求
- JDK 1.8 以上
- Spring Boot 1.5 以上

## 适用

Add a dependency using maven:

```xml
<!--add dependency in pom.xml-->
 <dependency>
      <groupId>com.xr.netty</groupId>
      <artifactId>remote</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>
``` 

## 列子

查看 [test-server1,test-server2](这个地方是服务和客户端一起引用了,要是想单独适用可以分别引用).


## License
[Apache License, Version 1.5](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation 
