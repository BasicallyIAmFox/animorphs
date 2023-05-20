package basicallyiamfox.ani.extensions

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

fun <E : Enum<E>> JsonObject.addProperty(property: String, value: E) = addProperty(property, value.name)
inline fun <reified E : Enum<E>> JsonObject.hasEnum(element: String): Boolean {
    if (hasString(element)) {
        return enumValueOfOrNull<E>(getString(element)) != null
    }
    return false
}
inline fun <reified E : Enum<E>> JsonObject.getEnum(name: String): E {
    if (hasString(name)) {
        val value = enumValueOfOrNull<E>(getString(name))
        if (value != null) {
            return value
        }
    }
    throw JsonSyntaxException("Expected $name to be an Enum")
}
inline fun <reified E : Enum<E>> JsonObject.asEnum(name: String): E {
    if (isJsonPrimitive) {
        val value = enumValueOfOrNull<E>(asString(name))
        if (value != null) {
            return value
        }
    }
    throw JsonSyntaxException("Expected $name to be an Enum")
}
inline fun <reified E : Enum<E>> JsonObject.asEnum(name: String, defaultValue: E): E {
    if (isJsonPrimitive) {
        val value = enumValueOfOrNull<E>(asString(name))
        if (value != null) {
            return value
        }
    }
    return defaultValue
}