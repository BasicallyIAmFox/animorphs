package basicallyiamfox.ani.server

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.transformation.Transformation
import basicallyiamfox.ani.transformation.TransformationManager
import com.google.common.collect.Maps
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.logging.LogUtils
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler

class ServerTransformationLoader : JsonDataLoader(GSON, "animorphs/transformations") {
    companion object {
        private val LOGGER = LogUtils.getLogger()
        private val GSON = GsonBuilder().create()
    }
    var manager: TransformationManager? = null
        private set

    override fun apply(prepared: MutableMap<Identifier, JsonElement>?, manager: ResourceManager?, profiler: Profiler?) {
        val transformationMap = Maps.newHashMap<Identifier, Transformation>()

        prepared!!.forEach { (id, json) -> run {
            try {
                val obj2 = json.asJsonObject
                val obj = JsonSerializer.fromJson<Transformation, JsonObject>(obj2)
                transformationMap[id] = obj
            } catch (exception: Exception) {
                LOGGER.error(
                    "Parsing error loading custom transformation {}: {}",
                    id,
                    exception.message as Any?
                )
            }
        } }

        this.manager = TransformationManager()
        this.manager!!.load(transformationMap)
    }

    fun get(): Iterable<Transformation>? {
        return manager?.get()
    }

    fun get(id: Identifier): Transformation? {
        return manager?.get(id)
    }
}