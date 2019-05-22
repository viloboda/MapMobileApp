package model

import java.util.HashMap

enum class ObjectType(val id: Int) {
    None(0),
    House(1),
    City(2);

    companion object {
        val objectTypes: Map<Int, ObjectType>
            get() {
                val objectTypeValues = HashMap<Int, ObjectType>(values().size)
                for (gt in values()) {
                    objectTypeValues[gt.id] = gt
                }

                return objectTypeValues
            }
    }
}