Gradle是Android使用的构建工具，了解它可以帮助我们理解Android项目的构建过程，实现我们在构建过程中的小需求。

本文以实现一个自动生成渠道包（打包，360加固，多渠道配置）的自定义插件为例，介绍一下Gradle的基础知识。

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
    def value1 = "我支持使用表达式，$value，${1+1}" 
    ```

1. 三引号（java.lang.String）：

    ```
    def value2 = """我支持
    换
    行
    """
    ```

### 1.3：List

1. 定义：```def value = ["a", "b", "c"]```
1. 改：```value[0] = "e"```
1. 查：```value[0]```

Groovy默认会使用ArrayList，如果你想使用别的可直接使用Java中强类型的声明方式：

例如：```LinkedList value = [1, 2, 3]```

### 1.4：Map

1. 定义：```def value = ["a": 1, "b": 2, "c": 3]```
1. 增、改：```value.a = 100```或```value["a"] = 100```
1. 查：```value[a]```或```value.a```

这里默认会使用LinkedHashMap

### 1.5：Range

1. 定义：```def value = 1..100```
1. 使用：```if (1 in value)```

### 1.6：闭包

闭包真的是Groovy中一个很重要的东西，闭包可能有一点像Java中的lambda表达式，不过闭包要强大许多；

1. 闭包的定义：

    ```
    def value = { String name ->
        println name
    }
    ```

    闭包的参数类型可以被省略，上面的代码可以修改为：

    ```
    def value = { name ->
        println name
    }
    ```

    闭包只有一个参数时，这个参数可以被省略，我们可以直接使用```it```来访问这个参数，上面的代码可以修改为：

    ```
    def value = {
        println it
    }
    ```

1. 闭包的使用：

    以我们刚才定义的闭包为例：```value.call("wangzhi")```或```value("wangzhi")```

1. 闭包的委托策略：

    1. 闭包有三个相关对象:
        - this：闭包定义处最近的对象(不包含闭包)；
        - owner：闭包定义处最近的对象或闭包；
        - delegate：闭包的代理对象，默认和owner一致，可以手动设置；

        不太明白这三个对象的同学不用着急，后面我们用一个例子就可以很清楚的解释它们的作用。

    2. 闭包的委托策略：
        - ```Closure.OWNER_FIRST```：默认策略，首先从owner上寻找属性或方法，找不到则在delegate上寻找。
        - ```Closure.DELEGATE_FIRST```：先在owner上寻找，后再delegate上寻找。
        - ```Closure.OWNER_ONLY```：只在owner上寻找
        - ```Closure.DELEGATE_ONLY```：只在delegate上寻找
        - ```Closure.TO_SELF```：高级选项，让开发者自定义策略

    3. 举个例子：请看如下代码

        ```
        class A{
            String name
            def value = {
                println name
            }
        }

        def a = new A()
        a.name = "wangzhi"
        a.value.call()
        ```

        在这里闭包的三个对象都是A的实例对象，这个应该比较好理解，代码也没什么问题，可以正确输出，下面我们把代码改动一下

        ```
        class A{
            String name
            def value = {
                println age
            }
        }

        def a = new A()
        a.name = "wangzhi"
        a.value.call()
        ```

        在这里闭包的三个对象仍然是A的实例对象，不过这里代码就有问题了，因为A里没有age这个属性，这时候代理对象就可以派上用场了，我们再改动一下

        ```
        class A{
            String name
            def value = {
                println age
            }
        }
        class B{
            int age
        }

        def a = new A()
        a.name = "wangzhi"

        def b = new B()
        b.age = 18

        a.value.delegate = b
        a.value.call()
        ```

        在这里闭包的代理对象是B的实例对象b，按照默认的委托策略，当闭包在owner中找不到的时候，会在delegate中寻找，所以上面的代码可以正确的输出18

    4. 在Groovy中当函数的最后一个参数是闭包时，调用时可以省略圆括号。

        ```
        buildscript {
            repositories {
                google()
                jcenter()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:3.1.4'
            }
        }
        ```

        是不是很熟悉，这里其实是调用了Project的buildscript方法，不过因为buildscript方法只有一个类型为闭包的参数，所以在这里圆括号是可以省略的，repositories、repositories也是一样的道理，如果是Java应该像下面这么写

        ```
        buildscript({
            repositories({
                google()
                jcenter()
            })
            dependencies({
                classpath 'com.android.tools.build:gradle:3.1.4'
            })
        })
        ```

### 1.7：[了解更多](https://blog.csdn.net/singwhatiwanna/article/details/76084580)
    
## 2：Gradle执行流程

1. 初始化阶段：解析settings.gradle来获取模块信息
1. 配置阶段：配置每个模块，构建task树
1. 执行阶段：执行任务
1. [了解更多](https://blog.csdn.net/singwhatiwanna/article/details/78797506)

## 3：Gradle生命周期

1. 配置阶段开始之前：project.beforeEvaluate
1. 配置阶段结束：project.afterEvaluate
    
    这是一个比较重要的生命周期，当走到这里时说明所有的task都已经配置完成了，我们可以对其进行操作，加入我们的逻辑或插入我们的自定义task。

1. 执行阶段结束：project.gradle.buildFinished

## 4：Project



## 5：Task
## 6：Plugin


首先是初始化阶段。对我们前面的multi-project build而言，就是执行settings.gradle
Configration阶段的目标是解析每个project中的build.gradle。比如multi-project build例子中，解析每个子目录中的build.gradle。在这两个阶段之间，我们可以加一些定制化的Hook。这当然是通过API来添加的。
