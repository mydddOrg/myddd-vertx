package org.myddd.vertx.reflections

import org.reflections.Reflections
import javax.inject.Named

object ReflectionUtils {

    fun scan(vararg packages:String):List<ReflectionEntity>{
        val result = mutableListOf<ReflectionEntity>()
        val reflections = Reflections(packages)
        val implClasses = reflections.getTypesAnnotatedWith(Named::class.java)
        implClasses.forEach { implClass ->
            val namedAnnotation = implClass.getAnnotation(Named::class.java)
            val interfaces = implClass.interfaces
            interfaces.forEach { interfaceClass  ->
                result.add(ReflectionEntity(
                    name = namedAnnotation.value,
                    interfaceClass = interfaceClass,
                    implClass = implClass
                ))
            }
        }

        return result
    }
}