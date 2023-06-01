package basicallyiamfox.ani.extensions

infix fun <T> T?.orIfNull(value: T): T {
    return when {
        this == null -> value
        else -> this
    }
}