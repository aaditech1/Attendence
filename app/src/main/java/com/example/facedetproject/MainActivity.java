package com.example.facedetproject;



import static com.example.facedetproject.Constants.StringConstants.TYPE_ATTENDANCE;
import static com.example.facedetproject.Constants.StringConstants.TYPE_REGISTER;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.facedetproject.UI.AttendanceActivity;
import com.example.facedetproject.UI.RegisterActivity;
import com.example.facedetproject.Utils.AppPrefManager;

public class MainActivity extends AppCompatActivity {

    MaterialButton registerBtn, attendanceBtn;
    private AppPrefManager appPrefManager;

    @Override
    public void onBackPressed() {

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBtn = findViewById(R.id.registerBtn);
        attendanceBtn = findViewById(R.id.attendanceBtn);
        appPrefManager = new AppPrefManager(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPrefManager.setMode(TYPE_REGISTER);
                startRegisterActivity();
            }
        });

        attendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appPrefManager.setMode(TYPE_ATTENDANCE);
                startAttendanceActivity();
            }
        });

        if (appPrefManager.getDeviceId().equals("")) {
            showDeviceIdDialog();
        }
    }

    private void showDeviceIdDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.device_id_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);
        TextView tvTitleDialog = dialog.findViewById(R.id.tvTitleDialog);
        TextInputEditText etDeviceId = dialog.findViewById(R.id.etDeviceId);
        TextInputEditText etPassword = dialog.findViewById(R.id.etPassword);
        TextInputEditText etUrl = dialog.findViewById(R.id.etUrl);
        TextInputLayout etUrlLayout = dialog.findViewById(R.id.etUrlLayout);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appPrefManager.getDeviceId().equals("")) {
                    tvTitleDialog.setText("Device Id not given previously");
                    tvTitleDialog.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    dialog.dismiss();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredDeviceID = (!etDeviceId.getText().toString().isEmpty()) ? etDeviceId.getText().toString() : "";
                String enteredPassword = (!etPassword.getText().toString().isEmpty()) ? etPassword.getText().toString() : "";
                String enteredUrl = (!etUrl.getText().toString().isEmpty()) ? etUrl.getText().toString() : "";
                System.out.println("entered device id ko value: " + enteredDeviceID);
                if (enteredDeviceID.equals("")) {
                    tvTitleDialog.setText("Enter Device Id First");
                    tvTitleDialog.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    if (enteredPassword.equals("")) {
                        tvTitleDialog.setText("Enter Password first");
                        tvTitleDialog.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        if (enteredUrl.equals("")) {
                            tvTitleDialog.setText("Enter Url first");
                            tvTitleDialog.setTextColor(Color.parseColor("#FF0000"));
                        } else {
                            appPrefManager.setUrl(enteredUrl);
                            appPrefManager.setDeviceId(enteredDeviceID);
                            appPrefManager.setPassword(enteredPassword);
                            dialog.dismiss();
                        }
                    }

                }
            }
        });
        dialog.show();
    }

    private void startAttendanceActivity() {
        startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
//        appPrefManager.setMode(TYPE_ATTENDANCE);

    }

    private void startRegisterActivity() {
//        appPrefManager.setMode(TYPE_REGISTER);
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }
}
