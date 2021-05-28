package com.example.telegram.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection

class HttpClient {

    var access_token = "94cd31ecb0316b7c111ef3f2123c79d1b9a539c910044011dcad24cb65554032c770ada97972fa32a0a61"

    fun HTTPRequest(): String? {
        val requestUrl2 =
            "https://api.vk.com/method/friends.get?v=5.52&fields=nickname,%20city,%20photo_100&access_token=$access_token" // поля какие выводить - читать в вк инсрукции
        var urlConn: URLConnection?
        var bufferedReader: BufferedReader?
        try {
            val url = URL(requestUrl2)
            urlConn = url.openConnection()
            bufferedReader = BufferedReader(InputStreamReader(urlConn.getInputStream()))
            val stringBuffer = StringBuffer()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            return stringBuffer.toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

}
