package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.json.addProperty
import basicallyiamfox.ani.json.getIdentifier
import basicallyiamfox.ani.json.getInt
import com.google.gson.JsonObject
import net.minecraft.block.enums.Instrument
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.apache.commons.lang3.math.Fraction
import java.awt.Color
import kotlin.math.*

class NoteTickRuleDecorator() : RuleDecorator() {
    object Serializer : ISerializer<NoteTickRuleDecorator> {
        override fun toJson(obj: JsonObject, type: NoteTickRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("ticks_for_play", type.playEveryTick)
        }

        override fun fromJson(obj: JsonObject): NoteTickRuleDecorator {
            val inst = NoteTickRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.playEveryTick = obj.getInt("ticks_for_play")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: NoteTickRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeInt(type.playEveryTick)
        }

        override fun fromPacket(buf: PacketByteBuf): NoteTickRuleDecorator {
            val inst = NoteTickRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.playEveryTick = buf.readInt()
            return inst
        }
    }
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

    var playEveryTick: Int = 10

    constructor(playEveryTick: Int) : this() {
        id = ID
        this.playEveryTick = playEveryTick
    }

    override fun update(world: World, player: PlayerEntity) {
        val duck = player as NoteTickPlayerEntity

        val amount = (ceil(abs((player.velocity.x + player.velocity.y + player.velocity.z) * 7.5f)) * 0.5 - 0.5).roundToInt()
        duck.noteTick += amount

        while (duck.noteTick >= playEveryTick) {
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

            duck.noteTick -= playEveryTick
            if (++duck.noteType > 24) {
                duck.noteType = 0
            }
        }
    }
}