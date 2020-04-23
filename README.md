首先了解页面Activity跳转，只要获取到了clazz对象，那么所有的Activity都可以跳转
```
    startActivity(mContext,clazz)
```
简单思路：

* 首先在底层模块中需要一个存放key和value的map，用于存放地址和Activity的class对象，

* 然后在每一个需要跨模块跳转的添加地址和class存入方法的工具类

* 之后App模块集成所有Module，在App模块中在合适的地方调用每一个Module的这些个方法工具类，就把需要的Activity的Class对象存入了

* 最后在需要调用的地方将需要的Activity的Class对象取出

进阶思路：

* 通过注解和注解处理器，将所有添加注解的Activity的path和class通过文件生成器自动生成，生成的文件需要固定使用同一个包名，但是文件名不能一样，这里生成的文件全部实现一个统一接口，这样会方便调用
* 之后在初始化的时候通过反射获取该包名下的所有类，并且实例化调用统一方法

##### 一、ARouter工具
```
    // 需要一个Map 存放当前App中所有需要跳转的Activity的Class对象
    private val activityMap: HashMap<String, Class<out Activity>> = hashMapOf()

    /*** 将地址和Activity的Class对象存入 ***/
    fun setActivityClazz(path: String, clazz: Class<out Activity>) {
        if (path.isNotEmpty()) {
            activityMap.put(path, clazz)
        }
    }

    /*** 读取存放的Activity ***/
    fun getActivityClazz(path: String): Class<out Activity>? {
        return activityMap[path]
    }
```
##### 二、IRouter公共接口
```
interface IRouter {
	fun putActivity()
}
```
##### 三、底层路径
```
object RouterPath{
    const val MemberActivity = "/Member/MemberActivity"
    const val NewsActivity = "/News/NewsActivity"
}
```
##### 四、Module数据存入
将Router模块依赖到app和其他需要跳转的所有module,在每一个需要跳转的module中创建一个ModuleRouter工具实现IRouter，这个时候可以获取到需要跳转的Class对象，存入ARouter组件中，例如：
```
object MemberRouter : IRouter {

    override fun putActivity() {
        ARouter.get().setActivityClazz(RouterPath.MemberActivity, MemberActivity::class.java)
    }
}
```
##### 五、App中的数据存入与获取
```
    // 在合适的地方进行调用 存入所有模块中需要跳转的Activity对象
    MemberRouter.putActivity()
    NewsRouter.putActivity()

    // 在Member模块中跳转到News模块中
    val clazz = ARouter.get().getActivityClazz(RouterPath.NewsActivity)
    startActivity(Intent(this,clazz))
```
到这里一个简单的路由组件的实现就完成了，基本思路就是通过中间件来存入和获取需要跳转的Activity的Class对象

##### 六、进阶-注解 仿照ARouter
###### 1、创建注解
创建Kotlin Library （annotaion）
```
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Route(val path: String)
```
###### 2、创建注解处理器
创建Kotlin Library （annotation_compiler）

我的AS是3.6.2都加上才会进行处理
`implementation 'com.google.auto.service:auto-service:1.0-rc6'`
`kapt 'com.google.auto.service:auto-service:1.0-rc6'`

```
// 自动注册
@AutoService(Processor::class)
// 表示使用Java8和build.gradle中保持同步
@SupportedSourceVersion(SourceVersion.RELEASE_8)
// 只进行解析Route注解
@SupportedAnnotationTypes("com.memo.annotation.Route")
class RouteCompiler : AbstractProcessor() {

	/*** 文件生成器 ***/
	lateinit var filer: Filer
	/*** 日志打印 ***/
	lateinit var logger: Messager

	/*** 初始化 ***/
	override fun init(environment: ProcessingEnvironment) {
		super.init(environment)
		filer = environment.filer
		logger = environment.messager
	}

	/*** 处理生成代码文件 ***/
	override fun process(set: MutableSet<out TypeElement>, environment: RoundEnvironment): Boolean {
		// 拿到Route注解的节点
		// 有三种注解节点  类节点TypeElement 方法节点ExecutableElement 变量节点VariableElement
		val routeSets = environment.getElementsAnnotatedWith(Route::class.java)
		// 存放path和activity
		val activityMap = HashMap<String, String>()
		routeSets.forEach {
			val typeElement = it as TypeElement
			// 地址
			val path = typeElement.getAnnotation(Route::class.java).path
			// Activity全路径
			val activityName = typeElement.qualifiedName.toString()
			// 把地址和Activity的带包名的地址存入
			activityMap[path] = activityName

			logger.printMessage(Diagnostic.Kind.WARNING, "发现路由组件-->$activityName")
		}

		// 生成文件
		if (activityMap.isNotEmpty()) {
			// 生成唯一的文件名称 防止文件重复 有多个Module多次调用
			// 按照ARouter这里是module名称拼接 偷懒了
			val fileName = "RouterUtils_${System.currentTimeMillis()}"
			try {
				// 创建文件
				// 使用use方法自动关闭io流
				filer.createSourceFile(fileName).openWriter().use {
					it.write(createFileContent(fileName, activityMap))
				}
			} catch (e: Exception) {
				logger.printMessage(Diagnostic.Kind.ERROR, e.toString())
			}

		}

		return false
	}


	/**
	 * 创建Java文件内容
	 * 这里可以使用JavaPoet或者KotlinPoet代替 依赖相应的包
	 * 我用的不熟练所以就直接字符串拼接了
	 */
	private fun createFileContent(fileName: String, activityMap: HashMap<String, String>): String {
		val buffer = StringBuffer()
		buffer.append(
			"package com.memo.router.utils;\n" +
					"import com.memo.router.core.ARouter;\n" +
					"import com.memo.router.core.IRouter;\n" +
					"public class $fileName implements IRouter {\n" +
					"@Override\n" +
					"public void putActivity() {\n"
		)
		activityMap.forEach { (path, activity) ->
			buffer.append("ARouter.get().setActivityClazz(\"$path\", $activity.class);\n")
		}
		buffer.append("}\n}")
		return buffer.toString()
	}
}
```
###### 3、调用putActivity方法
在初始化的时候可以传入ApplicationContext，利用这个进行页面跳转
之前我们在写文件的时候是固定包名的`com.memo.router.utils`,那么我们可以通过反射来找到对应包名下面的所有文件，ClassUtils直接从Arouter中CV过来
```
		val files = ClassUtils.getFileNameByPackageName(mContext, "com.memo.router.utils")
		files.forEach {
			try {
				val clazz = Class.forName(it)
				// 查看是否实现了IRouter
				if (IRouter::class.java.isAssignableFrom(clazz)) {
					// 开始调用putActivity方法
					(clazz.newInstance() as IRouter).putActivity()
				}
			} catch (e: Exception) {
			}
		}
```
##### 4、添加注解并且跳转
```
	/*** 通过ApplicationContext 直接跳转 ***/
	fun startActivity(path: String, bundle: Bundle? = null) {
		val clazz = activityMap[path]
		clazz?.let {
			val intent = Intent(mContext, it)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			if (bundle != null) intent.putExtras(bundle)
			mContext.startActivity(intent)
		}
	}

    ARouter.get().startActivity(RouterPath.NewsActivity)
```