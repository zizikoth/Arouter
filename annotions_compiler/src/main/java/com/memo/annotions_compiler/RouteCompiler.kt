package com.memo.annotions_compiler

import com.google.auto.service.AutoService
import com.memo.annotation.Route
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


/**
 * title:这个注解处理器用来就是用来生成各个Module中的的Router工具 例如MemberRouter
 * describe:
 *
 * @author memo
 * @date 2020-04-22 14:17
 * @email zhou_android@163.com
 *
 * Talk is cheap, Show me the code.
 */
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
