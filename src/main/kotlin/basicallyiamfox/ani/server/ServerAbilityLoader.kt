package basicallyiamfox.ani.server

import basicallyiamfox.ani.json.JsonSerializer
import basicallyiamfox.ani.transformation.ability.Ability
import basicallyiamfox.ani.transformation.ability.AbilityManager
import com.google.common.collect.Maps
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.logging.LogUtils
import net.minecraft.resource.JsonDataLoader
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profiler

class ServerAbilityLoader : JsonDataLoader(GSON, "animorphs/abilities") {
    companion object {
        private val LOGGER = LogUtils.getLogger()
        private val GSON = GsonBuilder().create()
    }
    var manager: AbilityManager? = null
        private set

    override fun apply(prepared: MutableMap<Identifier, JsonElement>?, manager: ResourceManager?, profiler: Profiler?) {
        val transformationMap = Maps.newHashMap<Identifier, Ability>()

        prepared!!.forEach { (id, json) -> run {
            try {
                val obj2 = json.asJsonObject
                val obj = JsonSerializer.fromJson<Ability, JsonObject>(obj2)
                transformationMap[id] = obj
            } catch (exception: Exception) {
                LOGGER.error(
                    "Parsing error loading custom transformation ability {}: {}",
                    id,
                    exception.message as Any?
                )
            }
        } }

        this.manager = AbilityManager()
        this.manager!!.load(transformationMap)
    }

    fun get(): Iterable<Ability>? {
        return manager?.get()
    }

    fun get(id: Identifier): Ability? {
        return manager?.get(id)
    }
}