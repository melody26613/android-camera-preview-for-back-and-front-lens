package com.example.camerapreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private CameraPreviewFragment cameraPreviewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCameraPreview();
    }

    private void initCameraPreview() {
        cameraPreviewFragment = CameraPreviewFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cameraPreviewLayout, cameraPreviewFragment)
                .commit();

        if (cameraPreviewFragment.getView() != null) {
            cameraPreviewFragment.show();
        }
    }
}