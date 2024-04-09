package com.example.librarylink

class ModelPdf {

    //variables
    var uid:String =""
    var id:String =""
    var title:String =""
    var description:String =""
    var categoryId:String =""
    var url:String =""
    var timestamp:Long =0
    var viewcount:Long =0
    var downloadsCount:Long =0

    //empty constructor
    constructor()

    // parameter constructor
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        viewcount: Long,
        downloadsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.viewcount = viewcount
        this.downloadsCount = downloadsCount
    }
}