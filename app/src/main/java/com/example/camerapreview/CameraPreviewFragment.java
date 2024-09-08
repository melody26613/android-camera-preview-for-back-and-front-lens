package com.example.camerapreview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.camerapreview.databinding.FragmentCameraPreviewBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraPreviewFragment extends Fragment {
    private final static String TAG = CameraPreviewFragment.class.getSimpleName();

    private FragmentCameraPreviewBinding binding;

    private ExecutorService cameraExecutor;
    private Preview preview;

    private ProcessCameraProvider processCameraProvider;

    private int lensFacing = CameraSelector.LENS_FACING_BACK;

    // can pass interface as argument if needed
    public static CameraPreviewFragment newInstance() {
        return new CameraPreviewFragment();
    }

    public CameraPreviewFragment() {
        //  An empty constructor for Android System to use, otherwise exception may occur.
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted != null && isGranted) {
                    bindPreview();
                } else {
                    try {
                        Toast.makeText(getActivity(), getString(R.string.hint_grant_permission), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraPreviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.previewView.post(this::initCamera);
        binding.switchLenButton.post(this::initSwitchButton);
    }

    private void initCamera() {
        Log.i(TAG, "initCamera()");

        cameraExecutor = Executors.newSingleThreadExecutor();
        preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        try {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
            cameraProviderFuture.addListener(() -> {
                try {
                    processCameraProvider = cameraProviderFuture.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                show();
            }, ContextCompat.getMainExecutor(requireContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindPreview() {
        Log.i(TAG, "bindPreview()");

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        ImageCapture imageCapture = new ImageCapture.Builder().build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        processCameraProvider.unbindAll();
        processCameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalysis);
    }

    public void show() {
        try {
            String permission = Manifest.permission.CAMERA;

            if (ActivityCompat.checkSelfPermission(
                    requireContext(), permission) == (PackageManager.PERMISSION_GRANTED)) {
                bindPreview();
            } else {
                requestPermissionLauncher.launch(permission);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSwitchButton() {
        Button switchButton = binding.switchLenButton;
        switchButton.setOnClickListener(v -> {
            lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK) ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
            bindPreview();
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (processCameraProvider != null) {
            processCameraProvider.unbindAll();
            processCameraProvider.shutdown();
            processCameraProvider = null;
        }

        if (preview != null) {
            preview.setSurfaceProvider(null);
            preview = null;
        }

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }
    }
}
