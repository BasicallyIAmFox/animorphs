package basicallyiamfox.ani.decorator.rule

import basicallyiamfox.ani.AnimorphsKeybindings
import basicallyiamfox.ani.core.rule.RuleDecorator
import basicallyiamfox.ani.core.serializer.ISerializer
import basicallyiamfox.ani.extensions.*
import basicallyiamfox.ani.mixin.IEntityMixin
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class BeeflyRuleDecorator() : RuleDecorator() {
    object Serializer : ISerializer<BeeflyRuleDecorator> {
        override fun toJson(obj: JsonObject, type: BeeflyRuleDecorator) {
            obj.addProperty("id", type.id)
            obj.addProperty("min_speed", type.minSpeed)
            obj.addProperty("max_speed", type.maxSpeed)
            obj.addProperty("start_at", type.flyStart)
        }

        override fun fromJson(obj: JsonObject): BeeflyRuleDecorator {
            val inst = BeeflyRuleDecorator()
            inst.id = obj.getIdentifier("id")
            inst.minSpeed = obj.getFloat("min_speed")
            inst.maxSpeed = obj.getFloat("max_speed")
            inst.flyStart = obj.getInt("start_at")
            return inst
        }

        override fun toPacket(buf: PacketByteBuf, type: BeeflyRuleDecorator) {
            buf.writeIdentifier(type.id)
            buf.writeFloat(type.minSpeed)
            buf.writeFloat(type.maxSpeed)
            buf.writeShort(type.flyStart)
        }

        override fun fromPacket(buf: PacketByteBuf): BeeflyRuleDecorator {
            val inst = BeeflyRuleDecorator()
            inst.id = buf.readIdentifier()
            inst.minSpeed = buf.readFloat()
            inst.maxSpeed = buf.readFloat()
            inst.flyStart = buf.readShort().toInt()
            return inst
        }
    }
    interface BeeflyPlayerEntity {
        var stingerFly: Int
        var stingerTick: Int
    }
    class Packet : FabricPacket {
        companion object {
            val ID = Identifier("animorphs:bee")
            val TYPE: PacketType<Packet> = PacketType.create(ID) { Packet(it) }
        }

        val minSpeed: Float
        val maxSpeed: Float

        constructor(buf: PacketByteBuf) {
            minSpeed = buf.readFloat()
            maxSpeed = buf.readFloat()
        }
        constructor(minSpeed: Float, maxSpeed: Float) {
            this.minSpeed = minSpeed
            this.maxSpeed = maxSpeed
        }

        override fun write(buf: PacketByteBuf?) {
            buf!!.writeFloat(minSpeed)
            buf.writeFloat(maxSpeed)
        }

        override fun getType(): PacketType<*> {
            return TYPE
        }
    }

    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:beefly")

        fun fly(player: PlayerEntity, minSpeed: Float, maxSpeed: Float): Vec3d {
            var speed = minSpeed
            if (player.isSprinting) {
                speed = maxSpeed
            }
            val velocity = Vec3d(0.0, speed.toDouble(), 0.0)

            val newVelocity = Vec3d(player.velocity.x, player.velocity.y, player.velocity.z).add(velocity)
            player.velocity = newVelocity
            player.onLanding()
            return newVelocity
        }
    }

    var minSpeed: Float = 0f
    var maxSpeed: Float = 0f
    var flyStart: Int = 0

    constructor(minSpeed: Float, maxSpeed: Float, flyStart: Int) : this() {
        id = ID
        this.minSpeed = minSpeed
        this.maxSpeed = maxSpeed
        this.flyStart = flyStart
    }

    override fun update(world: World, player: PlayerEntity) {
        if (!world.isClient)
            return

        player as BeeflyPlayerEntity
        if (!AnimorphsKeybindings.beeflyKeyBinding!!.isPressed) {
            player.stingerFly = 0
        }
        else {
            if (player.stingerFly >= flyStart) {
                val newVelocity = fly(player, minSpeed, maxSpeed)
                ClientPlayNetworking.send(Packet(minSpeed, maxSpeed))

                if (!player.isInvisible && player.isVisualActive()) {
                    var left = Vec3d(player.x, player.y + 1.3, player.z)
                    var right = Vec3d(player.x, player.y + 1.3, player.z)
                    val leftInput = IEntityMixin.invokeMovementInputToVelocity(Vec3d(0.1, 0.0, -0.15), 1.1f, player.bodyYaw)
                    val rightInput = IEntityMixin.invokeMovementInputToVelocity(Vec3d(-0.1, 0.0, -0.15), 1.1f, player.bodyYaw)
                    left = left.add(leftInput)
                    right = right.add(rightInput)

                    val divider = 3
                    world.addParticle(
                        ParticleTypes.END_ROD,
                        left.x,
                        left.y,
                        left.z,
                        -newVelocity.x + leftInput.x / divider,
                        -newVelocity.y,
                        -newVelocity.z + leftInput.z / divider
                    )
                    world.addParticle(
                        ParticleTypes.END_ROD,
                        right.x,
                        right.y,
                        right.z,
                        -newVelocity.x + rightInput.x / divider,
                        -newVelocity.y,
                        -newVelocity.z + rightInput.z / divider
                    )
                }
            }
            else {
                player.stingerFly += 1
            }
        }
    }
}