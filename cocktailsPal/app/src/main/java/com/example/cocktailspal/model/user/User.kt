package com.example.cocktailspal.model.user

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cocktailspal.MyApplication
import com.google.firebase.firestore.FieldValue
import java.sql.Timestamp

@Entity
class User {
    @PrimaryKey
    var id = ""
    var email: String? = ""
    var password: String? = ""
    var name: String? = ""
    var avatarUrl: String? = ""
    var lastUpdated: Long? = null

    constructor()
    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }

    constructor(id: String, email: String?, password: String?, name: String?, avatarUrl: String?) {
        this.id = id
        this.email = email
        this.password = password
        this.name = name
        this.avatarUrl = avatarUrl
    }

    fun toJson(): Map<String, Any?> {
        val json: MutableMap<String, Any?> = HashMap()
        json[ID] = id
        json[NAME] = name
        json[AVATAR] = avatarUrl
        json[LAST_UPDATED] = FieldValue.serverTimestamp()
        return json
    }

    companion object {
        const val NAME = "name"
        const val ID = "id"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val AVATAR = "avatar"
        const val COLLECTION = "users"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "users_local_last_update"
        fun fromJson(json: Map<String?, Any?>): User {
            val id = json[ID] as String?
            val email = json[EMAIL] as String?
            val password = json[PASSWORD] as String?
            val name = json[NAME] as String?
            val avatar = json[AVATAR] as String?
            val user = User(
                id!!, name, avatar, email, password
            )
            try {
                val time: Timestamp? = json[LAST_UPDATED] as Timestamp?
                user.lastUpdated = time?.getSeconds()?.toLong()
            } catch (e: Exception) {
            }
            return user
        }

        var localLastUpdate: Long?
            get() {
                var sharedPref: SharedPreferences? = MyApplication.Companion.myContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE);
                return sharedPref?.getLong(LOCAL_LAST_UPDATED, 0)
            }
            set(time) {
                val sharedPref: SharedPreferences? = MyApplication.Companion.myContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE);
                val editor: SharedPreferences.Editor = sharedPref!!.edit()
                if (time != null) {
                    editor.putLong(LOCAL_LAST_UPDATED, time)
                }
                editor.commit()
            }
    }
}
