package com.example.android.vocabulary.server;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Nadina on 18.04.2017.
 */

public interface ServerApi {
        @FormUrlEncoded
        @POST("/api/v1.5/tr.json/translate")
        Call<Object> translate(@FieldMap Map<String,String> map);
}
