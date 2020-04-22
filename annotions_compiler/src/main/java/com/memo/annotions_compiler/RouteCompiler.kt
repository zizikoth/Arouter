package com.memo.annotions_compiler

import com.google.auto.service.AutoService
import com.memo.annotation.Route
import sun.rmi.runtime.Log
import java.util.logging.Logger
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
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.memo.annotation.Route")
class RouteCompiler : AbstractProcessor() {

	lateinit var filer: Filer
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
			val path = typeElement.getAnnotation(Route::class.java).path
			val activityName = typeElement.qualifiedName.toString()
			// 把地址和Activity的带包名的地址存入
			activityMap[path] = activityName

			logger.printMessage(Diagnostic.Kind.NOTE, "发现路由组件$activityName")
		}

		// 生成文件
		if (activityMap.isNotEmpty()) {
			//生成唯一的文件名称 防止文件重复
			val fileName = "RouterUtils_${System.currentTimeMillis()}"
			try {
				val sourceFile = filer.createSourceFile(fileName)
				val fileContent = createFileContent(fileName,activityMap)
				//使用use方法自动关闭io流
				sourceFile.openWriter().use {
					it.write(fileContent)
				}
			} catch (e: Exception) {
				logger.printMessage(Diagnostic.Kind.ERROR, e.toString())
			}

		}

		return false
	}


	private fun createFileContent(fileName: String, activityMap: HashMap<String, String>): String {
		val buffer = StringBuffer()
		buffer.append("package com.memo.router.utils;\n" +
				"import com.memo.router.core.ARouter;\n" +
				"import com.memo.router.core.IRouter;\n" +
				"public class $fileName implements IRouter {\n" +
				"@Override\n" +
				"public void putActivity() {\n")
		activityMap.forEach { (path, activity) ->
			buffer.append("ARouter.get().setActivityClazz(\"$path\", $activity.class);\n")
		}
		buffer.append("}}")
		return buffer.toString()
	}
}
