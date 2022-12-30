package com.nbmlon.customcalender_myfirstapp;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.managemodule.DBHelper;
import com.nbmlon.submodule.EditText_withBackAlert;
import com.nbmlon.submodule.datepicker.DatePickerListener;
import com.nbmlon.submodule.datepicker.HorizontalPicker;
import com.nbmlon.submodule.datepicker.IconClickListener;
import com.nbmlon.submodule.inputListener;
import com.nbmlon.submodule.search.SearchResClickListener;
import com.nbmlon.submodule.search.searchView;
import com.nbmlon.submodule.subAdapter_forDone;
import com.nbmlon.submodule.subAdapter_forLeft;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class subActivity extends AppCompatActivity implements DatePickerListener, IconClickListener, SearchResClickListener, inputListener {

    private HorizontalPicker mDatePicker;
    private RelativeLayout mRoot_Layout;
    private subAdapter_forDone mAdapter_for_done;
    private subAdapter_forLeft mAdapter_for_left;
    private RecyclerView mDetailTodoRV_ForDone;
    private RecyclerView mDetailTodoRV_ForLeft;
    private LinearLayout mInputFrame;
    private EditText_withBackAlert mInputET;
    private TextView mInputBtn;
    private FrameLayout mUpperFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sub);
        InitSetting();

        CustomCalender.getAds(subActivity.this, findViewById(R.id.sub_adView));



        mDatePicker= (HorizontalPicker) findViewById(R.id.datepicker);

        mDatePicker.setListener(this)
                    .setListener_icon(this)
                    .init();

        mDatePicker.setDate(new DateTime());

        setLauncher();
    }

    private void setLauncher() {
        ActivityResultLauncher<Intent> DL_AlbumLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        ArrayList<Uri> adapterItems = new ArrayList<>();
                        ClipData uris = result.getData().getClipData();
                        for(int i =0; i < uris.getItemCount() ; i++)
                            adapterItems.add(uris.getItemAt(i).getUri());
                        mAdapter_for_done.addImagesToFrame(adapterItems);
                    }
                });
        subAdapter_forDone.setLauncher(DL_AlbumLauncher);
    }

    private void InitSetting() {
        mRoot_Layout = (RelativeLayout) findViewById(R.id.sub_Root_Frame);


        mUpperFrame = findViewById(R.id.sub_upperFrame);
        mDetailTodoRV_ForDone = (RecyclerView) findViewById(R.id.sub_rvDone);
        mDetailTodoRV_ForLeft = (RecyclerView) findViewById(R.id.sub_rvLeft);
        mInputFrame = (LinearLayout) findViewById(R.id.sub_input_Frame);

        mDetailTodoRV_ForDone.setAdapter(new subAdapter_forDone());
        mDetailTodoRV_ForLeft.setAdapter(new subAdapter_forLeft());


        mInputET = mInputFrame.findViewById(R.id.sub_input_et);
        mInputET.setInputListener(this);
        mInputBtn = mInputFrame.findViewById(R.id.sub_input_btn);



        mInputBtn.setOnClickListener(v -> {
            String new_title = mInputET.getText().toString();
            if (new_title.length() <= 0)
                Toast.makeText(getApplicationContext(),"추가할 Todo를 입력해주세요", Toast.LENGTH_SHORT).show();
            else if(mAdapter_for_left.AddTodo(new_title)){
                //키보드를 내려 중지하지 않는 이상 계속 이어 입력 받게 하자
                mInputET.getText().clear();
            }
            else
                Toast.makeText(getApplicationContext(),"중복 항목 존재", Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    public void onDateSelected(DateTime dateSelected) {

        boolean isPast = dateSelected.getMillis() - new DateTime().withTime(0,0,0,0).getMillis() < 0;

        DBHelper dstDBhelper = new DBHelper(this, dateSelected );
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdapter_for_done = new subAdapter_forDone(subActivity.this, dateSelected.toString("yyyyMMdd"), dstDBhelper);
                mDetailTodoRV_ForDone.setAdapter(mAdapter_for_done);
                if (mInputFrame.getVisibility() == View.VISIBLE) {
                    mInputET.post(new Runnable() {
                        @Override
                        public void run() {
                            mInputET.clearFocus();
                            mInputET.getText().clear();
                            mInputFrame.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).run();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdapter_for_left = new subAdapter_forLeft(subActivity.this, dateSelected.toString("yyyyMMdd"), dstDBhelper);
                mDetailTodoRV_ForLeft.setAdapter(mAdapter_for_left);
                mAdapter_for_left.setPast(isPast);
                mAdapter_for_left.setInputListener(subActivity.this);
            }
        }).run();

        mInputFrame.setVisibility(View.GONE);
    }

    @Override
    public void OnAddIconClick() {
        mUpperFrame.setVisibility(View.GONE);
        mInputFrame.setVisibility(View.VISIBLE);
        mInputET.requestFocus();
    }


    @Override
    public void OnSearchIconClick() {
        if (mInputFrame.getVisibility() == View.VISIBLE){
            Toast.makeText(getApplicationContext(), "추가 작업을 종료 후 검색해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        searchView inflated = new searchView(this, mRoot_Layout);
        inflated.setListener(this);
        mRoot_Layout.addView(inflated);
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback( true) {
            @Override
            public void handleOnBackPressed() {
                mRoot_Layout.removeView(inflated);
                setEnabled(false);
                remove();
            }
        });

    }

    @Override
    public void inputCancel(int etID) {
        mUpperFrame.setVisibility(View.VISIBLE);

        if(etID == R.id.sub_input_et){
            mInputET.getText().clear();
            mInputFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void inputStart(int etId) {
    }

    @Override
    public void inputDone(int etId) {
        mInputBtn.callOnClick();
    }


    @Override
    public void ResClicked(String dateString) {
        int y = Integer.parseInt(dateString.substring(0,4));
        int m = Integer.parseInt(dateString.substring(4,6));
        int d = Integer.parseInt(dateString.substring(6));

        mDatePicker.setDate(new DateTime().withDate(y,m,d).withTime(0,0,0,0));
        onBackPressed();
    }

}