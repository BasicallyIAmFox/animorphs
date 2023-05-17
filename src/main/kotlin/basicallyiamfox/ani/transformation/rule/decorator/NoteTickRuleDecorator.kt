package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.minecraft.block.enums.Instrument
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.apache.commons.lang3.math.Fraction
import java.awt.Color
import kotlin.math.*


class NoteTickRuleDecorator : RuleDecorator() {
    interface NoteTickPlayerEntity {
        var noteType: Int
        var noteTick: Int
    }

    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:note_tick")

        @JvmStatic
        fun colorByNoteType(noteType: Float): Color {
            val red = 0.0f.coerceAtLeast(sin(noteType * MathHelper.TAU) * 0.65f + 0.35f)
            val green = 0.0f.coerceAtLeast(sin((noteType + Fraction.getFraction(1, 3).toFloat()) * MathHelper.TAU) * 0.65f + 0.35f)
            val blue = 0.0f.coerceAtLeast(sin((noteType + Fraction.getFraction(2, 3).toFloat()) * MathHelper.TAU) * 0.65f + 0.35f)
            return Color(red, green, blue)
        }
    }

    init {
        id = ID
    }

    override fun update(world: World, player: PlayerEntity) {
        val duck = player as NoteTickPlayerEntity

        val amount = (ceil(abs((player.velocity.x + player.velocity.y + player.velocity.z) * 7.5f)) * 0.5 - 0.5).roundToInt()
        duck.noteTick += amount

        while (duck.noteTick >= 10) {
            val currentType: Int = duck.noteType
            val pitch = 2.0.pow((currentType - 12) / 12.0).toFloat()
            val instrument: Instrument = Instrument.fromBelowState(world.getBlockState(player.blockPos.down()))
            world.playSound(null, player.blockPos.up(), instrument.sound.value(), SoundCategory.RECORDS, 3f, pitch)

            world.addParticle(
                ParticleTypes.NOTE,
                true,
                player.x + 0.5,
                player.y + 2.2,
                player.z + 0.5,
                currentType / 24.0,
                0.0,
                0.0
            )

            duck.noteTick -= 10
            if (++duck.noteType > 24) {
                duck.noteType = 0
            }
        }
    }
}