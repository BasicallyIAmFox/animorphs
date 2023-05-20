package basicallyiamfox.ani.extensions

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.util.Identifier

fun JsonObject.addProperty(property: String, value: Identifier) = addProperty(property, value.toString())
fun JsonObject.hasIdentifier(element: String): Boolean {
    if (hasString(element)) {
        return Identifier.tryParse(getString(element)) != null
    }
    return false
}
fun JsonObject.getIdentifier(name: String): Identifier {
    if (hasString(name)) {
        val value = Identifier.tryParse(getString(name))
        if (value != null) {
            return value
        }
    }
    throw JsonSyntaxException("Expected $name to be an Identifier")
}
fun JsonObject.asIdentifier(name: String): Identifier {
    if (isJsonPrimitive) {
        val value = Identifier.tryParse(asString(name))
        if (value != null) {
            return value
        }
    }
    throw JsonSyntaxException("Expected $name to be an Identifier")
}
fun JsonObject.asIdentifier(name: String, defaultValue: Identifier): Identifier {
    if (isJsonPrimitive) {
        val value = Identifier.tryParse(asString(name))
        if (value != null) {
            return value
        }
    }
    return defaultValue
}