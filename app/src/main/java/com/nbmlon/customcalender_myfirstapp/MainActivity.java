package com.nbmlon.customcalender_myfirstapp;

import static com.nbmlon.managemodule.PermissionManager.GetPermission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdSize;
import com.nbmlon.customcalender_myfirstapp.widget.appWidget_calender;
import com.nbmlon.mainmodule.MainAdapter;
import com.nbmlon.mainmodule.crop.CropView;
import com.nbmlon.mainmodule.crop.cropListener;
import com.nbmlon.mainmodule.mainimage.myCalender;
import com.nbmlon.mainmodule.result.Dialog_Result;
import com.nbmlon.managemodule.PermissionManager;
import com.nbmlon.managemodule.SharedPreferenceManager;
import com.nbmlon.managemodule.Todoitem;
import com.nbmlon.managemodule.ViewCropperBitmap;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements cropListener {


    private myCalender.curCalender curCalender;

    private RelativeLayout mMainLayout;
    private ImageView mMainImage;
    private EditText mMainInput;
    private TextView mBtn_add;
    private MainAdapter mMainAdapter;
    private RecyclerView mMainRV;
    private TextView mMonthTV;

    private ActivityResultLauncher<Intent> subActivityLauncher;
    private ActivityResultLauncher<Intent> MainImageCropLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        CustomCalender.getAds(MainActivity.this, findViewById(R.id.main_adView));

        Intent intent = getIntent();
        boolean MonthChanged = intent.getBooleanExtra("YearMonthChanged",false);
        if( MonthChanged ) {
            int lastyyyyMM = intent.getIntExtra("yearMonth", -1);
            monthChanged(lastyyyyMM);
        }



        ScrollView scrollView = findViewById(R.id.TopFrame);
        mMainRV = findViewById(R.id.main_rv);
        mMainImage = findViewById(R.id.main_image);
        mMainLayout = findViewById(R.id.MainLayout);
        mMainInput = findViewById(R.id.main_et_newTodo);
        mBtn_add = findViewById(R.id.main_btn_add);
        mMonthTV = findViewById(R.id.main_tv_month);

        mMonthTV.setText(new DateTime().toString("yyyy. MM."));
        mMainInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mBtn_add.callOnClick();
                return true;
            }
            return false;
        });

        scrollView.post(() -> {
            if(CustomCalender.mainImageLength == CustomCalender.UNSETTING){
                int[] view_pos = new int[2];                int[] root_pos = new int[2];
                mMainImage.getLocationOnScreen(view_pos);   scrollView.getLocationOnScreen(root_pos);

                int adHeight = AdSize.BANNER.getHeightInPixels(MainActivity.this);

                int maxW = (int) (scrollView.getWidth() * 0.9);
                int maxH = scrollView.getHeight() - adHeight + root_pos[1] - view_pos[1];
                CustomCalender.mainImageLength = Math.min(maxW, maxH);
            }

            curCalender = new myCalender.curCalender
                    (MainActivity.this,
                            CustomCalender.mainImageLength);

            mMainImage.setLayoutParams(new LinearLayout.LayoutParams(CustomCalender.mainImageLength,CustomCalender.mainImageLength));
            mMainImage.setImageBitmap(curCalender.getCalender());

        });


        Thread th1 = new Thread(this::setClickListener); th1.start();
        SetLauncher();

        mMainAdapter =  new MainAdapter(this);
        mMainRV.setAdapter(mMainAdapter);
    }





    private void SetLauncher(){

        subActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_CANCELED ||
                            result.getResultCode() == RESULT_OK )
                        getUpdate();
                });

        MainImageCropLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        Uri imageUri = result.getData().getData();
                        inflateCropView(imageUri);
                    }
                });

        ActivityResultLauncher<Intent> DL_AlbumLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        ArrayList<Uri> adapterItems = new ArrayList<>();
                        adapterItems.add(result.getData().getData());
                        mMainAdapter.addImagesToFrame(adapterItems);
                    }
                });
        MainAdapter.setLauncher(DL_AlbumLauncher);

    }

    private void setClickListener(){
        mMainImage.setOnLongClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("업로드할 이미지 선택")
                    .setNegativeButton("앨범선택", (dialog, which) -> {
                        if(GetPermission(MainActivity.this ,Manifest.permission.READ_EXTERNAL_STORAGE, PermissionManager.ALBUM_PERMISSION_REQUEST)){
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            MainImageCropLauncher.launch(intent);
                        }
                     })
                    .setPositiveButton("취소", (dialog, which) -> dialog.dismiss()).show();
            return false;
        });



        mMainImage.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), subActivity.class);
            subActivityLauncher.launch(intent);
        });

        mBtn_add.setOnClickListener(view -> {

            String title = mMainInput.getText().toString();

            if(title.length() >0){
                Todoitem newTodo = new Todoitem(title);

                //목록 생성
                if(mMainAdapter.AddTodo(newTodo)) {
                    Toast.makeText(getApplicationContext(), "목록 생성 완료"  , Toast.LENGTH_SHORT).show();
                    mMainInput.setText(null);
                    mMainInput.clearFocus();
                }
                //목록 생성 실패 ==> 겹치는 항목 존재
                else {
                    Toast.makeText(getApplicationContext(), "중복항목 존재" , Toast.LENGTH_SHORT).show();
                }
            }
        });



        mMonthTV.setOnClickListener(v -> {
            if (CustomCalender.RESULT_MONTHS.size() > 0)
                startActivity(new Intent(MainActivity.this, ResultGalleryActivity.class));
            else
                Toast.makeText(getApplicationContext(), "저장된 달력이 없습니다.",Toast.LENGTH_SHORT).show();
        });

    }



    public void getUpdate(){
        int yyyymm = Integer.parseInt(new DateTime().toString("yyyyMM"));
        boolean MonthChanged = CustomCalender.LastAccessYearMonth != yyyymm;
        if(MonthChanged)
            monthChanged( CustomCalender.LastAccessYearMonth);
        mMainImage.setImageBitmap(curCalender.getCalender());
        mMainAdapter =  new MainAdapter(this);
        mMainRV.setAdapter(mMainAdapter);

    }

    public void monthChanged(int lastyyyyMM){

        Dialog_Result dl_result =  new Dialog_Result( MainActivity.this , CustomCalender.LastAccessYearMonth );
        dl_result.show();

        dl_result.setOnDismissListener(dialog -> {
            if( !CustomCalender.RESULT_MONTHS.contains( lastyyyyMM / 100 ))
                CustomCalender.RESULT_MONTHS.add(lastyyyyMM / 100);

            CustomCalender.LastAccessYearMonth = Integer.parseInt(new DateTime().toString("yyyyMM"));
            SharedPreferenceManager.DayLoader dayLoader = new SharedPreferenceManager.DayLoader(getApplicationContext());
            dayLoader.saveDay(CustomCalender.LastAccessYearMonth);

            SharedPreferenceManager.BitmapLoader bl = new SharedPreferenceManager.BitmapLoader(getApplicationContext(), lastyyyyMM);
            SharedPreferenceManager.MarkersLoader ml = new SharedPreferenceManager.MarkersLoader(getApplicationContext(), lastyyyyMM);

            ml.clear();
            bl.clear();


        });
    }


    private void inflateCropView(Uri uri){
        int[] location_view = new int[2];               int[] location_root = new int[2];
        mMainImage.getLocationOnScreen(location_view);  mMainLayout.getLocationOnScreen(location_root);
        int leftMargin =location_view[0];               int topMargin = location_view[1] - location_root[1];
        CropView cv = new CropView(this);
        cv.inflate(mMainLayout, mMainImage.getWidth(), uri, leftMargin, topMargin);
    }

    @Override
    public void cropDone(ImageView cropView) {
        Bitmap cropped_bm = ViewCropperBitmap.getBitmapFromView(MainActivity.this , cropView);
        curCalender.ChangeBasedImg(cropped_bm);
        mMainImage.setImageBitmap(curCalender.getCalender());
        appWidget_calender.cal_imgupdated();
    }
}
