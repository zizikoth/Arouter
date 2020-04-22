package com.memo.annotions_compiler

import com.google.auto.service.AutoService
import com.memo.annotation.Route
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


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
class RouteCompiler : AbstractProcessor() {

	lateinit var filer: Filer

	/*** 初始化 ***/
	override fun init(environment: ProcessingEnvironment) {
		super.init(environment)
		filer = environment.filer
	}

	/*** 返回处理注解处理器需要处理的所有注解 ***/
	override fun getSupportedAnnotationTypes(): MutableSet<String> {
		// 只处理Route的注解
		return mutableSetOf(Route::class.java.canonicalName)
	}

	/*** 返回注解处理器支持的Java版本 ***/
	override fun getSupportedSourceVersion(): SourceVersion {
		return processingEnv.sourceVersion
	}

    /*** 处理生成代码文件 ***/
	override fun process(set: MutableSet<out TypeElement>, environment: RoundEnvironment): Boolean {
        // 拿到Route注解的节点
        val set = environment.getElementsAnnotatedWith(Route::class.java)
        // 数据结构
        val map:Map<String,String> = HashMap()
        set.forEach {
            val typeElement = it as TypeElement
            val route = typeElement.getAnnotation(Route::class.java)
            typeElement.qualifiedName.toString()
        }
        return false
	}
}
