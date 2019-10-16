package model

enum class ObjectType(val id: Int) {
    None(0),
    House(1),
    City(2),

    Floor(10),
    FloorArea(11),
    FloorWall(12);
}