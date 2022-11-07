package com.nbmlon.dialogmodule.gallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nbmlon.dialogmodule.R;
import com.nbmlon.managemodule.FileManager;

public class Dialog_Gallery extends Dialog {

    public Dialog_Gallery(Context context, DeleteListener dl, Uri uri, int dstYear, int dstMonth){
        super(context, R.style.dialogTheme_nofloating);
        setContentView(R.layout.dialog_gallery);
        TextView tv = findViewById(R.id.gallery_date);
        ImageView btn_delete = findViewById(R.id.gallery_icon_delete);
        ImageView btn_cancel = findViewById(R.id.gallery_icon_back);

        ImageView calender = findViewById(R.id.gallery_calender);

        tv.setText("" + dstYear + "년 " + dstMonth + "월.");
        calender.setAdjustViewBounds(true);
        calender.post(() -> {
            calender.getLayoutParams().height = calender.getWidth();
            calender.setImageURI(uri);
        });

        btn_delete.setOnClickListener(v -> {
            //todo 삭제
            new AlertDialog.Builder(getContext())
                    .setTitle("정말로 삭제하시겠습니까?")
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss()).setPositiveButton("삭제", (dialog, which) -> {
                        FileManager.Gallery fm =  new FileManager.Gallery(getContext());
                        int leftedCount = fm.deleteCalender(dstYear, dstMonth);
                        Toast.makeText(context.getApplicationContext(), "삭제 완료",Toast.LENGTH_SHORT).show();
                        dl.calenderDeleted(dstYear, dstMonth, leftedCount);
                        dialog.dismiss();
                        Dialog_Gallery.this.dismiss();
            }).show();

        });

        btn_cancel.setOnClickListener(v -> Dialog_Gallery.this.dismiss());

    }
}
