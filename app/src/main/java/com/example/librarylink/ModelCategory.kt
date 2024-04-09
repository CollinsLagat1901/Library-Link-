package com.example.librarylink

class ModelCategory {

    //vaiarbles, must match as in firebase

    var id:String=""
    var category:String=""
    var timestamp:Long = 0
    var uid:String=""

    //empty condtructor, required by firebase
    constructor()
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }

    //parameter constructor
}