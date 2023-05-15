package basicallyiamfox.ani.transformation

import basicallyiamfox.ani.packet.PacketSender
import net.minecraft.util.Identifier

class TransformationManager {
    companion object {
        init {
            PacketSender.addSender<TransformationManager> { inst, buf ->
                buf.writeShort(inst.map.count())
                inst.map.forEach { (t, u) ->
                    buf.writeIdentifier(t)
                    PacketSender.toPacket(u, buf)
                }
            }
            PacketSender.addReceiver { buf ->
                val manager = TransformationManager()
                val map = hashMapOf<Identifier, Transformation>()

                val count = buf.readShort().toInt()
                for (i in 0 until count) {
                    val id = buf.readIdentifier()
                    val e = PacketSender.fromPacket<Transformation>(buf)
                    map[id] = e
                }

                manager.load(map)
                return@addReceiver manager
            }
        }
    }

    private lateinit var map: MutableMap<Identifier, Transformation>

    fun load(map: Map<Identifier, Transformation>) {
        this.map = hashMapOf()
        map.forEach { (t, u) ->
            this.map[t] = u
        }
    }

    fun get(): Iterable<Transformation> {
        return map.values
    }

    fun get(id: Identifier): Transformation? {
        return map[id]
    }
}