package glucosetracker.com.bloodgluscosetracker.Utils;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import glucosetracker.com.bloodgluscosetracker.BuildConfig;


/**
 * Created by GurvinderS on 3/18/2017.
 */

public class CameraGalleryUtil {

    private Fragment mFragment;
    private Activity mActivity;

    private String mCurrentPhotoPath;


    public CameraGalleryUtil(Activity activity, Fragment fragment) {
        this.mActivity = activity;
        this.mFragment = fragment;


    }


    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }





    public void openCamera() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f;
        try {
            mCurrentPhotoPath = null;
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();

            Uri photoURI = FileProvider.getUriForFile(mActivity, mActivity.getApplicationContext().getPackageName() + "." + BuildConfig.APPLICATION_ID + ".provider", f);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip =
                        ClipData.newUri(mActivity.getContentResolver(), "A photo", photoURI);

                takePictureIntent.setClipData(clip);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                List<ResolveInfo> resInfoList =
                        mActivity.getPackageManager()
                                .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    mActivity.grantUriPermission(packageName, photoURI,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
            if (mFragment == null)
                mActivity.startActivityForResult(takePictureIntent, AppConstants.CAMERA_REQUEST);
            else
                mFragment.startActivityForResult(takePictureIntent, AppConstants.CAMERA_REQUEST);

        } catch (IOException e) {
            e.printStackTrace();
            mCurrentPhotoPath = null;
        }

    }

    public void openGalleryChooser() {

        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent i1 = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        Intent[] intentArray = new Intent[]{i, i1};
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        if (chooserIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            if (mFragment == null)
                mActivity.startActivityForResult(i, AppConstants.GALLERY_REQUEST);
            else
                mFragment.startActivityForResult(i, AppConstants.GALLERY_REQUEST);
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (mFragment == null)
                mActivity.startActivityForResult(intent, AppConstants.GALLERY_REQUEST);
            else
                mFragment.startActivityForResult(intent, AppConstants.GALLERY_REQUEST);
        }

    }


    public void openGallery() {

        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(mActivity.getPackageManager()) != null) {
            if (mFragment == null)
                mActivity.startActivityForResult(i, AppConstants.GALLERY_REQUEST);
            else
                mFragment.startActivityForResult(i, AppConstants.GALLERY_REQUEST);
        } else {
            Intent intent = new Intent();
            intent.setType("image*//*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (mFragment == null)
                mActivity.startActivityForResult(intent, AppConstants.GALLERY_REQUEST);
            else
                mFragment.startActivityForResult(intent, AppConstants.GALLERY_REQUEST);
        }

    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    public String getCameraFilePath() {
        return mCurrentPhotoPath;


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return getImagePathFromInputStreamUri(uri);

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {

        String timeStamp = DateUtils.getInstance().getDateFromDateFormat(new Date(), "yyyyMMdd_HHmmss");
        String imageFileName = AppConstants.JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        return File.createTempFile(imageFileName, AppConstants.JPEG_FILE_SUFFIX, albumF);
    }

    private File getAlbumDir() {

        File storageDir = null;

        String path = Environment.getExternalStorageDirectory() + AppConstants.CAPTURE_IMAGE_PATH;
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdirs();
        }
        System.out.println("path " + path);
        if (isDirectoryCreated)
            storageDir = dir;


        return storageDir;
    }

    public Bitmap getBitmap(String mPicturePath, int imgWidth, int imgHeight) {
        int targetW = imgWidth;
        int targetH = imgHeight;

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPicturePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        // bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mPicturePath, bmOptions);
        Matrix matrix = new Matrix();
        matrix.setRotate(getCameraPhotoOrientation(mPicturePath));

        if (bitmap != null) {

            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            File f = new File(mPicturePath);
            try {
                bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /* Associate the Bitmap to the ImageView */
            return bmRotated;
        } else {
            bitmap = BitmapFactory.decodeFile(mPicturePath);
            /* Associate the Bitmap to the ImageView */
            return bitmap;
        }

    }


    private int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            // context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    private String getImagePathFromInputStreamUri(Uri uri) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = mActivity.getContentResolver().openInputStream(uri); // context needed
                File photoFile = createTemporalFileFrom(inputStream);

                filePath = photoFile.getPath();

            } catch (FileNotFoundException e) {
                // log
            } catch (IOException e) {
                // log
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private File createTemporalFileFrom(InputStream inputStream) throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];

            targetFile = createTemporalFile();
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    private File createTemporalFile() {
        return new File(mActivity.getExternalCacheDir(), "tempFile" + System.currentTimeMillis() + ".jpg"); // context needed
    }

    public boolean checkImagePathIsRight(String mPicturePath) {
        return mPicturePath != null && !(mPicturePath.contains("https") || mPicturePath.contains("http"));
    }

    public String getByteString(String fileName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(fileName);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            inputStream.close();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
