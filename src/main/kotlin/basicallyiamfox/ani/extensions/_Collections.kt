package basicallyiamfox.ani.extensions

import com.google.gson.JsonArray
import com.google.gson.JsonElement

fun <TType : MutableCollection<TValue>, TValue> TType.addSelf(value: TValue): TType {
    this.add(value)
    return this
}

fun Iterable<JsonElement>.toJsonArray(): JsonArray {
    val array = JsonArray()
    forEach {
        array.add(it)
    }
    return array
}