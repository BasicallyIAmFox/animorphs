package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.addProperty
import basicallyiamfox.ani.extensions.getFloat
import basicallyiamfox.ani.extensions.getIdentifier
import com.google.gson.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.world.World

class PlaySoundRuleDecorator() : RuleDecorator() {
    object Serializer : ISerializer<PlaySoundRuleDecorator> {
        override fun toJson(obj: JsonObject, type: PlaySoundRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("value", type.soundId)
            obj.addProperty("volume", type.volume)
            obj.addProperty("pitch", type.pitch)
        }

        override fun fromJson(obj: JsonObject): PlaySoundRuleDecorator {
            val inst = PlaySoundRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.soundId = obj.getIdentifier("value")
            inst.volume = obj.getFloat("volume")
            inst.pitch = obj.getFloat("pitch")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: PlaySoundRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeIdentifier(type.soundId)
            buf.writeFloat(type.volume)
            buf.writeFloat(type.pitch)
        }

        override fun fromPacket(buf: PacketByteBuf): PlaySoundRuleDecorator {
            val inst = PlaySoundRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.soundId = buf.readIdentifier()
            inst.volume = buf.readFloat()
            inst.pitch = buf.readFloat()
            return inst
        }
    }
    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:play_sound")
    }

    lateinit var soundId: Identifier
    var volume = 0f
    var pitch = 0f

    constructor(soundEvent: SoundEvent, volume: Float, pitch: Float) : this() {
        id = ID
        soundId = soundEvent.id
        this.volume = volume
        this.pitch = pitch
    }

    override fun update(world: World, player: PlayerEntity) {
        player.playSound(SoundEvent.of(soundId), volume, pitch)
    }
}