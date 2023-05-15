package basicallyiamfox.ani.packet

import basicallyiamfox.ani.getAbilityLoader
import basicallyiamfox.ani.getAbilityManager
import basicallyiamfox.ani.getTransformationLoader
import basicallyiamfox.ani.transformation.TransformationManager
import basicallyiamfox.ani.transformation.ability.AbilityManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.Identifier

class Networking {
    companion object {
        @Environment(EnvType.CLIENT)
        class ThisClassExistsOnClientOnly {
            companion object {
                @JvmStatic
                var instance = ThisClassExistsOnClientOnly()
                    internal set
            }

            var transformations: TransformationManager? = null
            var abilities: AbilityManager? = null
        }

        private val sendTransformationsId: Identifier = Identifier("animorphs", "send_transformations")
        private val sendAbilitiesId: Identifier = Identifier("animorphs", "send_abilities")

        fun init() {
            ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
                val buffer = PacketByteBufs.create()
                PacketSender.toPacket(server.getTransformationLoader(), buffer)
                ServerPlayNetworking.send(handler.player, sendTransformationsId, buffer)
            }
            ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
                val buffer = PacketByteBufs.create()
                PacketSender.toPacket(server.getAbilityLoader(), buffer)
                ServerPlayNetworking.send(handler.player, sendAbilitiesId, buffer)
            }
        }

        fun clinit() {
            ClientPlayNetworking.registerGlobalReceiver(sendTransformationsId) { _, _, buf, _ ->
                ThisClassExistsOnClientOnly.instance.transformations = PacketSender.fromPacket(buf)
                println("yo transformations were loaded!!!")
            }
            ClientPlayNetworking.registerGlobalReceiver(sendAbilitiesId) { client, _, buf, _ ->
                ThisClassExistsOnClientOnly.instance.abilities = PacketSender.fromPacket(buf)
                println("yo abiliities were loaded!!!")
                println(client.getAbilityManager().get().first().id.toString())
            }

            ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
                ThisClassExistsOnClientOnly.instance = ThisClassExistsOnClientOnly()
            }
        }
    }
}