package com.nbmlon.mainmodule;


import static com.nbmlon.managemodule.PermissionManager.GetPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.dialogmodule.record.DL_btnlistener;
import com.nbmlon.dialogmodule.record.Dialog_Records;
import com.nbmlon.dialogmodule.record.ImageUploadingFrame;
import com.nbmlon.managemodule.DBHelper;
import com.nbmlon.managemodule.PermissionManager;
import com.nbmlon.managemodule.Todoitem;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.mainViewHolder>{

    private ArrayList<Todoitem> mTodoList;
    private final Context mContext;
    private final DBHelper mDBHelper;

    private ImageUploadingFrame runningDL_ImageFrame;
    private static ActivityResultLauncher<Intent> launcher;

    public static void setLauncher(ActivityResultLauncher<Intent> launcher_){
        launcher = launcher_;
    }


    public MainAdapter(Context mContext) {
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext, new DateTime());      //오늘 날짜의 dbhelper 호출
        this.mTodoList = mDBHelper.GetLeftTodoList();           //오늘 날짜의 db에서 남은 todolist 뽑아오기 (select문)
    }



    public class mainViewHolder extends RecyclerView.ViewHolder implements DL_btnlistener{
        private Todoitem dstTodoitem;

        private final TextView rv_title;
        private final CheckBox rv_ckbox;
        private final DL_btnlistener dl_btnlistener;

        public mainViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_title = (TextView) itemView.findViewById(R.id.rv_title);
            rv_ckbox = (CheckBox) itemView.findViewById(R.id.rv_ckbox);
            //LinearLayout itembox = (LinearLayout) itemView.findViewById(R.id.itemBox);

            dl_btnlistener = this;

            rv_title.setHorizontallyScrolling(true);
            rv_title.setSelected(true);


            //체크박스 클릭 --> 완료
            rv_ckbox.setOnClickListener(view -> {
                rv_ckbox.setChecked(false);
                dstTodoitem.setEnd_time(new DateTime().toString("hh:mm a"));
                Dialog_Records DL_record = new Dialog_Records(mContext, mDBHelper, dstTodoitem, new DateTime().toString("yyyyMMdd"));
                DL_record.setListener(dl_btnlistener);
                runningDL_ImageFrame = DL_record.getImageFrame();

                DL_record.show();

            });
        }

        @Override
        public void DL_BtnDoneClicked() {
            //update UI
            rv_ckbox.setChecked(true);
            mTodoList.remove(getAdapterPosition());
            rv_ckbox.setChecked(false);
            notifyItemRemoved(getAdapterPosition());
        }

        @Override
        public void DL_GalleryClicked() {
            if(GetPermission((Activity)mContext , Manifest.permission.READ_EXTERNAL_STORAGE, PermissionManager.ALBUM_PERMISSION_REQUEST)){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                launcher.launch(intent);
            }
        }

    }


    @NonNull
    @Override
    public mainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main_rv, parent, false);

        return new mainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull mainViewHolder holder, int position) {
        holder.dstTodoitem = mTodoList.get(position);

        Todoitem todoitem = mTodoList.get(position);
        ((mainViewHolder) holder).rv_title.setText(todoitem.getTitle());
    }

    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    public Boolean AddTodo(Todoitem item){
        if (mDBHelper.InsertTodo(item.getTitle())){
            mTodoList.add(item);
            notifyItemInserted(mTodoList.indexOf(item));
            return true;
        }

        return false;
    }


    public void addImagesToFrame(ArrayList<Uri> uris){
        runningDL_ImageFrame.AddAdapterItems(uris);
    }

}