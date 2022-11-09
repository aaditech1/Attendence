package com.example.facedetproject.UI;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ankai.coredvr.DvrMonitor;
import com.ankai.coredvr.IDvr;
import com.ankai.coredvr.MyDvr;
import com.ankai.coredvr.callback.OnCaptureMjpegCompletionCallback;
import com.ankai.coredvr.callback.OnConnectedCallback;
import com.ankai.coredvr.callback.OnDisconnectedCallback;
import com.ankai.coredvr.callback.OnInitializedCallback;
import com.example.facedetproject.Models.RegisterModels.RegisterRequestModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.facedetproject.Connection.API;
import com.example.facedetproject.MainActivity;
import com.example.facedetproject.Models.RegisterModels.RegisterResponseModel;
import com.example.facedetproject.Models.UserDetailsModel.UserDetailsResponseModel;
import com.example.facedetproject.R;
import com.example.facedetproject.Utils.AppPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register Dvr";
    ImageButton btnBack;
    MaterialButton btnRegister;
    private FrameLayout mPreviewLayout;
    private IDvr mDvr;
    private static final int REQUEST_CODE = 10000;
    private DvrMonitor mDvrMonitor;
    private SurfaceView mSurfaceView;
    EditText et_firstName, et_lastName, etAttendeeId;
        ImageView imgView;
    LinearLayout llLinear;
    String filename = "";
    ArrayList<String> idList = new ArrayList<>();
    private AppPrefManager appPrefManager;
    private final DvrMonitor.Listener mDvrMonitorListener = new DvrMonitor.Listener() {
        @Override
        public void atMounted(String dvrFilename) {
            if (mDvr != null && !mDvr.isConnected()) {
                mDvr.connect(dvrFilename);
            }
        }

        @Override
        public void onMounted(String dvrFilename) {
            if (mDvr != null && !mDvr.isConnected()) {
                mDvr.connect(dvrFilename);
            }
        }

        @Override
        public void onUnmounted(String dvrFilename) {
        }
    };

    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        destroyDvr();
        super.onPause();
    }

    @Override
    protected void onResume() {
        createDvr();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mPreviewLayout = findViewById(R.id.preview_layout);
        btnBack = findViewById(R.id.btnBack);
        btnRegister = findViewById(R.id.btnRegister);
        imgView = findViewById(R.id.imgView);
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        etAttendeeId = findViewById(R.id.etAttendeeId);
        llLinear = findViewById(R.id.llLinear);
        appPrefManager = new AppPrefManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                createDvr();
            }
        } else {
            createDvr();
        }

        callUserListApi();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceIdDialog();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllFilled()) {
                    if (!isDataRepeated()) {
                        File dir = Environment.getExternalStorageDirectory();
                        File target = new File(dir, "captures");
                        if (!target.exists())
                            target.mkdirs();
                        filename = target.getAbsolutePath() + "/" + et_firstName.getText().toString().trim() + " " + et_lastName.getText().toString() + ".jpg";
                        System.out.println("filename is " + filename);
                        mDvr.captureMjpeg(filename);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please enter a different attendee id", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    System.out.println("not filled");
                }
            }
        });
    }

    private boolean isDataRepeated() {
        boolean repeat = false;
        for (String fData : idList) {
            if (etAttendeeId.getText().toString().trim().equals(fData)) {
                repeat = true;
                break;
            }
        }
        return repeat;
    }

    private void callUserListApi() {
        Call<UserDetailsResponseModel> call = API.getClient(appPrefManager.getUrl()).getUserList();
        call.enqueue(new Callback<UserDetailsResponseModel>() {
            @Override
            public void onResponse(Call<UserDetailsResponseModel> call, Response<UserDetailsResponseModel> response) {
                if (response.body() != null) {
                    UserDetailsResponseModel respModel = response.body();
                    idList = respModel.getIds();
                } else {
                    System.out.println("got error msg from server");
                }
            }

            @Override
            public void onFailure(Call<UserDetailsResponseModel> call, Throwable t) {

            }
        });
    }

    private void showDeviceIdDialog() {
        Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.device_id_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView tvTitleDialog = dialog.findViewById(R.id.tvTitleDialog);
        TextInputEditText etDeviceId = dialog.findViewById(R.id.etDeviceId);
        TextInputEditText etPassword = dialog.findViewById(R.id.etPassword);
        TextInputLayout etIdLayout = dialog.findViewById(R.id.etIdLayout);
        TextInputLayout etUrlLayout = dialog.findViewById(R.id.etUrlLayout);
        etIdLayout.setVisibility(View.GONE);
        etDeviceId.setVisibility(View.GONE);
        etUrlLayout.setVisibility(View.GONE);
        tvTitleDialog.setText("Enter password to proceed");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPassword = (!etPassword.getText().toString().isEmpty()) ? etPassword.getText().toString() : "";
                System.out.println("entered password ko value: " + enteredPassword);
                if (enteredPassword.equals("")) {
                    tvTitleDialog.setText("Enter Password first");
                    tvTitleDialog.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    if (enteredPassword.equals(appPrefManager.getPassword())) {
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        dialog.dismiss();
                    } else {
                        tvTitleDialog.setText("Incorrect password!!!");
                        tvTitleDialog.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
            }
        });
        dialog.show();
    }

    private boolean isAllFilled() {
        return (!et_firstName.getText().toString().isEmpty() && !et_lastName.getText().toString().isEmpty() && !etAttendeeId.getText().toString().isEmpty());
    }

    @Override
    protected void onDestroy() {
        destroyDvr();
        super.onDestroy();
    }

    private void createDvr() {
        Log.i(TAG, "createDvr");
        if (mDvr == null) {
            mDvr = MyDvr.create();
            mDvr.setOnConnectedCallback(new OnConnectedCallback() {
                @Override
                public void onConnect(IDvr dvr) {
                    Log.i(TAG, "onConnect");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addSurfaceView();
                        }
                    });
                }
            });
            mDvr.setOnDisconnectedCallback(new OnDisconnectedCallback() {
                @Override
                public void onDisconnected(IDvr dvr) {
                    Log.i(TAG, "onDisconnected");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            removeSurfaceView();
                        }
                    });
                }
            });
            mDvr.setOnInitializedCallback(new OnInitializedCallback() {
                @Override
                public void onInitialized(IDvr dvr) {
                    Log.i(TAG, "onInitialized");
                }
            });

            /*Callback for image*/
            mDvr.setOnCaptureMjpegCompletionCallback(new OnCaptureMjpegCompletionCallback() {
                @Override
                public void onCompletion(IDvr iDvr, String s) {
                    System.out.println("inside video callback method");
                    File dir = Environment.getExternalStorageDirectory();
                    File target = new File(dir, "captures");
                    if (!target.exists())
                        target.mkdirs();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendFileToServer(target);
                        }
                    });

                }
            });
        }
        if (mDvrMonitor == null) {
            mDvrMonitor = new DvrMonitor(this, mDvrMonitorListener);
            mDvrMonitor.register();
        }
    }

    private void sendFileToServer(File target) {
        System.out.println("inside send files to server method");
        File[] files = target.listFiles();
        if (files.length > 0) {
            String newFilename = et_firstName.getText().toString().trim() + " " + et_lastName.getText().toString() + ".jpg";
            File uploadFile = new File(target, newFilename);
            Uri uri = Uri.fromFile(uploadFile);
            uploadFile = new File(uri.getPath());
            Bitmap myBitmap = BitmapFactory.decodeFile(uploadFile.getAbsolutePath());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImageData = Base64.encodeToString(byteArray, Base64.DEFAULT);
            RegisterRequestModel requestModel = new RegisterRequestModel();
            requestModel.setAttendee_name(et_firstName.getText().toString().trim() + " " + et_lastName.getText().toString().trim());
            requestModel.setAttendee_id(etAttendeeId.getText().toString().trim());
            requestModel.setRegistration_device(appPrefManager.getDeviceId());
            requestModel.setDepartment("General");
            requestModel.setImage_base64(encodedImageData);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(requestModel);
            System.out.println("json sent to server is " + jsonOutput);
            Call<RegisterResponseModel> call = API.getClient(appPrefManager.getUrl()).registerUser(requestModel);
            File finalUploadFile = uploadFile;
            System.out.println("api call ko request " + call.request());
            call.enqueue(new Callback<RegisterResponseModel>() {
                @Override
                public void onResponse(Call<RegisterResponseModel> call, Response<RegisterResponseModel> response) {
                    System.out.println("response code is " + response.code());
                    if (response.body() != null) {
                        RegisterResponseModel responseModel = response.body();
                        Toast.makeText(RegisterActivity.this, responseModel.getAcknowledge(), Toast.LENGTH_LONG).show();
                        idList.add(etAttendeeId.getText().toString().trim());
                        finalUploadFile.delete();
                        System.out.println("file deleted successfully" + responseModel.getAcknowledge());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Could not connect to the server", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponseModel> call, Throwable t) {
                    finalUploadFile.delete();
                    System.out.println("failure ko message is " + t.getMessage());
                    System.out.println("failure ko localised message is " + t.getLocalizedMessage());
                    System.out.println("failure ko cause is " + t.getCause());
                }
            });
        }
    }

    private void addSurfaceView() {
        Log.i(TAG, "addSurfaceView");
        if (mSurfaceView == null) {
            mSurfaceView = new SurfaceView(this);
            mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (mDvr != null)
                        mDvr.addSurface(holder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    // TODO Create Detection Live Here
                    Log.d("SURFACE UPDATED", "NOW");
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (mDvr != null)
                        mDvr.removeSurface(holder);
                }



            });
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            mSurfaceView.setLayoutParams(params);
            mPreviewLayout.addView(mSurfaceView);
        }
    }

    private void removeSurfaceView() {
        Log.i(TAG, "removeSurfaceView");
        if (mSurfaceView != null) {
            mPreviewLayout.removeView(mSurfaceView);
            mSurfaceView = null;
        }
    }


    private void destroyDvr() {
        Log.i(TAG, "destroyDvr");
        if (mDvrMonitor != null) {
            mDvrMonitor.unregister();
            mDvrMonitor = null;
        }
        if (mDvr != null) {
            mDvr.destroy();
            mDvr = null;
        }
    }
}