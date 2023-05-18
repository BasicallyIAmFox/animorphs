/*
Taken and adapted from:
https://github.com/tModLoader/tModLoader/blob/1.4.4/patches/tModLoader/Terraria/ModLoader/StatModifier.cs
*/

package basicallyiamfox.ani.util

data class StatModifier(
    val base: Float,
    val additive: Float,
    val multiplicative: Float,
    val flat: Float
) {
    operator fun plus(value: Float): StatModifier = StatModifier(base, additive + value, multiplicative, flat)
    operator fun minus(value: Float): StatModifier = StatModifier(base, additive - value, multiplicative, flat)
    operator fun times(value: Float): StatModifier = StatModifier(base, additive, multiplicative * value, flat)
    operator fun div(value: Float): StatModifier = StatModifier(base, additive, multiplicative / value, flat)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatModifier

        if (base != other.base) return false
        if (additive != other.additive) return false
        if (multiplicative != other.multiplicative) return false
        if (flat != other.flat) return false

        return true
    }
    override fun hashCode(): Int {
        var hashCode = 1713062080
        hashCode = hashCode * -1521134295 + base.hashCode()
        hashCode = hashCode * -1521134295 + additive.hashCode()
        hashCode = hashCode * -1521134295 + multiplicative.hashCode()
        hashCode = hashCode * -1521134295 + flat.hashCode()
        return hashCode
    }

    fun applyTo(baseValue: Float): Float {
        return (baseValue + base) * additive * multiplicative + flat
    }
    fun combineWith(value: StatModifier): StatModifier {
        return StatModifier(
            base + value.base,
            additive + value.additive - 1,
            multiplicative * value.multiplicative,
            flat + value.flat
        )
    }
    fun scale(scale: Float): StatModifier {
        return StatModifier(
            base * scale,
            1 + (additive - 1) * scale,
            1 + (multiplicative - 1) * scale,
            flat * scale
        )
    }
}
