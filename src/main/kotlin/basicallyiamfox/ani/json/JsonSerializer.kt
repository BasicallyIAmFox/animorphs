package basicallyiamfox.ani.json

import com.google.gson.JsonElement
import java.util.function.Function
import javax.management.OperationsException

class JsonSerializer {
    companion object {
        class Self<T>(var value: T)

        val toJsonFunctions: MutableMap<String, MutableMap<String, Function<Self<*>, JsonElement>>> =
            hashMapOf()
        val fromJsonFunctions: MutableMap<String, MutableMap<String, Function<JsonElement, *>>> =
            hashMapOf()

        inline fun <reified TType, reified TReturn : JsonElement> addSerializer(function: Function<TType, TReturn>) {
            val typeClass = TType::class.java.name
            val returnClass = TReturn::class.java.name

            val map: MutableMap<String, Function<Self<*>, JsonElement>>
            if (toJsonFunctions.containsKey(typeClass)) {
                map = toJsonFunctions[typeClass]!!
                if (map.containsKey(returnClass)) {
                    throw OperationsException()
                } else {
                    map[returnClass] = Function { self: Self<*> -> function.apply(self.value as TType) }
                }
            } else {
                map = hashMapOf()
                map[returnClass] = Function { self: Self<*> -> function.apply(self.value as TType) }
                toJsonFunctions[typeClass] = map
            }
        }

        @Throws(OperationsException::class)
        inline fun <reified TType, reified TReturn : JsonElement> addDeserializer(function: Function<TReturn, TType>) {
            val typeClass = TType::class.java.name
            val returnClass = TReturn::class.java.name

            val map: MutableMap<String, Function<JsonElement, *>>
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
                map = hashMapOf()
                map[returnClass] = Function<JsonElement, Any> { self: JsonElement ->
                    function.apply(
                        self as TReturn
                    )
                }
                fromJsonFunctions[typeClass] = map
            }
        }

        inline fun <reified TType, reified TReturn : JsonElement> toJson(value: TType): TReturn {
            val map = toJsonFunctions[TType::class.java.name]!!
            val func = map[TReturn::class.java.name]!!
            return func.apply(Self(value)) as TReturn
        }

        inline fun <reified TType, reified TReturn : JsonElement> fromJson(obj: TReturn): TType {
            val map = fromJsonFunctions[TType::class.java.name]
            val func = map?.get(TReturn::class.java.name)
            return func?.apply(obj) as TType
        }
    }
}