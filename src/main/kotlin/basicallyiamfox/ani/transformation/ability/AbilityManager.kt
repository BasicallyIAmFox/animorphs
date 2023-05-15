package basicallyiamfox.ani.transformation.ability

import basicallyiamfox.ani.packet.PacketSender
import net.minecraft.util.Identifier

class AbilityManager {
    companion object {
        init {
            PacketSender.addSender<AbilityManager> { inst, buf ->
                buf.writeShort(inst.map.count())
                inst.map.forEach { (t, u) ->
                    buf.writeIdentifier(t)
                    PacketSender.toPacket(u, buf)
                }
            }
            PacketSender.addReceiver { buf ->
                val manager = AbilityManager()
                val map = hashMapOf<Identifier, Ability>()

                val count = buf.readShort().toInt()
                for (i in 0 until count) {
                    val id = buf.readIdentifier()
                    val e = PacketSender.fromPacket<Ability>(buf)
                    map[id] = e
                }

                manager.load(map)
                return@addReceiver manager
            }
        }
    }

    private lateinit var map: MutableMap<Identifier, Ability>

    fun load(map: Map<Identifier, Ability>) {
        this.map = hashMapOf()
        map.forEach { (t, u) ->
            this.map[t] = u
        }
    }

    fun get(): Iterable<Ability> {
        return map.values
    }

    fun get(id: Identifier): Ability? {
        return map[id]
    }
}