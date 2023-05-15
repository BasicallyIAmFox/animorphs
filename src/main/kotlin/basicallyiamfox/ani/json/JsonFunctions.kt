package basicallyiamfox.ani.json

import basicallyiamfox.ani.util.Action
import basicallyiamfox.ani.util.Func
import net.minecraft.util.Identifier

open class JsonFunctions<TType, TReturn>(var serializer: Action<out TType, TReturn>, var deserializer: Func<Identifier, TReturn, TType>)