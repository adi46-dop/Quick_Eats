package com.example.quickeats.model

data class CartItems(
    var foodName : String?= null,
    var foodImage : String?= null,
    var foodPrice : String?= null,
    var foodDesc : String?= null,
    var foodQuantity : Int?= null,
    var foodIngredient : String?= null
)
