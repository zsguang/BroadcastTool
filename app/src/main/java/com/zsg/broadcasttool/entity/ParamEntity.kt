package com.zsg.broadcasttool.entity

data class ParamEntity(
    var type: ParamType = ParamType.STRING,
    var key: String = "",
    var value: String = "",
) {
    enum class ParamType {
        STRING,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOLEAN,
    }

    constructor(type: String, key: String, value: String) : this() {
        this.type = ParamType.valueOf(type)
        this.key = key
        this.value = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParamEntity

        if (type != other.type) return false
        if (key != other.key) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }


}
