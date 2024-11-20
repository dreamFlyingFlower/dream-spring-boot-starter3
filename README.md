# ReadMe



# dream-framework



# 介绍



* 利用SpringBoot3+JDK17版本开发的自动配置项目



# 软件架构




* dream-spring-boot-starter-cryption3:加密相关自动配置
* dream-spring-boot-starter-excel3:excel相关自动配置
* dream-spring-boot-starter-redis3:redis相关自动配置
* dream-spring-boot-starter-security3:安全相关自动配置
* dream-spring-boot-starter-storage3:存储相关自动配置
* dream-spring-boot-starter-web3:web相关自动配置




# 安装教程



* 直接引入即可使用



# cryption



## 概述



* 对第三方传递的数据进行解密
* 对传递给第三方的数据进行加密



## 简单使用



### 加密



* 引入当前starter
* 在需要加密的方法所属类上添加`CryptionController`
* 修改全局加密配置文件`EncryptResponseProperties`
* 在需要加密的方法上添加`EncryptResponse`



#### EncryptResponseProperties



* `secretKey`:全局加密密钥,默认1234567890qazwsx,长度必须是16的倍数
* `cryptType`:加密类型,默认AES
* `encryptClass`:需要加密的类型,如果为空,除void之外都加密



#### EncryptResponse



* `EncryptResponse#value()`:加密密钥,优先级高于`EncryptResponseProperties#secretKey`
* `EncryptResponse#cryptType()`:加密类型,优先级高于`EncryptResponseProperties#cryptType`



#### 注意



* 被`EncryptResponse`修饰的方法必须添加`org.springframework.web.bind.annotation.ResponseBody`或类上包含了该注解
* 被`EncryptResponse`修饰的方法若无返回值,不加密
* `EncryptResponse`会将方法返回值全部加密,而不会对单个数据加密



### 解密



* 引入当前starter
* 在需要解密的方法所属类上添加`CryptionController`
* 修改全局加密配置文件`DecryptRequestProperties`
* 在需要解密的方法上添加`DecryptRequest`



#### DecryptRequestProperties



* `secretKey`:全局加密密钥,默认1234567890qazwsx,长度必须是16的倍数
* `cryptType`:加密类型,默认AES



#### DecryptRequest



* `DecryptRequest#value()`:解密密钥,优先级高于`DecryptRequestProperties#secretKey`
* `DecryptRequest#cryptType()`:解密类型,优先级高于`DecryptRequestProperties#cryptType`



#### 注意



* 被`DecryptRequest`修饰的方法参数必须添加`org.springframework.web.bind.annotation.RequestBody`
* `DecryptRequest`会将参数全部解密,而不会对单个数据解密



## 相关类



* `CryptionController`:标识注解,在需要加密的方法所属的类上添加
* `EncryptResponse`:加密注解,在需要加密的方法上添加
* `EncryptResponseProperties`:全局加密配置
* `DecryptRequest`:解密注解,在需要解密的方法上添加
* `DecryptRequestProperties`:全局解密配置



# excel



# redis



# security



# storage



# web



## 相关类



* `AsyncExecutorAutoConfiguration`:该类中的`defaultAsyncTaskExecutor()`在SpringBoot2中可以使用,自定义异步任务等需要使用线程池的配置.SpringBoot3中已由`TaskExecutionAutoConfiguration`,`TaskExecutorConfigurations`,`TaskExecutorConfiguration`,`SimpleAsyncTaskExecutorBuilderConfiguration`等相关类进行优化,直接在配置文件中配置`spring.task.execution`即可.该自动配置删除



