package com.example.facedetproject.Connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class API {
    private static Retrofit retrofit = null;
//    public static ApiService getClient() {
    public static ApiService getClient(String url) {

        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .client(HttpClientService.getUnsafeOkHttpClient())
//                    .baseUrl(BASE_URL)
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        ApiService api = retrofit.create(ApiService.class);
        return api; // return the APIInterface object
    }
}
