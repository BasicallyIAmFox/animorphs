package basicallyiamfox.ani.packet

import basicallyiamfox.ani.cache.item.TooltipCache
import basicallyiamfox.ani.core.TransformationManager
import basicallyiamfox.ani.core.ability.AbilityManager
import basicallyiamfox.ani.decorator.rule.BeeflyRuleDecorator
import basicallyiamfox.ani.decorator.rule.MagmaticJumpRuleDecorator
import basicallyiamfox.ani.extensions.getAbilityLoader
import basicallyiamfox.ani.extensions.getTransformationLoader
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object Networking {
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
    class SendTransformationsPacket : FabricPacket {
        companion object {
            val TYPE: PacketType<SendTransformationsPacket> = PacketType.create(
                sendTransformationsId
            ) { SendTransformationsPacket(it) }
        }

        val loader: TransformationManager?

        constructor(buf: PacketByteBuf) : this(TransformationManager.Serializer.fromPacket(buf))
        constructor(loader: TransformationManager) {
            this.loader = loader
        }

        override fun write(buf: PacketByteBuf?) {
            TransformationManager.Serializer.toPacket(buf!!, loader!!)
        }

        override fun getType(): PacketType<*> {
            return TYPE
        }
    }
    class SendAbilityPacket : FabricPacket {
        companion object {
            val TYPE: PacketType<SendAbilityPacket> = PacketType.create(
                sendAbilitiesId
            ) { SendAbilityPacket(it) }
        }

        val loader: AbilityManager?

        constructor(buf: PacketByteBuf) : this(AbilityManager.Serializer.fromPacket(buf))
        constructor(loader: AbilityManager) {
            this.loader = loader
        }

        override fun write(buf: PacketByteBuf?) {
            AbilityManager.Serializer.toPacket(buf!!, loader!!)
        }

        override fun getType(): PacketType<*> {
            return TYPE
        }
    }

    private val sendTransformationsId: Identifier = Identifier("animorphs", "send_transformations")
    private val sendAbilitiesId: Identifier = Identifier("animorphs", "send_abilities")

    fun init() {
        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            val loader = server.getTransformationLoader().manager
            val packet = SendTransformationsPacket(loader!!)
            ServerPlayNetworking.send(handler.player, packet)
        }
        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            val loader = server.getAbilityLoader().manager
            val packet = SendAbilityPacket(loader!!)
            ServerPlayNetworking.send(handler.player, packet)
        }

        ServerPlayNetworking.registerGlobalReceiver(BeeflyRuleDecorator.Packet.TYPE) { packet, player, _ ->
            BeeflyRuleDecorator.fly(player, packet.minSpeed, packet.maxSpeed)
        }

        ServerPlayNetworking.registerGlobalReceiver(MagmaticJumpRuleDecorator.DamagePacket.TYPE) { _, player, _ ->
            MagmaticJumpRuleDecorator.damage(player)
        }
        ServerPlayNetworking.registerGlobalReceiver(MagmaticJumpRuleDecorator.JumpPacket.TYPE) { packet, player, _ ->
            MagmaticJumpRuleDecorator.jump(player, packet.magicScaleNumber)
        }
    }

    fun clinit() {
        ClientPlayNetworking.registerGlobalReceiver(SendTransformationsPacket.TYPE) { packet, _, _ ->
            ThisClassExistsOnClientOnly.instance.transformations = packet.loader
        }
        ClientPlayNetworking.registerGlobalReceiver(SendAbilityPacket.TYPE) { packet, _, _ ->
            ThisClassExistsOnClientOnly.instance.abilities = packet.loader
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            ThisClassExistsOnClientOnly.instance = ThisClassExistsOnClientOnly()
            TooltipCache.clear()
        }
    }
}