package basicallyiamfox.ani.json

import basicallyiamfox.ani.util.StatModifier
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import java.math.BigDecimal
import java.math.BigInteger

// Own extension methods
fun <E : Enum<E>> JsonObject.addProperty(property: String, value: E) = addProperty(property, value.name)
inline fun <reified E : Enum<E>> JsonObject.hasEnum(element: String): Boolean {
    if (!hasString(element)) {
        return false
    }
    val name = getString(element)
    return enumValues<E>().any { e -> e.name == name }
}
inline fun <reified E : Enum<E>> JsonObject.asEnum(name: String): E = try {
    enumValueOf(getString(name))
}
catch (_: Exception) {
    throw JsonSyntaxException("Expected " + name + " to be an Enum, was " + getType())
}
inline fun <reified E : Enum<E>> JsonObject.asEnum(name: String, defaultValue: E): E = try {
    enumValueOf(getString(name))
} catch (_: Exception) {
    defaultValue
}

fun JsonObject.addProperty(property: String, value: StatModifier) {
    val obj = JsonObject()
    obj.addProperty("base", value.base)
    obj.addProperty("additive", value.additive)
    obj.addProperty("multiplicative", value.multiplicative)
    obj.addProperty("flat", value.flat)
    add(property, obj)
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

fun JsonObject.addProperty(property: String, value: Identifier) = addProperty(property, value.toString())
fun JsonObject.hasIdentifier(element: String): Boolean {
    if (!hasString(element)) {
        return false
    }
    return Identifier.tryParse(getString(element)) != null
}
fun JsonElement.asIdentifier(name: String): Identifier = try {
    Identifier(asString(name))
}
catch (_: Exception) {
    throw JsonSyntaxException("Expected " + name + " to be an Identifier, was " + getType())
}
fun JsonElement.asIdentifier(name: String, defaultIdentifier: Identifier): Identifier = try {
    Identifier(asString(name))
}
catch (_: Exception) {
    defaultIdentifier
}
fun JsonObject.getIdentifier(name: String): Identifier = try {
    Identifier(getString(name))
}
catch (_: Exception) {
    throw JsonSyntaxException("Expected " + name + " to be an Identifier, was " + getType())
}
fun JsonObject.getIdentifier(name: String, defaultValue: Identifier): Identifier = try {
    Identifier(getString(name))
}
catch (_: Exception) {
    defaultValue
}

// Most methods as extensions from net.minecraft.util.JsonHelper
fun JsonElement.isString(): Boolean = JsonHelper.isString(this)
fun JsonElement.isNumber(): Boolean = JsonHelper.isNumber(this)
fun JsonElement.isBoolean(): Boolean = JsonHelper.isBoolean(this)

fun JsonObject.hasString(element: String): Boolean = JsonHelper.hasString(this, element)
fun JsonObject.hasNumber(element: String): Boolean = JsonHelper.hasNumber(this, element)
fun JsonObject.hasBoolean(element: String): Boolean = JsonHelper.hasBoolean(this, element)
fun JsonObject.hasArray(element: String): Boolean = JsonHelper.hasArray(this, element)
fun JsonObject.hasJsonObject(element: String): Boolean = JsonHelper.hasJsonObject(this, element)
fun JsonObject.hasPrimitive(element: String): Boolean = JsonHelper.hasPrimitive(this, element)
fun JsonObject.hasElement(element: String): Boolean = JsonHelper.hasElement(this, element)

fun JsonElement.asString(name: String): String = JsonHelper.asString(this, name)
fun JsonElement.asItem(name: String): Item = JsonHelper.asItem(this, name)
fun JsonElement.asBoolean(name: String): Boolean = JsonHelper.asBoolean(this, name)
fun JsonElement.asDouble(name: String): Double = JsonHelper.asDouble(this, name)
fun JsonElement.asFloat(name: String): Float = JsonHelper.asFloat(this, name)
fun JsonElement.asLong(name: String): Long = JsonHelper.asLong(this, name)
fun JsonElement.asInt(name: String): Int = JsonHelper.asInt(this, name)
fun JsonElement.asByte(name: String): Byte = JsonHelper.asByte(this, name)
fun JsonElement.asChar(name: String): Char = JsonHelper.asChar(this, name)
fun JsonElement.asBigDecimal(name: String): BigDecimal = JsonHelper.asBigDecimal(this, name)
fun JsonElement.asBigInteger(name: String): BigInteger = JsonHelper.asBigInteger(this, name)
fun JsonElement.asShort(name: String): Short = JsonHelper.asShort(this, name)
fun JsonElement.asObject(name: String): JsonObject = JsonHelper.asObject(this, name)
fun JsonElement.asArray(name: String): JsonArray = JsonHelper.asArray(this, name)

fun JsonObject.getString(element: String): String = JsonHelper.getString(this, element)
fun JsonObject.getString(element: String, defaultStr: String): String? = JsonHelper.getString(this, element, defaultStr)
fun JsonObject.getItem(element: String): Item = JsonHelper.getItem(this, element)
fun JsonObject.getItem(element: String, defaultItem: Item): Item? = JsonHelper.getItem(this, element, defaultItem)
fun JsonObject.getBoolean(element: String): Boolean = JsonHelper.getBoolean(this, element)
fun JsonObject.getBoolean(element: String, defaultBoolean: Boolean): Boolean = JsonHelper.getBoolean(this, element, defaultBoolean)
fun JsonObject.getDouble(element: String): Double = JsonHelper.getDouble(this, element)
fun JsonObject.getDouble(element: String, defaultDouble: Double): Double = JsonHelper.getDouble(this, element, defaultDouble)
fun JsonObject.getFloat(element: String): Float = JsonHelper.getFloat(this, element)
fun JsonObject.getFloat(element: String, defaultFloat: Float): Float = JsonHelper.getFloat(this, element, defaultFloat)
fun JsonObject.getLong(element: String): Long = JsonHelper.getLong(this, element)
fun JsonObject.getLong(element: String, defaultLong: Long): Long = JsonHelper.getLong(this, element, defaultLong)
fun JsonObject.getInt(element: String): Int = JsonHelper.getInt(this, element)
fun JsonObject.getInt(element: String, defaultInt: Int): Int = JsonHelper.getInt(this, element, defaultInt)
fun JsonObject.getByte(element: String): Byte = JsonHelper.getByte(this, element)
fun JsonObject.getByte(element: String, defaultByte: Byte): Byte = JsonHelper.getByte(this, element, defaultByte)
fun JsonObject.getChar(element: String): Char = JsonHelper.getChar(this, element)
fun JsonObject.getChar(element: String, defaultChar: Char): Char = JsonHelper.getChar(this, element, defaultChar)
fun JsonObject.getBigDecimal(element: String): BigDecimal = JsonHelper.getBigDecimal(this, element)
fun JsonObject.getBigDecimal(element: String, defaultBigDecimal: BigDecimal): BigDecimal = JsonHelper.getBigDecimal(this, element, defaultBigDecimal)
fun JsonObject.getBigInteger(element: String): BigInteger = JsonHelper.getBigInteger(this, element)
fun JsonObject.getBigInteger(element: String, defaultBigInteger: BigInteger): BigInteger = JsonHelper.getBigInteger(this, element, defaultBigInteger)
fun JsonObject.getShort(element: String): Short = JsonHelper.getShort(this, element)
fun JsonObject.getShort(element: String, defaultShort: Short): Short = JsonHelper.getShort(this, element, defaultShort)
fun JsonObject.getObject(element: String): JsonObject = JsonHelper.getObject(this, element)
fun JsonObject.getObject(element: String, defaultObject: JsonObject): JsonObject? = JsonHelper.getObject(this, element, defaultObject)
fun JsonObject.getArray(element: String): JsonArray = JsonHelper.getArray(this, element)
fun JsonObject.getArray(element: String, defaultArray: JsonArray): JsonArray? = JsonHelper.getArray(this, element, defaultArray)

fun JsonElement.getType(): String = JsonHelper.getType(this)