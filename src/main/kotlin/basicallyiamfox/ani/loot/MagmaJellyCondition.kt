package basicallyiamfox.ani.loot

import basicallyiamfox.ani.extensions.getDouble
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.condition.LootConditionType
import net.minecraft.loot.context.LootContext
import net.minecraft.util.JsonSerializer
import kotlin.math.pow

class MagmaJellyCondition(
    private val divider: Double,
    private val chance: Double
) : LootCondition {
    override fun test(t: LootContext?): Boolean {
        var divider: Double = divider
        if (t!!.random.nextFloat() < 0.3f) {
            divider /= t.random.nextBetween(10, 20).toDouble().pow(0.5) * 4
        } else {
            divider *= (t.random.nextFloat() * 0.5).pow(0.5)
        }
        return t.random.nextFloat() <= (chance / divider / 100.0).toFloat()
    }

    override fun getType(): LootConditionType {
        return AniConditionTypes.MAGMA_JELLY
    }

    class Builder : LootCondition.Builder {
        private var divider: Double = 1E+05
        private var chance: Double = 8184.0

        fun setDivider(value: Double): Builder {
            divider = value
            return this
        }

        fun setChance(value: Double): Builder {
            chance = value
            return this
        }

        override fun build(): LootCondition {
            return MagmaJellyCondition(
                divider,
                chance
            )
        }
    }
    class Serializer : JsonSerializer<MagmaJellyCondition> {
        override fun toJson(json: JsonObject?, condition: MagmaJellyCondition?, context: JsonSerializationContext?) {
            json!!.addProperty("chance", condition!!.chance)
            json.addProperty("divider", condition.divider)
        }

        override fun fromJson(json: JsonObject?, context: JsonDeserializationContext?): MagmaJellyCondition {
            val builder = Builder()
            builder.setChance(json!!.getDouble("chance"))
            builder.setDivider(json.getDouble("divider"))
            return builder.build() as MagmaJellyCondition
        }
    }
}