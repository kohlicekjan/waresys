package cz.kohlicek.waresys.service;

import java.util.List;

import cz.kohlicek.waresys.model.Account;
import cz.kohlicek.waresys.model.Device;
import cz.kohlicek.waresys.model.Item;
import cz.kohlicek.waresys.model.Tag;
import cz.kohlicek.waresys.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Rozhraní s popisující REST API serveru
 */
public interface WaresysService {

    //Account
    @GET("api/v1/account")
    Call<Account> getAccount();

    @FormUrlEncoded
    @PUT("api/v1/account/password")
    Call<Void> setPassword(@Field("oldPassword") String oldPassword, @Field("password") String password);

    //ITEM
    @GET("api/v1/items?limit=10")
    Call<List<Item>> getItems(@Query("search") String search, @Query("skip") Integer skip, @Query("sort") String order);

    @GET("api/v1/items/{id}")
    Call<Item> getItem(@Path("id") String itemId);

    @POST("api/v1/items")
    Call<Item> createItem(@Body Item item);

    @PUT("api/v1/items/{id}")
    Call<Item> updateItem(@Path("id") String itemId, @Body Item item);

    @DELETE("api/v1/items/{id}")
    Call<Void> deleteItem(@Path("id") String itemId);


    //TAG
    @GET("api/v1/tags?limit=10")
    Call<List<Tag>> getTags(@Query("skip") Integer skip, @Query("sort") String order);

    @GET("api/v1/tags/{id}")
    Call<Tag> getTag(@Path("id") String tagId);

    @GET("api/v1/tags/uid/{uid}")
    Call<Tag> getTagByUid(@Path("uid") String tagUid);

    @PUT("api/v1/tags/{id}")
    Call<Tag> updateTag(@Path("id") String tagId, @Body Tag tag);

    @DELETE("api/v1/tags/{id}")
    Call<Void> deleteTag(@Path("id") String tagId);


    //USER
    @GET("api/v1/users?limit=10")
    Call<List<User>> getUsers(@Query("search") String search, @Query("skip") Integer skip, @Query("sort") String order);

    @GET("api/v1/users/{id}")
    Call<User> getUser(@Path("id") String userId);

    @POST("api/v1/users")
    Call<User> createUser(@Body User user);

    @PUT("api/v1/users/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User user);

    @DELETE("api/v1/users/{id}")
    Call<Void> deleteUser(@Path("id") String userId);


    //DEVICE
    @GET("api/v1/devices?limit=10")
    Call<List<Device>> getDevices(@Query("search") String search, @Query("skip") Integer skip, @Query("sort") String order);

    @GET("api/v1/devices/{id}")
    Call<Device> getDevice(@Path("id") String deviceId);

    @PUT("api/v1/devices/{id}")
    Call<Device> updateDevice(@Path("id") String deviceId, @Body Device device);

    @DELETE("api/v1/devices/{id}")
    Call<Void> deleteDevice(@Path("id") String deviceId);

}
