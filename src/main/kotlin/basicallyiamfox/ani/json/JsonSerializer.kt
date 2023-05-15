package basicallyiamfox.ani.json

import com.google.gson.JsonElement
import java.util.function.Function
import javax.management.OperationsException

class JsonSerializer {
    companion object {
        class Self<T>(var value: T)

        val toJsonFunctions: MutableMap<Class<*>, MutableMap<Class<out JsonElement>, Function<Self<*>, JsonElement>>> =
            HashMap()
        val fromJsonFunctions: MutableMap<Class<*>, MutableMap<Class<out JsonElement>, Function<JsonElement, *>>> =
            HashMap()

        inline fun <reified TType, reified TReturn : JsonElement> addSerializer(function: Function<TType, TReturn>) {
            val typeClass: Class<TType> = TType::class.java
            val returnClass: Class<TReturn> = TReturn::class.java

            val map: MutableMap<Class<out JsonElement>, Function<Self<*>, JsonElement>>
            if (toJsonFunctions.containsKey(typeClass)) {
                map = toJsonFunctions[typeClass]!!
                if (map.containsKey(returnClass)) {
                    throw OperationsException()
                } else {
                    map[returnClass] = Function { self: Self<*> -> function.apply(self.value as TType) }
                }
            } else {
                map = HashMap()
                map[returnClass] = Function { self: Self<*> -> function.apply(self.value as TType) }
                toJsonFunctions[typeClass] = map
            }
        }

        @Throws(OperationsException::class)
        inline fun <reified TType, reified TReturn : JsonElement> addDeserializer(function: Function<TReturn, TType>) {
            val typeClass: Class<TType> = TType::class.java
            val returnClass: Class<TReturn> = TReturn::class.java

            val map: MutableMap<Class<out JsonElement>, Function<JsonElement, *>>
            if (fromJsonFunctions.containsKey(typeClass)) {
                map = fromJsonFunctions[typeClass]!!
                if (map.containsKey(returnClass)) {
                    throw OperationsException()
                } else {
                    map[returnClass] = Function<JsonElement, Any> { self: JsonElement ->
                        function.apply(
                            self as TReturn
                        )
                    }
                }
            } else {
                map = HashMap()
                map[returnClass] = Function<JsonElement, Any> { self: JsonElement ->
                    function.apply(
                        self as TReturn
                    )
                }
                fromJsonFunctions[typeClass] = map
            }
        }

        inline fun <reified TType, reified TReturn : JsonElement> toJson(value: TType): TReturn {
            return toJsonFunctions[TType::class.java]!![TReturn::class.java]!!.apply(Self(value)) as TReturn
        }

        inline fun <reified TType, reified TReturn : JsonElement> fromJson(obj: TReturn): TType {
            return fromJsonFunctions[TType::class.java]!![TReturn::class.java]!!.apply(obj) as TType
        }
    }
}