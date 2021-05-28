package com.example.telegram

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.example.telegram.databinding.ActivityMainBinding
import com.example.telegram.model.Friend
import com.example.telegram.util.HttpClient
import com.example.telegram.util.JsonParser
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mDrawer: Drawer
    private lateinit var mHeader: AccountHeader
    private lateinit var mToolbar: Toolbar

    var info: TextView? = null
    var main: LinearLayout? = null
    var allFriend: ArrayList<Friend> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        info = findViewById(R.id.info)
        main = findViewById(R.id.frgmCont)


        if (isOnline(this)) { // проверка подключения интернета
            val thread = Thread(
                null, doBackgroundThreadProcessing,
                "Background"
            ) // Здесь трудоемкие задачи переносятся в дочерний поток.
            thread.start()
        } else {
            Toast.makeText(
                applicationContext,
                "Нет соединения с интернетом!", Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        initFields()
        initFunc()
    }

    private fun initFunc() {
        setSupportActionBar(mToolbar)
        createHeader()
        createDrawer()
    }

    private fun createDrawer() {
        mDrawer = DrawerBuilder()
            .withActivity(this)
            .withToolbar(mToolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(mHeader)
            .addDrawerItems(
               PrimaryDrawerItem().withIdentifier(100)
                   .withIconTintingEnabled(true)
                   .withName("Создать группу")
                   .withSelectable(false)
                   .withIcon(R.drawable.ic_menu_create_groups),
                PrimaryDrawerItem().withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withName("Создать секретный чат")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_secret_chat),
                PrimaryDrawerItem().withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withName("Создать канал")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_create_channel),
                PrimaryDrawerItem().withIdentifier(103)
                    .withIconTintingEnabled(true)
                    .withName("Контакты")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_contacts),
                PrimaryDrawerItem().withIdentifier(104)
                    .withIconTintingEnabled(true)
                    .withName("Звонки")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_phone),
                PrimaryDrawerItem().withIdentifier(105)
                    .withIconTintingEnabled(true)
                    .withName("Избранное")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_favorites),
                PrimaryDrawerItem().withIdentifier(106)
                    .withIconTintingEnabled(true)
                    .withName("Настройки")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_settings),
                PrimaryDrawerItem().withIdentifier(107)
                    .withIconTintingEnabled(true)
                    .withName("Создать группу")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_create_groups),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(108)
                    .withIconTintingEnabled(true)
                    .withName("Пригласить друзей")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_invate),
                PrimaryDrawerItem().withIdentifier(109)
                    .withIconTintingEnabled(true)
                    .withName("Вопросы о телеграм")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_help)
            ).withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener{
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                   Toast.makeText(applicationContext,position.toString(),Toast.LENGTH_SHORT).show()
                    return false
                }
            }).build()

    }

    private fun createHeader() {
        mHeader = AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                ProfileDrawerItem().withName("Yura Petrov")
                    .withEmail("+7911111111")
            ).build()
    }

    private fun initFields() {
        mToolbar = mBinding.mainToolbar
    }

    private val doBackgroundThreadProcessing =
        Runnable { // Метод, который выполняет какие-то действия в фоновом режиме.
            val newrequest = HttpClient()
            val newjson = JsonParser()
            allFriend = newjson.parse(newrequest.HTTPRequest())!!
            main!!.post {
                for (i in allFriend.indices) {
                    newRecord(i)
                }
            }
        }

    @SuppressLint("SetTextI18n")
    fun newRecord(i: Int?) {
        val layoutVER = LinearLayout(this)
        layoutVER.orientation = LinearLayout.HORIZONTAL
        val image = CircleImageView(this)
        DownloadImageTask(image).execute(allFriend[i!!].photo) // Показать картинку
        image.minimumHeight = 250
        image.minimumWidth = 250
        image.setPadding(0,5,5,10)
        layoutVER.addView(image)
        val layoutHOR = LinearLayout(this)
        layoutHOR.orientation = LinearLayout.VERTICAL
//        layoutHOR.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)

        val first_last_name = TextView(this)
        first_last_name.setText(allFriend[i].fname + " " + allFriend[i].lname)
        first_last_name.textSize = 15f
        first_last_name.setPadding(0,20,10,10)

        layoutHOR.addView(first_last_name)
        val city = TextView(this)
        city.setText(allFriend[i].city)
        city.textSize = 15f
        city.setPadding(0,20,10,10)

        layoutHOR.addView(city)
        layoutVER.addView(layoutHOR)
        main!!.addView(layoutVER)
    }




    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    inner class DownloadImageTask(var bmImage: ImageView) :  AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg urls: String?): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
        }
    }
}
