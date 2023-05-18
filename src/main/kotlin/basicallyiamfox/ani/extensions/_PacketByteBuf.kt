package basicallyiamfox.ani.extensions

import basicallyiamfox.ani.util.StatModifier
import net.minecraft.network.PacketByteBuf

fun PacketByteBuf.writeStatModifier(value: StatModifier) {
    writeFloat(value.base)
    writeFloat(value.additive)
    writeFloat(value.multiplicative)
    writeFloat(value.flat)
}
fun PacketByteBuf.readStatModifier(): StatModifier {
    val base = readFloat()
    val additive = readFloat()
    val multiplicative = readFloat()
    val flat = readFloat()
    return StatModifier(base, additive, multiplicative, flat)
}