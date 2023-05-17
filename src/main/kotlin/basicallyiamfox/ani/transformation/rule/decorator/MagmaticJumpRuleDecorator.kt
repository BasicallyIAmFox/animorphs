package basicallyiamfox.ani.transformation.rule.decorator

import basicallyiamfox.ani.isMoving
import basicallyiamfox.ani.isVisualActive
import basicallyiamfox.ani.mixin.IEntityMixin
import basicallyiamfox.ani.mixin.ILivingEntityMixin
import basicallyiamfox.ani.transformation.rule.RuleDecorator
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class MagmaticJumpRuleDecorator() : RuleDecorator() {
    interface MagmaticJumpPlayerEntity {
        var magmaTick: Int
        var magmaDamage: Float
        var magmaScale: Float
    }
    class JumpPacket : FabricPacket {
        companion object {
            val ID = Identifier("animorphs:magma_jump")
            val TYPE: PacketType<JumpPacket> = PacketType.create(ID) { JumpPacket(it) }
        }

        val magicScaleNumber: Float

        constructor(buf: PacketByteBuf) {
            magicScaleNumber = buf.readFloat()
        }
        constructor(magicScaleNumber: Float) {
            this.magicScaleNumber = magicScaleNumber
        }

        override fun write(buf: PacketByteBuf?) {
            buf!!.writeFloat(magicScaleNumber)
        }

        override fun getType(): PacketType<*> {
            return TYPE
        }
    }
    class DamagePacket : FabricPacket {
        companion object {
            val ID = Identifier("animorphs:magma_jump_damage")
            val TYPE: PacketType<DamagePacket> = PacketType.create(ID) { DamagePacket() }
        }

        override fun write(buf: PacketByteBuf?) {
        }

        override fun getType(): PacketType<*> {
            return TYPE
        }
    }

    companion object {
        @JvmStatic
        val ID = Identifier("animorphs:magmatic_jump")

        const val magicScaleNumber = 2.2857144f

        fun damage(player: PlayerEntity) {
            val duck = player as MagmaticJumpPlayerEntity

            val pos = player.pos
            val box: Box = Box(pos, Vec3d.ofCenter(BlockPos(pos.getX().toInt(), pos.getY().toInt(), pos.getZ().toInt()))).expand(10.0)
            player.world.getEntitiesByClass(LivingEntity::class.java, box) { entity ->
                !entity.uuid.equals(player.uuid) && entity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) > 0.0 && entity.isAlive
            }.forEach { entity ->
                entity.damage(player.damageSources.lava(), duck.magmaDamage)
            }

            duck.magmaDamage = 0.0f
        }

        fun jump(player: PlayerEntity, magicScaleNumber: Float) {
            var jumpVelocity = player.jumpBoostVelocityModifier
            jumpVelocity += magicScaleNumber * (MagmaticJumpRuleDecorator.magicScaleNumber / 10.0f * 7.06f)
            (player as MagmaticJumpPlayerEntity).magmaDamage = MagmaticJumpRuleDecorator.magicScaleNumber * 2.06f;

            val d = (player as ILivingEntityMixin).invokeGetJumpVelocity().toDouble() + jumpVelocity
            val vec3d = player.velocity
            player.setVelocity(vec3d.x, d, vec3d.z)
            if (player.isSprinting) {
                val f = player.yaw * MathHelper.RADIANS_PER_DEGREE
                player.velocity = player.velocity.add(
                    (-MathHelper.sin(f) * 0.2f).toDouble(), 0.0,
                    (MathHelper.cos(f) * 0.2f).toDouble()
                )
            }
            player.velocityDirty = true
        }
    }

    var ready: Int = 0
    var max: Int = 0

    constructor(ready: Int, max: Int) : this() {
        id = ID
        this.ready = ready
        this.max = max
    }

    private fun getMagicScaleNumber(player: PlayerEntity): Float {
        return (player as MagmaticJumpPlayerEntity).magmaTick.toFloat() / max.toFloat() / magicScaleNumber
    }

    override fun update(world: World, player: PlayerEntity) {
        val duck = player as MagmaticJumpPlayerEntity
        val iDuck = player as IEntityMixin

        duck.magmaScale = getMagicScaleNumber(player)
        if (!player.isSneaking || player.isMoving() || player.isSpectator) {
            if (duck.magmaTick >= ready) {
                jump(player, duck.magmaScale)
                ClientPlayNetworking.send(JumpPacket(duck.magmaScale))
            }
            duck.magmaTick = 0
            iDuck.invokeCalculateDimensions()
        } else {
            if (duck.magmaTick >= ready) {
                var min = 0
                var max = 1
                var p = ParticleTypes.FLAME
                if (duck.magmaTick >= this.max - 1) {
                    if (duck.magmaTick == this.max - 1) {
                        min = 20
                        max = 30
                        duck.magmaTick = this.max
                        iDuck.invokeCalculateDimensions()
                        ClientPlayNetworking.send(JumpPacket(duck.magmaScale))
                    }
                    p = ParticleTypes.SOUL_FIRE_FLAME
                    min += 2
                } else {
                    duck.magmaTick += 1
                    iDuck.invokeCalculateDimensions()
                    ClientPlayNetworking.send(JumpPacket(duck.magmaScale))
                }
                if (world.isClient && !player.isInvisible && player.isVisualActive()) {
                    var pos = Vec3d(player.x, player.y, player.z)
                    for (i in min until max) {
                        pos = pos.add(
                            if (player.random.nextBoolean()) player.random.nextDouble() else -player.random.nextDouble(),
                            0.05,
                            if (player.random.nextBoolean()) player.random.nextDouble() else -player.random.nextDouble()
                        )
                        val input: Vec3d = Entity.movementInputToVelocity(
                            Vec3d(
                                (if (player.random.nextBoolean()) player.random.nextDouble() else -player.random.nextDouble()) / 10,
                                -player.random.nextDouble() / 15,
                                0.0
                            ), 1.1f, player.bodyYaw
                        )
                        world.addParticle(p, pos.x, pos.y + 0.05, pos.z, input.x, input.y, input.z)
                        pos = Vec3d(player.x, player.y, player.z)
                    }
                }
            } else {
                duck.magmaTick += 1
                iDuck.invokeCalculateDimensions()
                ClientPlayNetworking.send(JumpPacket(duck.magmaScale))
            }
        }

        if (duck.magmaDamage > 0.0f && !player.isMoving() && !player.isSpectator) {
            damage(player)

            duck.magmaDamage = 0.0f
            ClientPlayNetworking.send(DamagePacket())
        }
    }
}