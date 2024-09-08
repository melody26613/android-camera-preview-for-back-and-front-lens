package com.example.camerapreview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCameraPreview();
    }

    private void initCameraPreview() {
        CameraPreviewFragment cameraPreviewFragment = CameraPreviewFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cameraPreviewLayout, cameraPreviewFragment)
                .commit();

        if (cameraPreviewFragment.getView() != null) {
            cameraPreviewFragment.show();
        }
    }
}