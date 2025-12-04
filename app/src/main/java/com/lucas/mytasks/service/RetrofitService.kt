package com.lucas.mytasks.service

import com.lucas.mytasks.adapter.LocalDateAdapter
import com.lucas.mytasks.adapter.LocalTimeAdapter
import com.lucas.mytasks.repository.TaskRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalTime

class RetrofitService() {

    private val taskRepository: TaskRepository

    companion object {
        private const val BASE_URL = "http://172.17.16.1:8080"
    }

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(createClient())
                .addConverterFactory(createConverter())
                .build()

        taskRepository = retrofit.create(TaskRepository::class.java)
    }

    fun getTaskRepository() = taskRepository

    private fun createClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
    }

    private fun createConverter(): Converter.Factory {
        val gson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
                .create()

        return GsonConverterFactory.create(gson)
    }
}