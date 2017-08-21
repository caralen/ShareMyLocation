package hr.fer.ruazosa.sharemylocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created on 18.6.2017..
 */

public interface RestServiceClient {

    @GET("loginUser")
    Call<User> loginUser(
            @Query("userName") String userName,
            @Query("userPassword") String userPassword,
            @Query("token") String token
    );

    @POST("registerUser")
    Call<User> registerUser(
            @Body User user
    );

    @GET("resetPassword")
    Call<User> resetPassword(
            @Query("userName") String userName,
            @Query("userPassword") String userPassword
    );

    @POST("changeUser")
    Call<User> changeUser(
            @Body User user
    );

    @GET("changePassword")
    Call<User> changePassword(
            @Query("userID") long userID,
            @Query("password") String password
    );

    @POST("createGroup")
    Call<Group> createGroup(
            @Body Group group
    );

    @GET("getAllGroups")
    Call<List<Group>> getAllGroups(
    );

    @GET("getOtherGroups")
    Call<List<Group>> getOtherGroups(
            @Query("userID") long userID
    );

    @GET("removeFromGroup")
    Call<UserGroup> removeFromGroup(
            @Query("userID") long userID,
            @Query("groupID") long groupID
    );

    @GET("getUserGroups")
    Call<List<Group>> getUserGroups(
            @Query("userID") long userID
    );

    @GET("getUserForUserID")
    Call<User> getUserForUserID(
            @Query("id") long id
    );

    @POST("registerUserGroup")
    Call<UserGroup> registerUserGroup(
            @Body UserGroup userGroup
    );

    @POST("addMessageToGroup")
    Call<Message> addMessageToGroup(
            @Body Message message
    );

    @GET("getAllGroupMessages")
    Call<List<Message>> getAllGroupMessages(
            @Query("groupID") long groupID
    );

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://sharemylocation.mybluemix.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
