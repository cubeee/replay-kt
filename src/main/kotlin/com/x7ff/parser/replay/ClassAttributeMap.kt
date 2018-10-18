package com.x7ff.parser.replay

data class ClassAttributeMap (
    val nameMap: Map<Long, String>,
    val classNetCacheByName: Map<String, ClassNetCache>
) {
    fun getName(nameIndex: Long): String? = nameMap[nameIndex]

    fun getClassNetCacheByName(className: String): ClassNetCache? {
        val key = when {
            className.contains("CrowdActor_TA") -> "TAGame.CrowdActor_TA"
            className.contains("VehiclePickup_Boost_TA") -> "TAGame.VehiclePickup_Boost_TA"
            className.contains("CrowdActor_TA") -> "TAGame.CrowdActor_TA"
            className.contains("CrowdManager_TA") -> "TAGame.CrowdManager_TA"
            className.contains("BreakOutActor_Platform_TA") -> "TAGame.BreakOutActor_Platform_TA"
            className.contains("InMapScoreboard_TA") -> "TAGame.InMapScoreboard_TA"
            else -> className
        }
        return classNetCacheByName[key]
    }

    companion object {
        fun createClassAttributeMap(
            caches: List<ClassNetCache>,
            names: List<Name>,
            objectReferences: List<ObjectReference>
        ): ClassAttributeMap {
            val nameMap = makeNameMap(names)
            val classNetByCache = makeClassNetCacheByName(caches, objectReferences)

            return ClassAttributeMap(nameMap, classNetByCache)
        }

        private fun makeClassNetCacheByName(caches: List<ClassNetCache>, objectReferences: List<ObjectReference>)
                = caches.map { objectReferences[it.objectIndex.toInt()].name to it }.toMap()

        private fun makeNameMap(names: List<Name>): Map<Long, String> =
            names.map { name ->
                name.index to name.name
            }.toMap()

    }

}