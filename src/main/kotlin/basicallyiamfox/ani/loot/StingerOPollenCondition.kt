package basicallyiamfox.ani.loot

import basicallyiamfox.ani.decorator.rule.BeeflyRuleDecorator.BeeflyPlayerEntity
import basicallyiamfox.ani.extensions.getDouble
import basicallyiamfox.ani.extensions.getFloat
import basicallyiamfox.ani.extensions.getInt
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.condition.LootConditionType
import net.minecraft.loot.context.LootContext
import net.minecraft.util.JsonSerializer

class StingerOPollenCondition(
    private val divideByXEveryYTicks: Int,
    private val defaultDivider: Double,
    private val divider: Int,
    private val undividedChance: Float,
    private val minDivider: Double,
    private val capChance: Double,
    private val compareMult: Double
) : LootCondition {

    override fun test(t: LootContext?): Boolean {
        val player = t!!.get(AniContextParameters.THIS_PLAYER)

        player as BeeflyPlayerEntity
        val hours = player.stingerTick / divideByXEveryYTicks
        var divider = defaultDivider / (this.divider * hours)
        if (divider < minDivider) {
            divider = minDivider
        }

        var chance = undividedChance / divider
        if (chance >= undividedChance * compareMult) {
            chance = capChance
        }
        return t.random.nextDouble() <= chance
    }

    override fun getType(): LootConditionType {
        return AniConditionTypes.STINGER_O_POLLEN
    }

    class Builder : LootCondition.Builder {
        private var divideByXEveryYTicks = 72000
        private var defaultDivider = 1E+09
        private var divider = 10
        private var undividedChance = 635.0F
        private var minDivider = 100.0
        private var capChance = 0.035
        private var compareMult = 1E-04

        fun setDivideByXEveryYTicks(value: Int): Builder {
            divideByXEveryYTicks = value
            return this
        }

        fun setDefaultDivider(value: Double): Builder {
            defaultDivider = value
            return this
        }

        fun setDivider(value: Int): Builder {
            divider = value
            return this
        }

        fun setUndividedChance(value: Float): Builder {
            undividedChance = value
            return this
        }

        fun setMinDivider(value: Double): Builder {
            minDivider = value
            return this
        }

        fun setCapChance(value: Double): Builder {
            capChance = value
            return this
        }

        fun setCompareMult(value: Double): Builder {
            compareMult = value
            return this
        }

        override fun build(): LootCondition {
            return StingerOPollenCondition(
                divideByXEveryYTicks,
                defaultDivider,
                divider,
                undividedChance,
                minDivider,
                capChance,
                compareMult
            )
        }
    }
    class Serializer : JsonSerializer<StingerOPollenCondition> {
        override fun toJson(json: JsonObject?, condition: StingerOPollenCondition?, context: JsonSerializationContext?) {
            json!!.addProperty("divide_by_number_every_x_ticks", condition!!.divideByXEveryYTicks)
            json.addProperty("default_divider", condition.defaultDivider)
            json.addProperty("divider", condition.divider)
            json.addProperty("undivided_chance", condition.undividedChance)
            json.addProperty("minimum_divider", condition.minDivider)
            json.addProperty("capped_chance", condition.capChance)
            json.addProperty("comparison_multiplier", condition.compareMult)
        }

        override fun fromJson(json: JsonObject?, context: JsonDeserializationContext?): StingerOPollenCondition {
            val builder = Builder()
            builder.setDivideByXEveryYTicks(json!!.getInt("divide_by_number_every_x_ticks"))
            builder.setDefaultDivider(json.getDouble("default_divider"))
            builder.setDivider(json.getInt("divider"))
            builder.setUndividedChance(json.getFloat("undivided_chance"))
            builder.setMinDivider(json.getDouble("minimum_divider"))
            builder.setCapChance(json.getDouble("capped_chance"))
            builder.setCompareMult(json.getDouble("comparison_multiplier"))
            return builder.build() as StingerOPollenCondition
        }
    }
}