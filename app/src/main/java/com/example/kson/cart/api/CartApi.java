package com.example.kson.cart.api;

import com.example.kson.cart.bean.CartEntity;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Author:kson
 * E-mail:19655910@qq.com
 * Time:2018/06/28
 * Description:
 */
public interface CartApi {
    @GET("product/getCarts")
    Observable<CartEntity> getData(@Query("uid") String uid);
}
