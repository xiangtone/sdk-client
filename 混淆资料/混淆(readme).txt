混淆的ep_helper.jar包的步骤：
1、混淆核心包，export出来的core核心包，对core核心包进行混淆。
   参考附件中的《Android混淆文档.docx》内的jar混淆步骤。
2、将混淆的核心包dex处理生成ep.jar
3、将混淆后的ep.jar放到壳的工程里面去，打包出ep_helper.jar包
4、混淆ep_helper.jar包。

混淆apk步骤
1、将混淆的ep_helper.jar加入libs文件夹
2、工程目录下的project.properties文件内确认这行没被注释掉：
   proguard.config=${sdk.dir}/tools/proguard/proguard-android.txt:proguard-project.txt
3、修改proguard-project.txt文件(参考《apk混淆文件》文件夹中的proguard-project.txt)。
4、必须export出apk才能起到混淆的作用。

