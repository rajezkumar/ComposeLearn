package com.raj.marvelcompose.model

import com.raj.marvelcompose.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.math.BigInteger
import java.security.MessageDigest

object APIService {
    private const val BASE_URL = "http://gateway.marvel.com/v1/public/"
    val api: MarvelApi = getRetrofit().create(MarvelApi::class.java)

    private fun getRetrofit(): Retrofit {
        val ts = System.currentTimeMillis().toString()
        val apiKeySecret = BuildConfig.MARVEL_SECRET
        val apiKey = BuildConfig.MARVEL_KEY
        val hash = getHash(ts, apiKeySecret, apiKey)

        val clientInterceptor = Interceptor { chain ->
            var request: Request = chain.request()
            val urlBuilder: HttpUrl.Builder = request.url.newBuilder()
                .addQueryParameter("ts", ts)
                .addQueryParameter("apiKey", apiKey)
                .addQueryParameter("hash", hash)
            val url: HttpUrl = urlBuilder.build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
        val okHttpClient: OkHttpClient =
            OkHttpClient.Builder().addInterceptor(clientInterceptor).build()
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

    }

    private fun getHash(ts: String, apiKeySecret: String, apiKey: String): String {
        val hashStr = ts + apiKeySecret + apiKey
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(hashStr.toByteArray())).toString(16).padStart(32, '0')

    }
}