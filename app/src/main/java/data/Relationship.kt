// В файле: data/Relationship.kt

package com.example.lifesaga.data

data class Relationship(
    val personId: String, // <--- ИЗМЕНЕНИЕ: БЫЛО INT, СТАЛО STRING
    val relationshipMeter: Int
)
