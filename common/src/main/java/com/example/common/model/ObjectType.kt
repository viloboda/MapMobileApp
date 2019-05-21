package com.example.common.model

import java.util.HashMap

enum class ObjectType private constructor(val id: Int) {
    None(0),
    House(1),
    Attraction(2);

    companion object {

        val objectTypes: Map<Int, ObjectType>
            get() {
                val objectTypeValues = HashMap<Int, ObjectType>(values().size)
                for (gt in values()) {
                    objectTypeValues.put(gt.id, gt)
                }

                return objectTypeValues
            }
    }
}