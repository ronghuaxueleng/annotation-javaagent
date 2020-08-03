# 简介

该模块主要是通过javaagent在程序启动时给java类动态添加注解

# 功能介绍

1、读取给定的配置文件，解析注解并添加到指定的类和方法上

2、在指定的目录中生成处理后的class文件

# 如何使用

在java启动时添加javaagent参数，指定本程序编译的jar包，并传递参数，如下所示：

```shell
java -javaagent:E:/javaagent.jar=-f=E:/annos.json,-d=E:/test -jar xxx.jar
```



> 命令说明：
>
> -javaagent参数后的jar的路径必须是绝对路径，否则可能找不到
>
> -f 和-d是javaagent.jar的参数
>
> ​	-f：注解文件绝对路径
>
> ​	-d：保存处理后的class文件的绝对路径
>
> ​	-h：查看参数说明

`annos.json`文件格式如下：

```json
{
    "test": {
        "fullClassName": "io.github.ronghuaxueleng.test.Test",
        "annotations": [{
            "name": "io.swagger.annotations.Api",
            "attrs": [{ "attrValue": ["专病应用"], "attrName": "tags" }]
        }],
        "list": [{
            "method": "update",
            "annotations": [{
                "name": "io.swagger.annotations.ApiOperation",
                "attrs": [{ "attrValue": "更新", "attrName": "value" }]
            }]
        }]
    }
}
```

其他请参考test下实例