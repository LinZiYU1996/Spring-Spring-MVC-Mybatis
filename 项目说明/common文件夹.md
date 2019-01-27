HttpInterceptor类
Http拦截器	继承HandlerInterceptorAdapter
- 方法preHandle：有Http请求的时候，获取请求Url，获取当前时间，输出日志（包括Url，参数内容），方便进行查错
- 方法afterCompletion：请求完成后，获取Url及当前时间，与之间时间比较得到花费时间，输出日志

RequestHolder
把每个用户放进ThreadLocal中，方便进行管理及操作

SpringExceptionResolver
异常处理：比较各种异常，输出对应的日志，然后返回相应处理

JsonData：自己的Json数据返回格式，方便进行处理

ApplicationContextHelper：获取Spring上下文管理的bean

