# bargainsDash
秒杀系统实战

* 商品秒杀，主要技术SpringBoot MyBatis Thymeleaf Redis RabbitMQ，并使用Jmeter进行压力测试。
* 秒杀主要优化点：页面缓存 预减库存 MQ异步处理 验证码隐藏真实秒杀地址 接口限流防刷等。

测试步骤：
1. baraginsDash.sql（user表初始化有5000个用户）,执行刷库，初始化数据
2. 执行BeforeTest.java 中的http://localhost:8080/prepareLoginUser， 使用5000个用户登录，并将sessionId输出到D:\tokens.txt，这一步可能需要几分钟
3. 将bargainsDash.jmx导入jmeter，选中需要测试的接口，开始测试

更详细的说明，请参看[简书上的系列文章](https://www.jianshu.com/nb/29972857)。
