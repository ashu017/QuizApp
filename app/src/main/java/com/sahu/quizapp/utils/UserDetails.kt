package com.sahu.quizapp.utils

class UserDetails{
    private var Bio:String = ""
    private var Email:String = ""
    private var Name:String = ""
    private var Username:String = ""
    private var PhoneNo:String = ""
    private  var Image:String = ""
    private var UserId:String = ""

    constructor()

    constructor(Bio:String,Email:String,Name:String,Username:String,PhoneNo:String,Image:String){
        this.Bio = Bio
        this.Email = Email
        this.Name = Name
        this.Username = Username
        this.PhoneNo = PhoneNo
        this.Image = Image
        this.UserId = UserId
    }
    fun getName():String{
        return Name
    }
    fun setName(Name:String){
        this.Name = Name
    }
    fun getUsername():String{
        return Username
    }
    fun setUsername(Username:String){
        this.Username = Username
    }
    fun getPhoneNo():String{
        return PhoneNo
    }
    fun setPhoneNo(PhoneNo:String){
        this.PhoneNo = PhoneNo
    }
    fun getBio():String{
        return Bio
    }
    fun setBio(Bio:String){
        this.Bio = Bio
    }
    fun getUserId():String{
        return UserId
    }
    fun setUserId(UserId:String){
        this.UserId = UserId
    }
    fun getImage():String{
        return Image
    }
    fun setImage(Image:String){
        this.Image = Image
    }
    fun getEmail():String{
        return Email
    }
    fun setEmail(Email:String){
        this.Email = Email
    }
}