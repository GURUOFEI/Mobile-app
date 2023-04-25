package com.example.textapp.notepad.activity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textapp.R;
import com.example.textapp.notepad.APP;
import com.example.textapp.notepad.adapter.PhotoAdapter;
import com.example.textapp.notepad.bean.LocationBean;
import com.example.textapp.notepad.bean.NotepadBean;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.DateUtil;
import com.example.textapp.notepad.utils.FileUtil;
import com.example.textapp.notepad.utils.LogUtil;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.ToastUtil;
import com.example.textapp.notepad.utils.firebse.FirestoreDatabaseUtil;
import com.example.textapp.notepad.utils.firebse.StorageUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordActivity extends BaseActivity implements View.OnClickListener {
    ImageView note_back;
    TextView note_time;
    EditText content;
    ImageView delete;
    ImageView note_save;
    TextView noteName;
    /**
     * 分类
     */
    private TextView tv_type;
    /**
     * 定位按钮
     */
    private TextView tv_location;
    /**
     * 图片列表
     */
    private RecyclerView rv_photo;
    /**
     * 图片列表适配器
     */
    private PhotoAdapter photoAdapter;
    /**
     * 图片列表数据
     */
    private List<String> photoList;
    /**
     * 图片地址
     */
    private String imagePath;

    /**
     * 添加图片按钮标识
     */
    public final static String PHOTO_ADD_FLAG = "add";
    private final static int REQUEST_CAMERA = 10001;
    private final static int REQUEST_IMAGES = 10002;

    /**
     * 删除定位的按钮
     */
    private ImageView iv_delete_location;

//    private SQLiteHelper mSQLiteHelper;
    //    private String id;
    private NotepadBean notepadBean;

    //分类
    private String[] typeArray;
    /**
     * 选择的分类
     */
    private int chooseType = -1;

    /**
     * 当前用户的uuid
     */
    private String uuid;

    private LocationBean locationBean = null;

    public final static String KEY_NOTEPAD = "key_notepad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        note_back = (ImageView) findViewById(R.id.note_back);//后退键
        note_time = (TextView) findViewById(R.id.tv_time);//保存记录的时间
        content = (EditText) findViewById(R.id.note_content);//记录的内容
        delete = (ImageView) findViewById(R.id.delete);//清空的按钮
        note_save = (ImageView) findViewById(R.id.note_save);//保存的按钮
        noteName = (TextView) findViewById(R.id.note_name);//标题的名称
        rv_photo = findViewById(R.id.rv_photo);//图片列表
        tv_type = findViewById(R.id.tv_type);
        tv_location = findViewById(R.id.tv_location);
        iv_delete_location = findViewById(R.id.iv_delete_location);
        tv_location.setOnClickListener(this);
        tv_type.setOnClickListener(this);
        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        iv_delete_location.setOnClickListener(this);

        initPhotoList();

        initData();

    }

    /**
     * 初始化图片列表
     */
    private void initPhotoList() {
        photoList = new ArrayList<>();
        //添加图片按钮
        photoList.add(PHOTO_ADD_FLAG);
        photoAdapter = new PhotoAdapter(photoList);
        photoAdapter.setCallback(new PhotoAdapter.Callback() {
            @Override
            public void onItemClick(int position) {
                LogUtil.d("click:"+position);
                if(position==photoList.size()-1) {
                    chooseImage();
                }else{
                    //浏览照片
                    Intent intent =new Intent(RecordActivity.this,PhotoActivity.class);
                    intent.putExtra("photo_url",photoList.get(position));
                    startActivity(intent);
                }
            }

            @Override
            public void delete(int position) {
                //从列表中删除照片
                photoList.remove(position);
                //刷新列表
                photoAdapter.notifyDataSetChanged();
            }
        });
        rv_photo.setAdapter(photoAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv_photo.setLayoutManager(linearLayoutManager);
    }

    public void initData() {
        uuid = (String) SharedPreUtil.getParam(RecordActivity.this, SharedPreUtil.LOGIN_UUID, "");

        //读取分类数据
        typeArray = getResources().getStringArray(R.array.noteboot_type);

//        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText(R.string.add_record);
        Intent intent = getIntent();
        if (intent != null) {
            notepadBean = intent.getParcelableExtra(KEY_NOTEPAD);
            if (notepadBean != null) {
                noteName.setText(R.string.edit_record);
                content.setText(notepadBean.getNotepadContent());
                note_time.setText(DateUtil.formate(notepadBean.getNotepadTime()));
                note_time.setVisibility(View.VISIBLE);
                if (notepadBean.getType() != -1) {
                    tv_type.setText(String.format(getString(R.string.type_show), typeArray[notepadBean.getType()]));
                }
                //如果有图片，显示图片
                if (!TextUtils.isEmpty(notepadBean.getPhotos())) {
                    String[] split = notepadBean.getPhotos().split(",");
                    for (String s : split) {
                        photoList.add(0, s);
                        LogUtil.d(s);
                    }
                    photoAdapter.notifyDataSetChanged();
                }
                //如果有定位，显示定位信息
                showLocationInfo(notepadBean.getLocationPlaceName());
            }else{
                //没有定位，不显示定位信息
                showLocationInfo("");
            }
        }

    }

    /**
     * 选择分类
     */
    private void chooseType() {
        //添加一个弹窗构造
        AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this).setCancelable(true);
        builder.setTitle(R.string.record_choose_type);
        builder.setItems(typeArray, (dialog, which) -> {
            this.chooseType = which;
            tv_type.setText(typeArray[which]);
        }).create();
        //创建弹窗
        AlertDialog dialog = builder.create();
        //显示弹窗
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type:
                chooseType();
                break;
            case R.id.note_back:
                finish();
                break;
            case R.id.delete:
                content.setText(" ");
                break;
            case R.id.note_save:
                String noteContent = content.getText().toString().trim();
                if (notepadBean != null) {
                    //修改记录的功能
                    if (noteContent.length() > 0) {
                        LogUtil.d("修改");
//                        if (mSQLiteHelper.updateData(id, noteContent, DBUtils.getTime())) {
//                            showToast("修改成功");
//                            setResult(2);
//                            finish();
//                        } else {
//                            showToast("修改失败");
//                        }

                        //更新的内容
                        Map<String, Object> updateContent = new HashMap<>();
                        updateContent.put("notepadContent", noteContent);//内容
                        updateContent.put("notepadTime", System.currentTimeMillis());//更新时间
                        updateContent.put("type", chooseType);//分类
                        updateContent.put("photos", getPhoto());//照片
                        //定位
                        if (locationBean != null) {
                            updateContent.put("locationPlaceName", locationBean.getLocationPlaceName());//定位名称
                            updateContent.put("latitude", locationBean.getLatitude());//纬度
                            updateContent.put("longitude", locationBean.getLongitude());//经度
                        }else{
                            updateContent.put("locationPlaceName", null);//定位名称
                            updateContent.put("latitude", 0.0f);//纬度
                            updateContent.put("longitude", 0.0f);//经度
                        }

                        showLoadingDialog(R.string.common_submit);
                        // Add a new document with a generated ID
                        FirestoreDatabaseUtil.getInstance()
                                .getUserNotebook(uuid)
                                .document(notepadBean.getId())
                                .update(updateContent)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        ToastUtil.show(R.string.modify_success);
                                        finish();
                                    } else {
                                        ToastUtil.show(R.string.modify_failure);
                                    }
                                    dismissLoadingDialog();
                                });
                    } else {
                        ToastUtil.show(R.string.edit_content_modify_tip);
                    }
                } else {
                    //添加记录的功能
                    if (noteContent.length() > 0) {
                        LogUtil.d("添加");
//                        if (mSQLiteHelper.insertData(noteContent,DBUtils.getTime(), (String) SharedPreUtil.getParam(RecordActivity.this, SharedPreUtil.LOGIN_DATA,""))){
//                            showToast("保存成功");
//                            setResult(2);
//                            finish();
//                        }else{
//                            showToast("保存失败");
//                        }
                        NotepadBean notepadBean = new NotepadBean();
                        notepadBean.setNotepadContent(noteContent);
                        notepadBean.setNotepadTime(System.currentTimeMillis());
                        notepadBean.setNotepadPhone((String) SharedPreUtil.getParam(RecordActivity.this, SharedPreUtil.LOGIN_DATA, ""));
                        notepadBean.setType(chooseType);
                        //处理图片数据
                        notepadBean.setPhotos(getPhoto());
                        //处理定位数据
                        if (locationBean != null) {//有定位
                            notepadBean.setLocationPlaceName(locationBean.getLocationPlaceName());
                            notepadBean.setLatitude(locationBean.getLatitude());
                            notepadBean.setLongitude(locationBean.getLongitude());
                        }

                        showLoadingDialog(R.string.common_submit);
                        // Add a new document with a generated ID
                        FirestoreDatabaseUtil.getInstance()
                                .getUserNotebook(uuid)
                                .add(notepadBean)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        ToastUtil.show(R.string.save_success);
                                        finish();
                                    } else {
                                        ToastUtil.show(R.string.save_failure);
                                    }

                                });
                    } else {
                        ToastUtil.show(R.string.edit_content_save_tip);
                    }
                }
                break;
            case R.id.tv_location:
                addLocation();
                break;
            case R.id.iv_delete_location:
                locationBean = null;
                showLocationInfo("");
                break;
            default:
                break;
        }
    }

    /**
     * 添加定位，调用google places sdk
     */
    private void addLocation() {
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);
        // Use fields to define the data types to return.
        //这里定义需要获取地点名称和经纬度
        final List placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            showLoadingDialog(R.string.location_search);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    LocationBean[] locations = new LocationBean[response.getPlaceLikelihoods().size()];
                    for (int i = 0; i < response.getPlaceLikelihoods().size(); i++) {
                        Place place = response.getPlaceLikelihoods().get(i).getPlace();

                        //提取定位数据
                        LocationBean locationBean = new LocationBean();
                        locationBean.setLocationPlaceName(place.getName());
                        locationBean.setLatitude(place.getLatLng().latitude);
                        locationBean.setLongitude(place.getLatLng().longitude);
                        locations[i] = locationBean;

                    }
                    showLocationItem(locations);
                } else {
                    LogUtil.d("3");
                    Exception exception = task.getException();
                    LogUtil.d(task.getException().getMessage());
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        LogUtil.d("Place not found: " + apiException.getStatusCode());
                    }
                }
                dismissLoadingDialog();
            });
        }
    }

    /**
     * 根据地址是否空，显示定位信息
     *
     * @param address
     */
    private void showLocationInfo(String address) {
        LogUtil.d("address:" + address);
        Drawable locationIcon = getResources().getDrawable(R.drawable.baseline_location_on_24);
        if (TextUtils.isEmpty(address)) {
            //没有定位
            tv_location.setText(R.string.location_add_tip);
            locationIcon.setTint(Color.parseColor("#666666"));
            iv_delete_location.setVisibility(View.GONE);
        } else {
            //定位
            tv_location.setText(address);
            locationIcon.setTint(Color.parseColor("#03A9F4"));
            iv_delete_location.setVisibility(View.VISIBLE);
        }
        tv_location.setCompoundDrawablesRelativeWithIntrinsicBounds(locationIcon, null, null, null);
    }

    /**
     * 定位列表选择框，从定位列表中选择保存的定位
     *
     * @param
     */
    private void showLocationItem(LocationBean[] locations) {
        //添加一个弹窗构造
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(true);
        builder.setTitle(R.string.location_choose);
        String[] placeNames = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            placeNames[i] = locations[i].getLocationPlaceName();
        }
        //菜单选择
        builder.setItems(placeNames, (dialog, which) -> {
            //记录选择的定位，用于上传
            locationBean = locations[which];
            //显示定位信息
            showLocationInfo(placeNames[which]);
        }).create();
        //创建弹窗
        AlertDialog dialog = builder.create();
        //显示弹窗
        dialog.show();
    }

    /**
     * 处理图片数据
     *
     * @return
     */
    private String getPhoto() {
        String photos = "";
        for (int i = 0; i < photoList.size(); i++) {
            //图片路径
            String photoUrl = photoList.get(i);
            if (!PHOTO_ADD_FLAG.equals(photoUrl)) {
                photos += photoUrl + ",";
            }
        }
        //去掉后面的逗号
        if (photos.length() > 0) {
            photos = photos.substring(0, photos.length() - 2);
        }
        return photos;
    }

    /**
     * 选择图片
     */
    private void chooseImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(R.array.add_photo, (dialog, which) -> {
            switch (which) {
                case 0://拍照
                    //生成一个临时文件,用于保存照片数据
                    imagePath = FileUtil.createJpgFile();
                    //调走相机
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(APP.context, APP.context.getPackageName() + ".fileprovider", new File(imagePath)));
                    startActivityForResult(intentCamera, REQUEST_CAMERA);//启动相机
                    break;
                case 1://相册
                    //调走相册
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, REQUEST_IMAGES);
                    break;
            }
        });
        builder.show();
    }

    /**
     * 页面回调监听
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //相册回调
            if (requestCode == REQUEST_IMAGES) {
                // 从相册返回的数据
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    //获取照片真实路径
                    imagePath = FileUtil.getRealPathFromUri(APP.context, uri);
                    uploadPhoto();
                }
            }
            //拍照回调
            if (requestCode == REQUEST_CAMERA) {
                Uri uri = FileProvider.getUriForFile(APP.context, APP.context.getPackageName() + ".fileprovider", new File(imagePath));
                uploadPhoto();
            }
        }
    }

    /**
     * 上传图片
     */
    private void uploadPhoto() {
        LogUtil.d("准备上传:" + imagePath);
        Uri file = Uri.fromFile(new File(imagePath));
        StorageReference ref = StorageUtil.getInstance().getRef(uuid, file.getLastPathSegment());
        showLoadingDialog(R.string.uploading);
        ref.putFile(file)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        LogUtil.d(downloadUri.toString());
                        //显示图片
                        photoList.add(0, downloadUri.toString());
                        //刷新列表
                        photoAdapter.notifyDataSetChanged();
                    }
                    dismissLoadingDialog();
                });
    }

}
