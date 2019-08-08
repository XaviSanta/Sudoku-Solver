package com.santa.xavi.sudoku;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private TextureView textureView;
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    // private Size imageDimension;

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    int[][] S = new int[9][9];
    int[][] Solution = new int[9][9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        S = initMatrix(S);

        textureView = (TextureView) findViewById(R.id.textureView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

    }
    private void createCameraPreview() {
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(100,100);
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            //imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public int[][] initMatrix(int[][] S){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                S[i][j] = 0;
            }
        }
        return S;
    }
    public void changeValue(Button b, EditText input){
        String s = input.getText().toString();
        String nameId = getResources().getResourceEntryName(b.getId()); //The id contains the row and col
        String sRow = nameId.substring(1,2);
        String sCol = nameId.substring(2);
        int num;
        int row;
        int col;
        try {
            num = Integer.parseInt(s);
        } catch(NumberFormatException nfe) {num = 0;}
        try {
            row = Integer.parseInt(sRow);
        } catch(NumberFormatException nfe) {row = 0;}
        try {
            col = Integer.parseInt(sCol);
        } catch(NumberFormatException nfe) {col = 0;}
        this.S[row][col] = num;
    }
    public void xavi(View view){
        Button b = (Button) view;
        EditText input = findViewById(R.id.editText);
        String s = input.getText().toString();   //Get the number in the editText
        changeValue(b,input);                   //change it in the matrix S
        b.setText(s);                          //put it in the visual cell
    }
    public void solveSudoku(View view){
        Sudoku s = new Sudoku();
        try{
            this.Solution = s.start(this.S);
            showSolution();
        }catch(Exception e){

        }

    }
    public void showSolution(){
        TextView v = findViewById(R.id.textView1);
        v.setText(" ");
        int num;
        Button b;
        String s;
        int Id ;
        for (int i = 0; i < 9; i ++){
            for(int j = 0; j < 9; j++){
                s = "_"+i+""+j;
                Id = getResources().getIdentifier(s,"id",getPackageName());
                b = findViewById(Id);
                num = this.Solution[i][j];
                s = String.valueOf(num);
                b.setText(s);
            }
        }
    }
    public void restart(View view){
        TextView v = findViewById(R.id.textView1);
        v.setText("Input your sudoku");
        this.S = initMatrix(this.S);
        this.Solution = initMatrix(this.Solution);
        Button b;
        String s;
        int Id ;
        for (int i = 0; i < 9; i ++){
            for(int j = 0; j < 9; j++){
                s = "_"+i+""+j;
                Id = getResources().getIdentifier(s,"id",getPackageName());
                b = findViewById(Id);
                b.setText("");
            }
        }
    }
}
