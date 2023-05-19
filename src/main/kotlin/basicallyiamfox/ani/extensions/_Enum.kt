package basicallyiamfox.ani.extensions

inline fun <reified E : Enum<E>> enumValueOfOrNull(name: String): E? {
    return enumValues<E>().find { it.name == name }
}