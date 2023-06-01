package basicallyiamfox.ani.core.serializer

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier

class TypeSerializers {
    companion object {
        val key: RegistryKey<Registry<ISerializer<*>>> = RegistryKey.ofRegistry(Identifier("animorphs:type_serializer"))
        val registry: Registry<ISerializer<*>> = FabricRegistryBuilder.createSimple(key).buildAndRegister()

        @JvmStatic
        fun <T> getById(id: Identifier): ISerializer<T>? {
            if (registry.containsId(id)) {
                return registry.get(id) as ISerializer<T>
            }
            return null
        }

        fun <T : ISerializer<A>, A> register(id: Identifier, type: T): T {
            Registry.register(registry, id, type)
            return type
        }
    }
}