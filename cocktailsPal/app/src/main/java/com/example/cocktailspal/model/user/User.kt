package com.example.cocktailspal.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User {
    @PrimaryKey
    var id: String? = ""
    var email: String? = ""
    var password: String? = ""
    var name: String? = ""
    var avatarUrl: String? = ""

    constructor()

    constructor(email: String?, password: String?, name: String?) {
        this.email = email
        this.password = password
        this.name = name
    }

    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }

    constructor(id: String?, email: String?, password: String?, name: String?, avatarUrl: String?) {
        this.id = id
        this.email = email
        this.password = password
        this.name = name
        this.avatarUrl = avatarUrl
    }

    constructor(id: String?, email: String?, name: String?, avatarUrl: String?) {
        this.id = id
        this.email = email
        this.name = name
        this.avatarUrl = avatarUrl
    }
}