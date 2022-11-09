package com.example.facedetproject.UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ankai.coredvr.DvrMonitor;
import com.ankai.coredvr.IDvr;
import com.ankai.coredvr.MyDvr;
import com.ankai.coredvr.callback.OnCaptureMjpegCompletionCallback;
import com.ankai.coredvr.callback.OnConnectedCallback;
import com.ankai.coredvr.callback.OnDisconnectedCallback;
import com.ankai.coredvr.callback.OnInitializedCallback;
import com.example.facedetproject.Adapter.ReportViewAdapter;
import com.example.facedetproject.Connection.API;
import com.example.facedetproject.MainActivity;
import com.example.facedetproject.Models.AttendanceModels.AcknowledgeClass;
import com.example.facedetproject.Models.AttendanceModels.AttendanceResponseModel;
import com.example.facedetproject.R;
import com.example.facedetproject.Utils.AppPrefManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity {
    private static final String TAG = "Attendance Activity";
    TextureView textureView;
    RecyclerView rvReports;
    ImageView btnBackAttendance;
    private AppPrefManager appPrefManager;
    CascadeClassifier cascadeClassifier;
    String filename = "";
    ReportViewAdapter adapteASDr;

    /*For dvr*/
    private IDvr mDvr;
    private DvrMonitor mDvrMonitor;
    private SurfaceView mSurfaceView;
    long startTime;
    private Mat rgbaMat = null;
    File caseFile;
    private boolean isReadyForDetection = true;

    private DvrMonitor.Listener mDvrMonitorListener = new DvrMonitor.Listener() {
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
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        createDvr();
    }

    @Override
    protected void onDestroy() {
        destroyDvr();
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        textureView = findViewById(R.id.textureView);
        rvReports = findViewById(R.id.rvReports);
        btnBackAttendance = findViewById(R.id.btnBackAttendance);
        appPrefManager = new AppPrefManager(this);

        btnBackAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeviceIdDialog();
            }
        });

        createDvr();


        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, callBack);
            System.out.println("if ma xiryo");
        } else {
            try {
                if (callBack != null) {
                    callBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                    System.out.println("callbackl");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        rgbaMat = new Mat();
    }

    private BaseLoaderCallback callBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            System.out.println("onManagerConnected");
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File fileDir = getDir("cascade", Context.MODE_PRIVATE);
                    caseFile = new File(fileDir, "haarcascade_frontalface_alt2.xml");
                    FileOutputStream fileOutputStream = new FileOutputStream(caseFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    fileOutputStream.close();

                    cascadeClassifier = new CascadeClassifier(caseFile.getAbsolutePath());
                    if (cascadeClassifier.empty()) {
                        cascadeClassifier = null;
                    } else {
                        fileDir.delete();
                    }

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }

        }

    };

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
        rgbaMat.release();
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
                            addTextureViewPreviewDetection();
//                            addSurfaceView();
                        }
                    });
                }
            });
//            mDvr.setOnDisconnectedCallback(new OnDisconnectedCallback() {
//                @Override
//                public void onDisconnected(IDvr dvr) {
//                    Log.i(TAG, "onDisconnected");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            removeSurfaceView();
//                        }
//                    });
//                }
//            });
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
                    System.out.println("inside video callback method1");
                    File dir = Environment.getExternalStorageDirectory();
                    File target = new File(dir, "captures");
                    if (!target.exists())
                        target.mkdirs();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendFiletoServer(target);
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

    private void addTextureViewPreviewDetection() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                if (mDvr != null) mDvr.addSurface(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                if (mDvr != null) mDvr.removeSurface(surface);
                return true;
            }
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                if (isReadyForDetection) {
                    isReadyForDetection = false;
                    Utils.bitmapToMat(textureView.getBitmap(640, 480), rgbaMat);
                    MatOfRect faceDetections = new MatOfRect();
                    cascadeClassifier.detectMultiScale(rgbaMat, faceDetections);
                    if (faceDetections.toArray().length > 0) {
                        System.out.println("the length of array" + faceDetections.toArray().length);
                        captureImage();
                    }
                }
            }
        });
    }

    private void sendFiletoServer(File target) {
        File[] files = target.listFiles();
        if (files.length > 0) {
            File uploadFile = new File(target, files[0].getName());
            System.out.println("upload file is " + uploadFile);
            Uri uri = Uri.fromFile(uploadFile);
            uploadFile = new File(uri.getPath());
            System.out.println("new upload file is " + uploadFile);

            /*Direct image transfer*/
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile);
            MultipartBody.Part parts = MultipartBody.Part.createFormData("image_base64", uploadFile.getName(), requestBody);
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"),
                    appPrefManager.getDeviceId());
            Call<AttendanceResponseModel> call = API.getClient(appPrefManager.getUrl()).validateUserMultipart(parts, idBody);
            File finalUploadFile = uploadFile;
            call.enqueue(new Callback<AttendanceResponseModel>() {
                @Override
                public void onResponse(Call<AttendanceResponseModel> call, Response<AttendanceResponseModel> response) {
                    long stopTime = System.nanoTime();
                    System.out.println("eta chha duration " + (stopTime - startTime) / 1000000000 + "Seconds");
                    System.out.println("response code is " + response.code());
                    finalUploadFile.delete();
                    if (response.body() != null) {
                        AttendanceResponseModel responseModel = response.body();
                        ArrayList<AcknowledgeClass> acknowledgeClasses = responseModel.getAcknowledge();
                        if (!acknowledgeClasses.isEmpty()) {
                            rvReports.setLayoutManager(new GridLayoutManager(AttendanceActivity.this, 1));
                            rvReports.setAdapter(null);
                            System.out.println("recycler view removed");
                            System.out.println("length of acknowledgeClasses" + acknowledgeClasses.size());
                            ReportViewAdapter adapter = new ReportViewAdapter(AttendanceActivity.this, acknowledgeClasses);
                            rvReports.setAdapter(adapter);
                        }
                    }
                    isReadyForDetection = true;
                }

                @Override
                public void onFailure(Call<AttendanceResponseModel> call, Throwable t) {
                    isReadyForDetection = true;
                    finalUploadFile.delete();
                    System.out.println("failure ko message is " + t.getMessage());
                    System.out.println("failure ko localised message is " + t.getLocalizedMessage());
                    System.out.println("failure ko cause is " + t.getCause());
                }
            });
        }
    }



    private void captureImage() {
        File dir = Environment.getExternalStorageDirectory();
        File target = new File(dir, "captures");
        if (!target.exists())
            target.mkdirs();
        filename = target.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
        System.out.println("filename is " + filename);
        startTime = System.nanoTime();
        mDvr.captureMjpeg(filename);
    }

    private void showDeviceIdDialog() {
        Dialog dialog = new Dialog(AttendanceActivity.this);
        dialog.setContentView(R.layout.device_id_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView tvTitleDialog = dialog.findViewById(R.id.tvTitleDialog);
        TextInputEditText etDeviceId = dialog.findViewById(R.id.etDeviceId);
        TextInputLayout etIdLayout = dialog.findViewById(R.id.etIdLayout);
        TextInputEditText etPassword = dialog.findViewById(R.id.etPassword);
        TextInputLayout etUrlLayout = dialog.findViewById(R.id.etUrlLayout);
        etDeviceId.setVisibility(View.GONE);
        etIdLayout.setVisibility(View.GONE);
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
                        destroyDvr();
                        startActivity(new Intent(AttendanceActivity.this, MainActivity.class));
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

}
