package basicallyiamfox.ani.transformation.condition

import basicallyiamfox.ani.json.JsonFunctions
import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.packet.PacketFunctions
import basicallyiamfox.ani.packet.PacketSender
import basicallyiamfox.ani.transformation.condition.decorator.*
import basicallyiamfox.ani.util.Action
import basicallyiamfox.ani.util.Func2
import basicallyiamfox.ani.util.Func3
import com.google.gson.JsonObject
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.util.function.Function

class ConditionDecorators {
    companion object {
        val key: RegistryKey<Registry<JsonFunctions<out ConditionDecorator, JsonObject>>> = RegistryKey.ofRegistry(Identifier("animorphs:condition_decorator"))
        val registry: Registry<JsonFunctions<out ConditionDecorator, JsonObject>> = Registries.create(key) { null!! }
        val key2: RegistryKey<Registry<PacketFunctions<out ConditionDecorator>>> = RegistryKey.ofRegistry(Identifier("animorphs:ability_decorator_packet"))
        val registry2: Registry<PacketFunctions<out ConditionDecorator>> = Registries.create(key2) { null!! }

        private inline fun <reified T : ConditionDecorator> commonObj(): JsonFunctions<T, JsonObject> = object :
            JsonFunctions<T, JsonObject>(
                Func2 { inst ->
                    return@Func2 JsonSerializer.toJson<T, JsonObject>(inst)
                },
                Func3 { _, obj ->
                    return@Func3 JsonSerializer.fromJson<T, JsonObject>(obj)
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

        private val anyFunctions = register(AndConditionDecorator.ID, commonObj<AndConditionDecorator>(), commonObj2())
        private val orFunctions = register(OrConditionDecorator.ID, commonObj<OrConditionDecorator>(), commonObj2())
        private val inDimensionFunctions = register(InDimensionConditionDecorator.ID, commonObj<InDimensionConditionDecorator>(), commonObj2())
        private val duringTimeTicksFunctions = register(DuringTimeTicksConditionDecorator.ID, commonObj<DuringTimeTicksConditionDecorator>(), commonObj2())
        private val isDayFunctions = register(IsDayConditionDecorator.ID, commonObj<IsDayConditionDecorator>(), commonObj2())
        private val isNightFunctions = register(IsNightConditionDecorator.ID, commonObj<IsNightConditionDecorator>(), commonObj2())
        private val isSkyVisibleFunctions = register(IsSkyVisibleConditionDecorator.ID, commonObj<IsSkyVisibleConditionDecorator>(), commonObj2())
        private val lightLevelFunctions = register(LightLevelConditionDecorator.ID, commonObj<LightLevelConditionDecorator>(), commonObj2())
        private val biomeTemperatureFunctions = register(BiomeTemperatureConditionDecorator.ID, commonObj<BiomeTemperatureConditionDecorator>(), commonObj2())

        val isSkyVisible = IsSkyVisibleConditionDecorator()
        val isDay = IsDayConditionDecorator()
        val isNight = IsNightConditionDecorator()
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