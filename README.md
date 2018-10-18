# tangula-android-http
## 项目介绍
唐古拉Android开发工具包系列项目中关于HTTP的支持。

项目背景见:[tangula-android-commons项目](https://github.com/eastoneking/tangula-android-commons)。

## 使用说明

- 第一步：添加JitPack仓库到你的主项目的build.gradle

添加下面代码到工程根目录的build.gradle中
```Groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

- 第二步:添加APP项目的依赖
```Groovy
	dependencies {
	        implementation 'com.github.eastoneking:tangula-android-http:$版本号'
	}
```

其他说明见项目[WIKI](https://github.com/eastoneking/tangula-android-http/wiki).

-----

The End