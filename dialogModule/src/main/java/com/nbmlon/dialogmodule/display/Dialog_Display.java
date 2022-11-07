package com.nbmlon.dialogmodule.display;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nbmlon.dialogmodule.R;
import com.nbmlon.managemodule.FileManager;
import com.nbmlon.managemodule.Todoitem;

import java.util.ArrayList;


public class Dialog_Display extends Dialog implements View.OnCreateContextMenuListener {

    private String mTodoDateString;
    private Todoitem dstTodoitem;

    public Dialog_Display(@NonNull Context context, String todoDateString, Todoitem dstTodoitem) {
        super(context, R.style.dialogTheme_nofloating);
        mTodoDateString = todoDateString;
        this.dstTodoitem = dstTodoitem;
        setContentView(R.layout.dialog_display);
        new Thread(this::Init).start();
    }


    private void Init(){
        TextView title = findViewById(R.id.detail_title);
        ImagePickerSlide imageRV = findViewById(R.id.detail_imageRV);
        TextView content = findViewById(R.id.detail_content);
        TextView time = findViewById(R.id.detail_time);

        ImageView iconBack = findViewById(R.id.detail_icon_back);

        title.setText(dstTodoitem.getTitle());
        content.setText(dstTodoitem.getContent());
        time.setText(dstTodoitem.getEnd_time());

        FileManager.RecordImage fileManager = new FileManager.RecordImage(getContext(), mTodoDateString, dstTodoitem.getTitle());
        ArrayList<Uri> uris = new ArrayList<>();
        if (dstTodoitem.getPhotoLen() > 0){
            uris = fileManager.getUrisFromFile();
            findViewById(R.id.dialog_dp_top_sepline).setVisibility(View.GONE);
        }
        imageRV.setImageUris(uris);


        iconBack.setOnClickListener(v -> Dialog_Display.this.dismiss());
    }



    public void setMoreIconListener(View.OnCreateContextMenuListener cl){
        ImageView iconMore = findViewById(R.id.detail_icon_more);
        iconMore.post(() -> {
            iconMore.setOnCreateContextMenuListener(cl);
            iconMore.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        v.showContextMenu(event.getX(), event.getY());
                    else
                        v.showContextMenu();
                }
                return true;
            });
        }
        );
    }
}
