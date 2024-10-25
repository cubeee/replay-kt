package com.x7ff.parser.replay

import com.x7ff.parser.buffer.BitBuffer
import com.x7ff.parser.replay.ClassNetCacheProperty.Companion.readClassNetCacheProperties

data class ClassNetCache(
    val objectIndex: Long,
    val parentId: Long,
    val id: Long,
    val properties: Set<ClassNetCacheProperty>,

    var root: Boolean = false,
    var parent: ClassNetCache? = null,
    var children: MutableList<ClassNetCache> = mutableListOf(),
    var maxPropertyId: Int = -1
) {

    private fun allProperties(): Set<ClassNetCacheProperty> {
        val parentProperties = parent?.allProperties() ?: emptySet()
        return properties.union(parentProperties)
    }

    private fun maxPropertyId(): Int {
        return allProperties().maxBy { p -> p.id }.id
    }

    fun getProperty(id: Int): ClassNetCacheProperty? {
        val property = properties.firstOrNull { it.id == id }
        if (property == null && parent != null) {
            return parent?.getProperty(id)
        }
        return property
    }

    companion object {
        fun BitBuffer.readClassNetCaches(): List<ClassNetCache> {
            val length = getInt()

            val caches = mutableListOf<ClassNetCache>()
            for (i in 0 until length) {
                val objectId = getUInt()
                val parentId = getUInt()
                val id = getUInt()
                val properties = readClassNetCacheProperties()

                val cache = ClassNetCache(objectId, parentId, id, properties)

                for (j in i - 1 downTo 0) {
                    val prevCache = caches[j]
                    if (cache.parentId == prevCache.id) {
                        cache.parent = prevCache
                        prevCache.children.add(cache)
                        break
                    }
                }
                cache.root = cache.parent == null

                caches.add(cache)
            }
            return caches.toList()
        }

        fun List<ClassNetCache>.calculateMaxPropertyIds(): List<ClassNetCache> {
            return onEach { cache -> cache.maxPropertyId = cache.maxPropertyId() }
        }

        fun List<ClassNetCache>.fixClassParents(objectReferences: List<ObjectReference>): List<ClassNetCache> {
            val fixedParents = mapOf(
                "ProjectX.PRI_X" to "Engine.PlayerReplicationInfo",
                "TAGame.PRI_TA" to "ProjectX.PRI_X",
                "TAGame.CarComponent_Boost_TA" to "TAGame.CarComponent_TA",
                "TAGame.CarComponent_FlipCar_TA" to "TAGame.CarComponent_TA",
                "TAGame.CarComponent_Jump_TA" to "TAGame.CarComponent_TA",
                "TAGame.CarComponent_Dodge_TA" to "TAGame.CarComponent_TA",
                "TAGame.CarComponent_DoubleJump_TA" to "TAGame.CarComponent_TA",
                "TAGame.GameEvent_TA" to "Engine.Actor",
                "TAGame.SpecialPickup_TA" to "TAGame.CarComponent_TA",
                "TAGame.SpecialPickup_BallVelcro_TA" to "TAGame.SpecialPickup_TA",
                "TAGame.SpecialPickup_Targeted_TA" to "TAGame.SpecialPickup_TA",
                "TAGame.SpecialPickup_Spring_TA" to "TAGame.SpecialPickup_Targeted_TA",
                "TAGame.SpecialPickup_BallLasso_TA" to "TAGame.SpecialPickup_Spring_TA",
                "TAGame.SpecialPickup_BoostOverride_TA" to "TAGame.SpecialPickup_Targeted_TA",
                "TAGame.SpecialPickup_BallCarSpring_TA" to "TAGame.SpecialPickup_Spring_TA",
                "TAGame.SpecialPickup_BallFreeze_TA" to "TAGame.SpecialPickup_Targeted_TA",
                "TAGame.SpecialPickup_Swapper_TA" to "TAGame.SpecialPickup_Targeted_TA",
                "TAGame.SpecialPickup_GrapplingHook_TA" to "TAGame.SpecialPickup_Targeted_TA",
                "TAGame.SpecialPickup_BallGravity_TA" to "TAGame.SpecialPickup_TA",
                "TAGame.SpecialPickup_HitForce_TA" to "TAGame.SpecialPickup_TA",
                "TAGame.SpecialPickup_Tornado_TA" to "TAGame.SpecialPickup_TA",
                "Engine.TeamInfo" to "Engine.ReplicationInfo",
                "TAGame.Team_TA" to "Engine.TeamInfo"
            )
            for (fixedParent in fixedParents) {
                fixClassParent(objectReferences, fixedParent.key, fixedParent.value)
            }
            return this
        }

        private fun List<ClassNetCache>.fixClassParent(
            objectReferences: List<ObjectReference>,
            childClassName: String,
            parentClassName: String
        ) {
            val parentClass = firstOrNull { objectReferences[it.objectIndex.toInt()].name == parentClassName }
            val childClass = firstOrNull { objectReferences[it.objectIndex.toInt()].name == childClassName }
            if (parentClass != null && childClass != null && (childClass.parent == null || childClass.parent != parentClass)) {
                childClass.root = false
                if (childClass.parent != null) {
                    childClass.parent!!.children.remove(childClass)
                }
                childClass.parent = parentClass
                parentClass.children.add(childClass)
            }
        }
    }

    override fun toString(): String {
        return "ClassNetCache(objectIndex=$objectIndex, parentId=$parentId, id=$id, properties=$properties, root=$root, parent=$parent)"
    }

}
