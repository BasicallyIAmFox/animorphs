package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.isVisualActive
import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.lwjgl.glfw.GLFW


class BeeflyRuleDecorator() : RuleDecorator() {
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

        val duck = player as BeeflyPlayerEntity
        val client = MinecraftClient.getInstance()
        if (!InputUtil.isKeyPressed(client.window.handle, GLFW.GLFW_KEY_SPACE)) {
            duck.stingerFly = 0
        }
        else {
            if (duck.stingerFly >= flyStart) {
                var speed: Float = minSpeed
                if (player.isSprinting) {
                    speed = maxSpeed
                }
                val velocity = Vec3d(0.0, speed.toDouble(), 0.0)

                val newVelocity = Vec3d(player.velocity.x, player.velocity.y, player.velocity.z).add(velocity)
                player.velocity = newVelocity
                player.onLanding()
                ClientPlayNetworking.send(Packet(minSpeed, maxSpeed))

                if (client.world != null && !player.isInvisible && player.isVisualActive()) {
                    var left = Vec3d(player.x, player.y + 1.3, player.z)
                    var right = Vec3d(player.x, player.y + 1.3, player.z)
                    val leftInput: Vec3d = Entity.movementInputToVelocity(Vec3d(0.1, 0.0, -0.15), 1.1f, player.bodyYaw)
                    val rightInput: Vec3d = Entity.movementInputToVelocity(Vec3d(-0.1, 0.0, -0.15), 1.1f, player.bodyYaw)
                    left = left.add(leftInput)
                    right = right.add(rightInput)

                    val divider = 3
                    client.world!!.addParticle(
                        ParticleTypes.END_ROD,
                        left.x,
                        left.y,
                        left.z,
                        -newVelocity.x + leftInput.x / divider,
                        -newVelocity.y,
                        -newVelocity.z + leftInput.z / divider
                    )
                    client.world!!.addParticle(
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
                duck.stingerFly += 1
            }
        }
    }
}