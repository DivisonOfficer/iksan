package com.sss.fills;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.sss.fills.MapActivity.exifOrientationToDegrees;

public class GetInfActivity extends AppCompatActivity {
    /*사진 활용*/
    private static final String TAG = "blackjin";
    private Boolean isPermission = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile;
    private Boolean isCamera = false;
    /*소리 활용*/
    MediaRecorder mRecorder = null;
    boolean isRecording = false;
    ImageButton mBtRecord = null;
    TextView tRecord=null;
    String mPath = null;
    EditText textEdit;
    public static String SavedMemo=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SavedMemo="";
        setContentView(R.layout.activity_get_inf);
        /*메모*/
        TextView tb;
        tb=(TextView)findViewById(R.id.getinfTitle);
        tb.setText(MapActivity.marker[MapActivity.Cur_Spot].getName());
        tb=(TextView)findViewById(R.id.getinfAdress);
        tb.setText(MapActivity.marker[MapActivity.Cur_Spot].Adress);
        tb=(TextView)findViewById(R.id.getinfIndex);
        tb.setText(MapActivity.marker[MapActivity.Cur_Spot].getIndex());
        Button textbt= (Button)findViewById(R.id.texteditfinal);
        final EditText editText = (EditText)this.findViewById(R.id.editTextGetInf);
        textbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send=new String(editText.getText().toString());
                MapActivity.marker[MapActivity.Cur_Spot].Memo=send;
                SavedMemo=send;
               // Toast.makeText(getApplication(),send+MapActivity.marker[MapActivity.Cur_Spot].Memo,Toast.LENGTH_LONG).show();
                Log.e("ferwef","메모적었다");
            }
        });



        editText.addTextChangedListener(new TextWatcher() {
            // 입력되는 텍스트에 변화가 있을 때
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // Toast.makeText(MainActivity.this, "입력중", Toast.LENGTH_SHORT).show();
            //    MapActivity.marker[MapActivity.Cur_Spot].setMemo(textEdit.getText());
            }

            // 입력이 끝났을 때
            @Override
            public void afterTextChanged(Editable editable) {
                //Toast.makeText(MainActivity.this, "입력완료", Toast.LENGTH_SHORT).show();
              //  MapActivity.marker[MapActivity.Cur_Spot].setMemo(editable);
            }

            // 입력하기 전에
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               // Toast.makeText(MainActivity.this, "입력전", Toast.LENGTH_SHORT).show();
            }
        });


/*녹음*/
        mRecorder = new MediaRecorder();

        mBtRecord = (ImageButton) findViewById(R.id.record);
        mBtRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording == false) {
                    initAudioRecorder();
                    mRecorder.start();

                    isRecording = true;
                    //mBtRecord.setText("Stop Recording");
                } else {
                    mRecorder.stop();

                    isRecording = false;
                    MapActivity.marker[MapActivity.Cur_Spot].Sound=new File(mPath);

                   // mBtRecord.setText("Start Recording");
                }
            }
        });
        /*final Button record = (Button) findViewById(R.id.record);
        *//* 이벤트를 받기 위한 리스너 작성 *//*
        record.setOnClickListener(
                new View.OnClickListener() {	// Listener
                    public void onClick(View v) {
                        Log.i("Act1.btn_act1_go2", "onClick");
                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.voicenote");
                        startActivity(intent);
                        //startActivity(intent);
                    }
                }
        );*/
        final ImageButton go_next = (ImageButton) findViewById(R.id.loading);
        /* 이벤트를 받기 위한 리스너 작성 */
        go_next.setOnClickListener(
                new View.OnClickListener() {	// Listener
                    public void onClick(View v) {
                        Log.i("Act1.btn_act1_go2", "onClick");
                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.sec.android.app.myfiles");
                        startActivity(intent);
                       // startActivity(intent);
                    }
                }
        );
       /* final Button next = (Button) findViewById(R.id.basic);
        *//* 이벤트를 받기 위한 리스너 작성 *//*
        next.setOnClickListener(
                new View.OnClickListener() {	// Listener
                    public void onClick(View v) {
                        //  Log.i("Act1.btn_act1_go2", "onClick");
                        Intent intent1 = new Intent(GetInfActivity.this, ViewPhotoActivity.class);
                        startActivity(intent1);
                    }
                }
        );*/
        tedPermission();//사진 권환 설정,파일 설정
        permissionCheck();//음악 권환 설정

        findViewById(R.id.btnGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
                if(isPermission) goToAlbum();
                else Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btnCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 권한 허용에 동의하지 않았을 경우 토스트를 띄웁니다.
                if(isPermission)  takePhoto();
                else Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
            }
        });

    }
    public void onBackPressed() {
        //textEdit=(EditText)this.findViewById(R.id.editTextGetInf);
        //MapActivity.marker[MapActivity.Cur_Spot].setMemo(textEdit.getText());
        super.onBackPressed();
    }

    void initAudioRecorder() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PCMRECORDER/record.aac";
        Log.d(TAG, "file path is " + mPath);
        mRecorder.setOutputFile(mPath);

        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);
            Cursor cursor = null;
            try {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));
                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }

    /**
     *  앨범에서 이미지 가져오기
     */
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this,
                        "{package name}.provider", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }

    /**
     *  폴더 및 파일 만들기
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "blackJin_" + timeStamp + "_";

        // 이미지가 저장될 폴더 이름 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/blackJin/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());
        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {
        ImageView imageView = findViewById(R.id.imageView);
       // ImageUtils.resizeFile(tempFile, tempFile, 1280, isCamera);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        originalBm=MapActivity.rotate(originalBm,exifDegree);
        int nh = (int) (originalBm.getHeight() * (1024.0 / originalBm.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(originalBm, 1024, nh, true);
        scaled=MapActivity.createSquaredBitmap(scaled);

        Log.d(TAG, "setImage : " + tempFile.getAbsolutePath());
        imageView.setImageBitmap(scaled);
        MapActivity.marker[MapActivity.Cur_Spot].photo=scaled;
        MapActivity.marker[MapActivity.Cur_Spot].image_exists=true;
/*
        Paint paint= new Paint();
        Bitmap sccaled=scaled.copy(scaled.getConfig(),true);
        Canvas tempcanvas= new Canvas(sccaled);
        Bitmap Nshape=MapActivity.marker[MapActivity.Cur_Spot].markerimage;
        Nshape=Bitmap.createScaledBitmap(Nshape,scaled.getWidth()/2,scaled.getHeight()/2,true);
        tempcanvas.drawBitmap(scaled,0,0,paint);
        PorterDuff.Mode mode= PorterDuff.Mode.XOR;
        paint.setXfermode(new PorterDuffXfermode(mode));
        tempcanvas.drawBitmap(Nshape,0,0,paint);
        Bitmap sscaled = Bitmap.createScaledBitmap(sccaled, 128, 128, true);
        MapActivity.marker[MapActivity.Cur_Spot].markerimage=sscaled;*/



        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        tempFile = null;
    }
    /**
     *  권한 설정
     */
    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;
            }
        };
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
            .setDeniedMessage(getResources().getString(R.string.permission_1))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
}
}
