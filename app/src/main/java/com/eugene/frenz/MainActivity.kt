package com.eugene.frenz

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eugene.frenz.databinding.ActivityMainBinding
import com.eugene.frenz.model.Friend
import com.squareup.picasso.Picasso
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.sdk.api.friends.FriendsService
import com.vk.sdk.api.friends.dto.FriendsGetFieldsResponse
import com.vk.sdk.api.users.dto.UsersFields
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    var info: TextView? = null
    var main: LinearLayout? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        info = findViewById(R.id.info)
        main = findViewById(R.id.frgmCont)
        if (VK.isLoggedIn()) {
            fetchFriends()
        }
        else {
            VK.login(this, arrayListOf(VKScope.PHOTOS, VKScope.OFFLINE))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                fetchFriends()
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun fetchFriends() {
        val friendFields = arrayListOf(
            UsersFields.PHOTO_100,
            UsersFields.CITY,
            UsersFields.BDATE,
            UsersFields.COUNTRY
        )
        VK.execute(FriendsService().friendsGet(fields = friendFields), object:
            VKApiCallback<FriendsGetFieldsResponse> {
            override fun success(result: FriendsGetFieldsResponse) {
                for (friend in result.items) {
                    val friendLocation = (friend.country?.title  ?: "Unknown Country") + " - " + (friend.city?.title ?: "Unknown City")
                    addFriendToLayoutInGayWay(Friend(friend.firstName, friend.lastName, friend.photo100, friend.bdate, friendLocation))
                }
            }
            override fun fail(error: Exception) {
                Log.e("WHAT THE FUCK", error.toString())
            }
        })
    }

    fun addFriendToLayoutInGayWay(friend: Friend) {
        // This approach is gay, RecyclerView must be used for performance, but I don't have time for this kind of crap!

        val imageSize = 200

        val layoutVER = LinearLayout(this)
        layoutVER.orientation = LinearLayout.HORIZONTAL
        val image = CircleImageView(this)
        Picasso.get().load(friend.photo).into(image);

        image.minimumHeight = imageSize
        image.minimumWidth = imageSize
        image.setPadding(0,5,5,10)
        layoutVER.addView(image)
        val layoutHOR = LinearLayout(this)
        layoutHOR.orientation = LinearLayout.VERTICAL

        val first_last_name = TextView(this)
        first_last_name.setText(friend.fname + " " + friend.lname)
        first_last_name.textSize = 15f
        first_last_name.typeface = Typeface.DEFAULT_BOLD
        first_last_name.setPadding(0,10,10,10)

        layoutHOR.addView(first_last_name)
        val dboText = TextView(this)
        dboText.setText(friend.birthday)
        dboText.textSize = 15f
        dboText.setPadding(0,10,10,10)

        val city = TextView(this)
        city.setText(friend.location)
        city.textSize = 15f
        city.setPadding(0,10,10,10)

        layoutHOR.addView(dboText)
        layoutHOR.addView(city)
        layoutVER.addView(layoutHOR)
        main!!.addView(layoutVER)
    }
}
