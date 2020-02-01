package kr.co.seoft.two_min.data

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService{

    @GET("develop/v1/timeset/preset/hot/kr.json")
    fun getHotPreset() : Single<List<ApiTimeSet>>

    @GET("develop/v1/timeset/preset/list/kr.json")
    fun getBasicPreset() : Single<List<ApiTimeSet>>



    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://raw.githubusercontent.com/ChallengeProject/timer_api/")
                .build()

            return retrofit.create(ApiService::class.java) // need change line
        }
    }


}
