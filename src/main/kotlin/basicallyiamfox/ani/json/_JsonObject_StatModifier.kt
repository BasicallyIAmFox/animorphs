package basicallyiamfox.ani.json

import basicallyiamfox.ani.util.StatModifier
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

fun JsonObject.addProperty(property: String, value: StatModifier) {
    val obj = JsonObject()
    obj.addProperty("base", value.base)
    obj.addProperty("additive", value.additive)
    obj.addProperty("multiplicative", value.multiplicative)
    obj.addProperty("flat", value.flat)
    add(property, obj)
}
fun JsonObject.hasStatModifier(property: String): Boolean {
    if (hasJsonObject(property)) {
        val obj = getAsJsonObject(property)
        return obj.hasNumber("base")
                && obj.hasNumber("additive")
                && obj.hasNumber("multiplicative")
                && obj.hasNumber("flat")
    }
    return false
}
fun JsonObject.getStatModifier(property: String): StatModifier {
    if (hasJsonObject(property)) {
        val obj = getAsJsonObject(property)
        val base = obj.getFloat("base", 0.0f)
        val additive = obj.getFloat("additive", 1.0f)
        val multiplicative = obj.getFloat("multiplicative", 1.0f)
        val flat = obj.getFloat("flat", 0.0f)
        return StatModifier(base, additive, multiplicative, flat)
    }
    throw JsonSyntaxException("Expected $property to be a Stat Modifier")
}
fun JsonObject.asStatModifier(property: String, defaultValue: StatModifier): StatModifier {
    if (hasJsonObject(property)) {
        val obj = getAsJsonObject(property)
        val base = obj.getFloat("base", 0.0f)
        val additive = obj.getFloat("additive", 1.0f)
        val multiplicative = obj.getFloat("multiplicative", 1.0f)
        val flat = obj.getFloat("flat", 0.0f)
        return StatModifier(base, additive, multiplicative, flat)
    }
    return defaultValue
}