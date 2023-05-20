package basicallyiamfox.ani.extensions

fun <T> T?.orIfNull(value: T): T {
    return when {
        this == null -> value
        else -> this
    }
}