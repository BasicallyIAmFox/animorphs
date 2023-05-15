package basicallyiamfox.ani.datagen

import com.google.common.collect.Sets
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.DataOutput
import net.minecraft.data.DataOutput.PathResolver
import net.minecraft.data.DataProvider
import net.minecraft.data.DataWriter
import net.minecraft.util.Identifier
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

abstract class FabricGenericProvider<T>(directoryName: String, output: FabricDataOutput) : DataProvider {
    private var pathResolver: PathResolver? = null

    init {
        pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, directoryName)
    }

    abstract fun generateTypes(consumer: Consumer<T?>?)

    override fun run(writer: DataWriter?): CompletableFuture<*> {
        val identifiers: MutableSet<Identifier> = Sets.newHashSet()
        val types: MutableSet<T> = Sets.newHashSet()

        generateTypes { e: T? -> types.add(e!!) }

        val futures: MutableList<CompletableFuture<*>> = ArrayList()

        for (type in types) {
            check(identifiers.add(getId(type))) { "Duplicate transformation " + getId(type) }
            futures.add(DataProvider.writeToPath(writer, toJson(type), getOutputPath(type)))
        }

        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    private fun getOutputPath(type: T): Path? {
        return pathResolver!!.resolveJson(getId(type))
    }

    abstract fun getId(type: T): Identifier
    abstract fun toJson(type: T): JsonObject
    abstract override fun getName(): String?
}