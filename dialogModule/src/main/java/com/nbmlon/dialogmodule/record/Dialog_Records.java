package com.nbmlon.dialogmodule.record;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nbmlon.dialogmodule.R;
import com.nbmlon.managemodule.DBHelper;
import com.nbmlon.managemodule.FileManager;
import com.nbmlon.managemodule.Todoitem;

import java.util.ArrayList;

public class Dialog_Records extends Dialog {

    private TextView mTitle;
    private EditText mTitle_ET;
    private EditText mContent;
    private TextView mRecordsTime;

    private ImageUploadingFrame mImages;
    private ImageView mBtn_Gallery;
    private ImageView mBtn_Done;
    private ImageView mBtn_Cancel;

    private DL_btnlistener dl_btnlistener;

    private final String  mDstTodoDateString;
    private final DBHelper mDstDBhelper;
    private final Todoitem mDstTodoitem;


    public Dialog_Records(@NonNull Context context, DBHelper dstDBhelper, Todoitem dstTodoitem, String dstDateString) {
        super(context, R.style.dialogTheme_nofloating);
        setContentView(R.layout.dialog_records);

        mDstDBhelper = dstDBhelper;
        mDstTodoitem = dstTodoitem;
        mDstTodoDateString = dstDateString;
        Init();
        new Thread(this::SetInit).start();


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setAttributes(params);


    }

    private void Init(){
        mTitle = findViewById(R.id.dl_tv_title);
        mTitle_ET = findViewById(R.id.dl_et_title);
        mContent = findViewById(R.id.dl_et_content);
        mRecordsTime = findViewById(R.id.dl_tv_time);
        mImages = findViewById(R.id.dl_HR_images);
        mBtn_Gallery = findViewById(R.id.dl_btn_gallery);
        mBtn_Done = findViewById(R.id.dl_btn_done);
        mBtn_Cancel = findViewById(R.id.dl_btn_cancel);

    }

    private void SetInit(){
        mTitle.setHorizontallyScrolling(true);
        mTitle.setSelected(true);

        mTitle.setText(mDstTodoitem.getTitle());
        mContent.setText(mDstTodoitem.getContent());
        mRecordsTime.setText(mDstTodoitem.getEnd_time());
        if ( mDstTodoitem.getPhotoLen() > 0 )
            mImages.postDelayed(() -> {
                mImages.AddAdapterItems(mDstTodoitem.getImageUris());
                mImages.SetImageUpdateListener();
            },100);

        mBtn_Gallery.setOnClickListener(v -> {
            if(mImages.mAdapter.getItemCount() < 5)
                dl_btnlistener.DL_GalleryClicked();
            else
                Toast.makeText(getContext(),"이미지는 최대 5장까지 가능합니다.",Toast.LENGTH_SHORT).show();
        });


        //사진이랑 Content 저장하여 Done 도장.
        mBtn_Done.setOnClickListener(v -> {
            String prevTitle = mDstTodoitem.getTitle();
            String inputTitle = mTitle_ET.getVisibility() == View.VISIBLE ?  mTitle_ET.getText().toString() : prevTitle;

            if ( inputTitle.length() > 0){
                boolean TITLE_CHANGED = !inputTitle.equals(prevTitle);

                if( !TITLE_CHANGED || mDstDBhelper.CheckTitleAddable(inputTitle)){

                    String inputContent = mContent.getText().toString();

                    mDstTodoitem.setTitle(inputTitle);
                    mDstTodoitem.setContent(inputContent);
                    int photoLen = mImages.mAdapter.getItemCount();
                    if (mImages.IMAGE_UPDATED() || TITLE_CHANGED){
                        try {
                            mDstTodoitem.setPhotoLen(photoLen);
                            ArrayList<Uri> uploaded_uris = mImages.getUris();
                            //사진 저장해야함
                            if (photoLen > 0) {
                                FileManager.RecordImage fileManager = new FileManager.RecordImage(getContext(), mDstTodoDateString, inputTitle);
                                fileManager.saveImageUrisToJPEG(uploaded_uris);
                                mDstTodoitem.setImageUris(uploaded_uris);
                            }
                            if (TITLE_CHANGED) {
                                FileManager.RecordImage fileManager_prev =  new FileManager.RecordImage(getContext(), mDstTodoDateString ,prevTitle);
                                fileManager_prev.deleteSavedFile();
                            }

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    mDstTodoitem.setDone(true);
                    mDstDBhelper.UpdateTodo(prevTitle, mDstTodoitem);

                    dismiss();
                    Toast.makeText(getContext(),"완료" , Toast.LENGTH_SHORT).show();
                    dl_btnlistener.DL_BtnDoneClicked();
                }
                else
                    Toast.makeText(getContext(), "중복 항목 존재",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(), "제목을 입력해주세요",Toast.LENGTH_SHORT).show();


        });

        mBtn_Cancel.setOnClickListener(v -> dismiss());

    }

    public void setListener(DL_btnlistener dl_btnlistener){
        this.dl_btnlistener = dl_btnlistener;
    }

    public void setTitleEditable(){
        mTitle.post(() -> {
            mTitle.setVisibility(View.GONE);
            mTitle_ET.setVisibility(View.VISIBLE);
            mTitle_ET.setText(mDstTodoitem.getTitle());
        });
    }

    public ImageUploadingFrame getImageFrame(){
        return this.mImages;
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }
}
