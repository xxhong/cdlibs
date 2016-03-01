package com.march.libs.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;

import com.march.libs.camera.util.CamParaUtil;
import com.march.libs.camera.util.FileUtil;
import com.march.libs.camera.util.CameraImageUtil;
import com.march.libs.utils.LUtils;

import java.io.IOException;
import java.util.List;

public class CameraInterface {
    private static final String TAG = "yanzi";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private int mCurrentCameraId = 0;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;

    public interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * ��Camera
     *
     * @param callback
     */
    public void doOpenCamera(CamOpenOverCallback callback) {
        Log.i(TAG, "Camera open....");
        if (mCamera == null) {
            mCamera = Camera.open(mCurrentCameraId);

            Log.i(TAG, "Camera open over....");
            if (callback != null) {
                callback.cameraHasOpened();
            }
        } else {
            Log.i(TAG, "Camera open ");
            doStopCamera();
        }


    }

    /**
     * @param surface
     * @param previewRate
     */
    public void doStartPreview(SurfaceTexture surface, float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }
    }


    public void switchCamera(CamOpenOverCallback camOpenOverCallback) {
        mCurrentCameraId = (mCurrentCameraId + 1) % Camera.getNumberOfCameras();
    }

    /**
     * ֹͣԤ�����ͷ�Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * ����
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }


    private void initCamera(float previewRate) {
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);

            changeSize(previewRate);

            mCamera.setDisplayOrientation(90);

//			CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters();
            Log.i(TAG, ":PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, ":PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    public void changeSize(float previewRate) {
        Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                mParams.getSupportedPictureSizes(), previewRate, 1000);
        mParams.setPictureSize(pictureSize.width, pictureSize.height);

        Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                mParams.getSupportedPreviewSizes(), previewRate, 1000);
        mParams.setPreviewSize(previewSize.width, previewSize.height);
        mCamera.setParameters(mParams);
        mCamera.startPreview();

        LUtils.i("chendong","pic " + pictureSize.width + " * " +pictureSize.height + " rate " + (pictureSize.width/pictureSize.height));
        LUtils.i("chendong","pre " + previewSize.width + " * " +previewSize.height + " rate " + (previewSize.width/previewSize.height));
    }


    ShutterCallback mShutterCallback = new ShutterCallback()
            //���Ű��µĻص������������ǿ����������Ʋ��š����ꡱ��֮��Ĳ�����Ĭ�ϵľ������ꡣ
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
            // �����δѹ��ԭ���ݵĻص�,����Ϊnull
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }
            if (null != b) {
                Bitmap rotaBitmap = CameraImageUtil.getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
            mCamera.startPreview();
            isPreviewing = true;
        }
    };


    public Camera getmCamera() {
        return mCamera;
    }
}
