package com.binus.catfeeder.API;

import com.binus.catfeeder.Data.VirtualPin;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface BlynkApi {

    // getAll?token={token}
    @GET("getAll")
    Call<VirtualPin> getVirtualPins(
            @Query("token") String token
    );

    @GET("update")
    Call<Void> sendDataToVirtualPins(@QueryMap Map<String, String> parameters);
}
