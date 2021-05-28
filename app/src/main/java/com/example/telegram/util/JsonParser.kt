package com.example.telegram.util

import com.example.telegram.model.Friend
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonParser {

    fun parse(resultJson: String?): ArrayList<Friend>? {
        try {
            val allFriend: ArrayList<Friend> = ArrayList()
            val jObject = JSONObject(resultJson).getJSONObject("response")
            val jArray = jObject["items"] as JSONArray
            var fname: String?
            var lname: String?
            var photo: String?
            var city: String?
            city = null
            photo = city
            lname = photo
            fname = lname
            for (i in 0 until jArray.length()) {
                city = null
                photo = city
                lname = photo
                fname = lname
                val oneObject = jArray.getJSONObject(i)
                fname = oneObject.getString("first_name")
                lname = oneObject.getString("last_name")
                photo = oneObject.getString("photo_100")
                city = if (oneObject.has("city")) oneObject.getJSONObject("city").getString("title") else "Неизвестно"
                allFriend.add(Friend(fname, lname, photo, city))
            }
            return allFriend
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
        return null
    }

}
