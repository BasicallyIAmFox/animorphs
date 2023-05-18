package basicallyiamfox.ani.decorator.condition

import basicallyiamfox.ani.core.condition.ConditionDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.core.serializer.TypeSerializers
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import net.minecraft.world.World

object ConditionDecorators {
    private val andFunctions = register(AndConditionDecorator.ID, AndConditionDecorator.Serializer)
    private val orFunctions = register(OrConditionDecorator.ID, OrConditionDecorator.Serializer)
    private val notFunctions = register(NotConditionDecorator.ID, NotConditionDecorator.Serializer)
    private val inDimensionFunctions = register(InDimensionConditionDecorator.ID, InDimensionConditionDecorator.Serializer)
    private val duringTimeTicksFunctions = register(DuringTimeTicksConditionDecorator.ID, DuringTimeTicksConditionDecorator.Serializer)
    private val isDayFunctions = register(IsDayConditionDecorator.ID, IsDayConditionDecorator.Serializer)
    private val isNightFunctions = register(IsNightConditionDecorator.ID, IsNightConditionDecorator.Serializer)
    private val isSkyVisibleFunctions = register(IsSkyVisibleConditionDecorator.ID, IsSkyVisibleConditionDecorator.Serializer)
    private val lightLevelFunctions = register(LightLevelConditionDecorator.ID, LightLevelConditionDecorator.Serializer)
    private val biomeTemperatureFunctions = register(BiomeTemperatureConditionDecorator.ID, BiomeTemperatureConditionDecorator.Serializer)
    private val beingRainedOnFunctions = register(BeingRainedOnConditionDecorator.ID, BeingRainedOnConditionDecorator.Serializer)
    private val damagePlayerFunctions = register(DamagePlayerConditionDecorator.ID, DamagePlayerConditionDecorator.Serializer)
    private val touchingWaterOrRainOnFunctions = register(IsTouchingWaterOrRainConditionDecorator.ID, IsTouchingWaterOrRainConditionDecorator.Serializer)

    @JvmField val isSkyVisible = IsSkyVisibleConditionDecorator()
    @JvmField val isDay = IsDayConditionDecorator()
    @JvmField val isNight = IsNightConditionDecorator()
    @JvmField val duringDayTime = DuringTimeTicksConditionDecorator(0, 13000 - 1)
    @JvmField val duringNightTime = DuringTimeTicksConditionDecorator(13000, 24000 - 1)

    @JvmStatic
    fun inDimension(world: RegistryKey<World>): InDimensionConditionDecorator = InDimensionConditionDecorator(world)

    private fun <T : ISerializer<A>, A : ConditionDecorator> register(id: Identifier, serializer: T): T {
        TypeSerializers.register(id, serializer)
        return serializer
    }

    fun init() {}
}