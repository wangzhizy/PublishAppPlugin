Gradle是Android使用的构建工具，了解它可以帮助我们理解Android项目的构建过程，实现我们在构建过程中的小需求。

以实现一个自动生成渠道包（打包，360加固，多渠道配置）的自定义插件为例，介绍一下Gradle的基础知识。

## 总览：

1. Groovy基础
1. Gradle执行流程
1. Gradle生命周期
1. Project
1. Task
1. Plugin

## 1：Groovy基础

Gradle的开发语言是Groovy，所以我们学习Gradle需要掌握Groovy，不过对于我们Android攻城狮来说这不是问题。为什么这么说呢：

1. 在Groovy中可以使用所有的Java类库；
1. Groovy最终也是编译为Java字节码执行在Java虚拟机上的；
1. 在上面的基础上Groovy对Java做了许多封装和扩展，方便我们的使用；

所以在我们的开发过程中，我们可以使用Java代码实现我们的需求。不过为了不浪费Groovy为我们的封装、与源码更好的交互，我们还是了解一下比较好，下面我们来看一下它与Java有那些不一样的地方。

### 1.1：def关键字
见名知意，这个是用来定义的，可以用来定义变量和方法。
1. 定义变量时，表示这个变量是动态类型的，下面的代码完全没有问题。
    ```
    def value = 1
    value = "wangzhi"
    ````
1. def也可以用来定义方法，当方法指明返回值时，def关键字可以被省略。
### 1.2：String
Groovy中有两种String，String（java.lang.String）和GString（groovy.lang.GString），在Groovy中有三种不同的定义String的方式，我们使用最多的应该是第二种；
1. 单引号（java.lang.String）：
    ```
    def value = '我是一个字符串' 
    ```
1. 双引号（groovy.lang.GString）：
    ```
    def value1 = "我支持使用表达式，${1+1}，${value}" 
    ```
1. 三引号（java.lang.String）：
    ```
    def value2 = """我支持
    换
    行，等格式
    """
    ```
### 1.2：List
1. 定义：```def value = ["a", "b", "c"]```
1. 改：```value[0] = "e"```
1. 查：```value[0]```

Groovy默认会使用ArrayList，如果你想使用别的可直接使用Java中强类型的声明方式：

例如：```LinkedList value = [1, 2, 3]```

### 1.3：Map
1. 定义：```def value = ["a": 1, "b": 2, "c": 3]```
1. 增、改：```value.a = 100```
1. 查：```value[a]```或```value.a```

这里默认会使用LinkedHashMap

## 2：Gradle执行流程
## 3：Gradle生命周期
## 4：Project
## 5：Task
## 6：Plugin

函数调用的时候还可以不加括号
list、map、range
闭包
最后一个参数是闭包的话，可以省略圆括号

api的封装
    file的读取为例


首先是初始化阶段。对我们前面的multi-project build而言，就是执行settings.gradle
Configration阶段的目标是解析每个project中的build.gradle。比如multi-project build例子中，解析每个子目录中的build.gradle。在这两个阶段之间，我们可以加一些定制化的Hook。这当然是通过API来添加的。
