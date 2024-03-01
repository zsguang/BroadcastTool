package com.zsg.broadcasttool.entity

data class BroadcastEntity(
    var action: String = "",
    val packageName: String = "",
    val params: MutableList<ParamEntity> = mutableListOf(),
//    var isFold: Boolean = true,
) {

    fun addParam(param: ParamEntity) {
        params.add(param)
    }

    fun removeParam(param: ParamEntity) {
        params.remove(param)
    }

    fun clearParams() {
        params.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BroadcastEntity

        if (action != other.action) return false
        if (packageName != other.packageName) return false
        if (params.size != other.params.size) return false
        for (i in params.indices) {
            if (params[i] != other.params[i]) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = action.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + params.hashCode()
        return result
    }


}