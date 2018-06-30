# PhotoSelector
仿微信的图片选择器

特别注意这个module使用provided files('libs/universal-image-loader-1.9.3-with-sources.jar')加载jar
 
所以主项目 必须引入 universal-image-loader-1.9.3-with-sources.jar ，否则提示找不到class

To get a Git project into your build:

### gradle：
```
 Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

allprojects {
 repositories {
  ...
  maven { url 'https://jitpack.io' }
 }
}

Step 2. Add the dependency

  dependencies {
   implementation 'com.github.androidlibraries:PhotoSelector:1.0.2'
}
```


[个人网站 www.jiangjiesheng.com](http://www.jiangjiesheng.com/ "胜行天下网") 

QQ:596957738 微信:596957738

EMail: dev@jiangjiesheng.com
