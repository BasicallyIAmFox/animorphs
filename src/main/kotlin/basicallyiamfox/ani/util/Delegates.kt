package basicallyiamfox.ani.util

fun interface Action2<TParameter0, TParameter1> {
    fun apply(parameter0: TParameter0, parameter1: TParameter1)
}
fun interface Func1<TParameter0, TReturnType> {
    fun apply(parameter0: TParameter0): TReturnType
}