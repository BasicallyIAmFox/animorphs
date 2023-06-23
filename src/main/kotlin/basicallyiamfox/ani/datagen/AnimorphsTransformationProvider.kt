package basicallyiamfox.ani.datagen

import basicallyiamfox.ani.core.Transformation
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.util.Identifier
import java.util.function.Consumer

abstract class AnimorphsTransformationProvider(output: FabricDataOutput) : FabricGenericProvider<Transformation>("animorphs/transformations", output) {
    abstract fun generateTransformations(consumer: Consumer<Transformation?>)

    override fun generateTypes(consumer: Consumer<Transformation?>?) = generateTransformations(consumer!!)
    override fun getId(type: Transformation): Identifier = type.id
    override fun toJson(type: Transformation): JsonObject {
        val obj = JsonObject()
        Transformation.Serializer.toJson(obj, type)
        return obj
    }
    override fun getName(): String? = "Animorphs/Transformations"
}
