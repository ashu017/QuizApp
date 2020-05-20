package com.sahu.quizapp.utils

class Posts {
    private var PostId:String = ""
    private var Description : String = ""
    private var Publisher : String = ""
    private var PostImage : String = ""
    constructor()
    constructor(PostId: String, Description: String, Publisher: String, PostImage: String) {
        this.PostId = PostId
        this.Description = Description
        this.Publisher = Publisher
        this.PostImage = PostImage
    }
    fun getPostId() : String{
        return PostId
    }
    fun getDescription() : String{
        return Description
    }
    fun getPostImage() : String{
        return PostImage
    }
    fun getPublisher() : String{
        return Publisher
    }
    fun setPostId(PostId: String){
        this.PostId = PostId
    }
    fun setPostImage(PostImage: String){
        this.PostImage = PostImage
    }
    fun setPublisher(Publisher: String){
        this.Publisher = Publisher
    }
    fun setDescription(Description: String){
        this.Description = Description
    }

}