package com.nbmlon.mainmodule.result;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nbmlon.dialogmodule.R;
import com.nbmlon.mainmodule.mainimage.myCalender;
import com.nbmlon.managemodule.FileManager;

import java.io.IOException;

public class Dialog_Result extends Dialog {

    private final Context mContext;
    private final int mYear;
    private final int mMonth;

    private final TextView tvTotal;
    private final TextView tvWin;
    private final TextView tvDrw;
    private final TextView tvLos;

    private Bitmap resBitmap;

    public Dialog_Result(@NonNull Context context, int yyyyMM) {
        super(context, R.style.dialogTheme_nofloating);
        setContentView(R.layout.dialog_result);

        mContext = context;
        mYear = yyyyMM / 100;
        mMonth = yyyyMM % 100;

        TextView resMonth = findViewById(R.id.dl_res_month);
        ImageView resImage = findViewById(R.id.dl_res_mainImage);
        TextView btnDelete = findViewById(R.id.dl_res_btnDelete);
        TextView btnSave = findViewById(R.id.dl_res_btnSave);

        tvTotal = findViewById(R.id.dl_smr_totalCount);
        tvWin = findViewById(R.id.dl_smr_winCount);
        tvDrw = findViewById(R.id.dl_smr_drwCount);
        tvLos = findViewById(R.id.dl_smr_losCount);

        resMonth.setText(mYear + ". " + mMonth + ".");

        ((Runnable) () -> {
            btnDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("기록 삭제").setMessage("기록 삭제 시, 내용을 다시 불러올 수 없습니다.\n삭제 하시겠습니까?");
                builder.setPositiveButton("삭제", (dialog, which) -> {
                    dialog.dismiss();
                    Dialog_Result.this.dismiss();
                });
                builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileManager.Gallery fm = new FileManager.Gallery(mContext);
                    resImage.post(() -> {
                        try {
                            fm.saveResultBitmap(resBitmap, mYear, mMonth);
                            Dialog_Result.this.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }).run();

        resImage.post(() -> {
            int w = resImage.getWidth();
            myCalender.resCalender lastCalender = new myCalender.resCalender(mContext, w, mYear, mMonth);
            resBitmap = lastCalender.getCalender();
            resImage.setImageBitmap(resBitmap);
            int[] summary = lastCalender.getSummary();
            displaySummary(summary);
        });

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        InsetDrawable inset = new InsetDrawable(new ColorDrawable(Color.TRANSPARENT), 100);
        getWindow().setBackgroundDrawable(inset);
        getWindow().setAttributes(params);

    }

    private void displaySummary(int[] summary){
        int Total_Count = summary[myCalender.resCalender.DAY_COUNT];
        int Win_Count = summary[myCalender.resCalender.WIN_COUNT];
        int Drw_Count = summary[myCalender.resCalender.DRW_COUNT];
        int Los_Count = summary[myCalender.resCalender.LOS_COUNT];


        tvTotal.setText("총 " + Total_Count + " 일.. ");
        tvWin.setText(Win_Count + " 일");
        tvDrw.setText(Drw_Count + " 일");
        tvLos.setText(Los_Count + " 일");
    }


    @Override
    public void onBackPressed() {
    }
}
