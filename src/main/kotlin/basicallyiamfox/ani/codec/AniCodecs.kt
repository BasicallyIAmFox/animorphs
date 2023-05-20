package basicallyiamfox.ani.codec

import basicallyiamfox.ani.extensions.enumValueOfOrNull
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

object AniCodecs {
    @JvmStatic
    inline fun <reified E : Enum<E>> enumCodec(): Codec<E> {
        return Codec.STRING.comapFlatMap({ id ->
            val e = enumValueOfOrNull<E>(id)
            if (e == null) {
                return@comapFlatMap DataResult.success(e)
            }
            return@comapFlatMap DataResult.error { "Not a valid enum value." }
        }, { it.name })
    }
}