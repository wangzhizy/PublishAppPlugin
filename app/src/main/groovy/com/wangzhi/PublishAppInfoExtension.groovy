package com.wangzhi
/**
 * 与自定义PLugin进行参数传递
 */
class PublishAppInfoExtension {

    /**
     * 360加固包（jiagu.jar）的文件路径
     */
    String qihuPath

    /**
     * keyStor文件路径
     */
    String keyStorePath

    /**
     * keyStor密码
     */
    String keyStorePass
    /**
     * keyStoreAlias
     */
    String keyStoreKeyAlias
    /**
     * keyStoreKeyAlias密码
     */
    String keyStoreKeyAliasPass
    /**
     * 渠道文件路径
     */
    String channelPath
    /**
     * 文件夹输出路径
     */
    String outputPath
    /**
     * 项目名
     */
    String projectName
    /**
     * 版本号
     */
    String projectVersion
    /**
     * apkName
     */
    String apkName

    PublishAppInfoExtension() {

    }
}