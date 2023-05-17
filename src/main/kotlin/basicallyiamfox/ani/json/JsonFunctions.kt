package basicallyiamfox.ani.json

import basicallyiamfox.ani.util.Func2
import basicallyiamfox.ani.util.Func3
import net.minecraft.util.Identifier

open class JsonFunctions<TType, TReturn>(var serializer: Func2<TType, TReturn>, var deserializer: Func3<Identifier, TReturn, TType>)