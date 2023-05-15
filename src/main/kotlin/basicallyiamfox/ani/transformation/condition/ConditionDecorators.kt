package basicallyiamfox.ani.transformation.condition

import basicallyiamfox.ani.json.JsonFunctions
import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.packet.PacketFunctions
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.condition.decorator.DuringTimeTicksConditionDecorator
import basicallyiamfox.ani.transformation.condition.decorator.InDimensionConditionDecorator
import basicallyiamfox.ani.util.Action
import basicallyiamfox.ani.util.Func
import com.google.gson.JsonObject
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.util.function.Function

class ConditionDecorators {
    companion object {
        val key = RegistryKey.ofRegistry<JsonFunctions<out ConditionDecorator, JsonObject>>(Identifier("animorphs:condition_decorator"))
        val registry = Registries.create(key) { null!! }
        val key2 = RegistryKey.ofRegistry<PacketFunctions<out ConditionDecorator>>(Identifier("animorphs:ability_decorator_packet"))
        val registry2 = Registries.create(key2) { null!! }

        private inline fun <reified T : ConditionDecorator> commonObj(): JsonFunctions<T, JsonObject> = object :
            JsonFunctions<T, JsonObject>(
                Action { inst, obj ->
                    obj.add("decorator", JsonSerializer.toJson<T, JsonObject>(inst))
                },
                Func { _, obj ->
                    return@Func JsonSerializer.fromJson<T, JsonObject>(obj)
                }
            ) { }
        private inline fun <reified T : ConditionDecorator> commonObj2(): PacketFunctions<T> = object :
            PacketFunctions<T>(
                Action { inst, buf ->
                    PacketSender.toPacket(inst, buf)
                },
                Function { buf ->
                    return@Function PacketSender.fromPacket<T>(buf)
                }
            ) { }

        private val statusEffect = register(
            Identifier("animorphs:in_dimension"),
            commonObj<InDimensionConditionDecorator>(),
            commonObj2()
        )
        private val duringTimeTicks = register(
            Identifier("animorphs:during_time_ticks"),
            commonObj<DuringTimeTicksConditionDecorator>(),
            commonObj2()
        )

        val duringDayTime = DuringTimeTicksConditionDecorator(0, 13000 - 1)
        val duringNightTime = DuringTimeTicksConditionDecorator(13000, 24000 - 1)

        fun inDimension(world: RegistryKey<World>): InDimensionConditionDecorator {
            return InDimensionConditionDecorator(world)
        }

        private fun <T : JsonFunctions<A, JsonObject>, A : ConditionDecorator> register(id: Identifier, jsonFunctions: T, packetFunctions: PacketFunctions<A>): T {
            Registry.register(registry, id, jsonFunctions)
            Registry.register(registry2, id, packetFunctions)
            return jsonFunctions
        }

        fun init() {
        }
    }
}