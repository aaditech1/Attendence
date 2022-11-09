package com.example.facedetproject.Connection;

import static com.example.facedetproject.Constants.StringConstants.GET_USER_DETAILS;
import static com.example.facedetproject.Constants.StringConstants.REGISTER_URL;
import static com.example.facedetproject.Constants.StringConstants.VERIFY_URL;

import com.example.facedetproject.Models.AttendanceModels.AttendanceRequestModel;
import com.example.facedetproject.Models.AttendanceModels.AttendanceResponseModel;
import com.example.facedetproject.Models.RegisterModels.RegisterRequestModel;
import com.example.facedetproject.Models.RegisterModels.RegisterResponseModel;
import com.example.facedetproject.Models.UserDetailsModel.UserDetailsResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {


    @POST(REGISTER_URL)
    Call<RegisterResponseModel> registerUser (@Body RegisterRequestModel requestModel);


    @POST(VERIFY_URL)
    Call<AttendanceResponseModel> validateUser (@Body AttendanceRequestModel requestModel);

    @Multipart
    @POST(VERIFY_URL)
    Call<AttendanceResponseModel> validateUserMultipart (@Part MultipartBody.Part part, @Part("device") RequestBody id);

    @GET(GET_USER_DETAILS)
    Call<UserDetailsResponseModel> getUserList ();
}
