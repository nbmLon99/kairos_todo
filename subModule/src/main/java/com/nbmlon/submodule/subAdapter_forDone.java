package com.nbmlon.submodule;

import static com.nbmlon.managemodule.PermissionManager.GetPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.dialogmodule.display.Dialog_Display;
import com.nbmlon.dialogmodule.record.DL_btnlistener;
import com.nbmlon.dialogmodule.record.Dialog_Records;
import com.nbmlon.dialogmodule.record.ImageUploadingFrame;
import com.nbmlon.managemodule.DBHelper;
import com.nbmlon.managemodule.FileManager;
import com.nbmlon.managemodule.PermissionManager;
import com.nbmlon.managemodule.Todoitem;

import java.util.ArrayList;

public class subAdapter_forDone extends RecyclerView.Adapter<subAdapter_forDone.ViewHolder> implements DialogInterface.OnDismissListener {
    private ArrayList<Todoitem> mTodoList;
    private Context mContext;
    public String mTodoDateString;
    private DBHelper mDBHelper;

    private boolean DL_Detail_Opend = false;
    private Dialog_Display dl_detail;
    private DialogInterface.OnDismissListener dl_dissmiss_listener;
    private static ActivityResultLauncher<Intent> launcher;
    private ImageUploadingFrame runningDL_ImageFrame;


    public subAdapter_forDone(){
        mTodoList = new ArrayList<>();
    }

    public static void setLauncher(ActivityResultLauncher<Intent> launcher_){
        launcher = launcher_;
    }
    public subAdapter_forDone(Context context, String todoDateString, DBHelper mDBHelper) {
        mContext = context;
        this.mTodoDateString = todoDateString;
        this.mDBHelper = mDBHelper;
        this.mTodoList = mDBHelper.GetDoneTodoList();
        dl_dissmiss_listener = this;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        DL_Detail_Opend = false;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, DL_btnlistener {

        Todoitem dstTodoitem;
        ViewHolder itemHolder;

        LinearLayout sub_rv_layout;
        TextView sub_rv_title;
        TextView sub_rv_content;
        TextView sub_rv_time;
        ImageView sub_rv_image;
        LinearLayout sub_rv_content_layout;
        ImageView sub_rv_icon_more;
        ImageView sub_rv_icon_multi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemHolder = this;

            sub_rv_layout = (LinearLayout) itemView.findViewById(R.id.sub_rvDone_Frame);
            sub_rv_title = (TextView) itemView.findViewById(R.id.sub_rvDone_title);
            sub_rv_content = (TextView) itemView.findViewById(R.id.sub_rvDone_content);
            sub_rv_time = (TextView) itemView.findViewById(R.id.sub_rvDone_time);
            sub_rv_image = (ImageView) itemView.findViewById(R.id.sub_rvDone_image);
            sub_rv_icon_more = (ImageView) itemView.findViewById(R.id.sub_rvDone_icon_more);
            sub_rv_icon_multi = (ImageView) itemView.findViewById(R.id.sub_rvDone_icon_multiImg);
            sub_rv_content_layout = (LinearLayout) itemView.findViewById((R.id.sub_rvDone_content_Frame));

            sub_rv_icon_more.setOnCreateContextMenuListener(this);


            sub_rv_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dl_detail = new Dialog_Display(mContext, mTodoDateString, dstTodoitem);
                    dl_detail.setMoreIconListener(itemHolder);
                    dl_detail.setOnDismissListener(dl_dissmiss_listener);
                    dl_detail.show();
                    DL_Detail_Opend = true;
                }
            });

            sub_rv_icon_more.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            v.showContextMenu(event.getX(), event.getY());
                        else
                            v.showContextMenu();
                    }
                    return true;
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
            int position = getAdapterPosition();
            Todoitem todoitem = mTodoList.get(position);

            menu.setHeaderTitle(todoitem.getTitle());

            menu.add(0, v.getId(), 0, "수정").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(DL_Detail_Opend)
                        dl_detail.dismiss();
                    Dialog_Records DL_record  = new Dialog_Records(mContext, mDBHelper, dstTodoitem, mTodoDateString);
                    DL_record.setTitleEditable();
                    DL_record.setListener(itemHolder);
                    runningDL_ImageFrame = DL_record.getImageFrame();
                    DL_record.show();
                    return true;
                }
            });

            menu.add(0, v.getId(), 0, "삭제").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(DL_Detail_Opend)
                        dl_detail.dismiss();
                    mTodoList.remove(position);
                    notifyItemRemoved(position);
                    mDBHelper.DeleteTodo(todoitem);
                    return true;
                }
            });
        }

        @Override
        public void DL_BtnDoneClicked() {
            notifyItemChanged(getAdapterPosition());
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subadapter_fordone, parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewHolder myViewHolder = holder;
        holder.dstTodoitem = mTodoList.get(position);
        Todoitem DstTodoitem = holder.dstTodoitem;

        new Thread((Runnable) () -> {
            int photolen = DstTodoitem.getPhotoLen();
            if (photolen == 0) {
                myViewHolder.sub_rv_image.setVisibility(View.GONE);
            } else {
                try {
                    FileManager.RecordImage fileManager = new FileManager.RecordImage(mContext, mTodoDateString, DstTodoitem.getTitle());
                    ArrayList<Uri> uris = fileManager.getUrisFromFile();
                    DstTodoitem.setImageUris(uris);
                    myViewHolder.sub_rv_image.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myViewHolder.sub_rv_image.setImageURI(uris.get(0));
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    if (photolen > 1)
                        myViewHolder.sub_rv_icon_multi.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).run();

        myViewHolder.sub_rv_title.setText(DstTodoitem.getTitle());
        myViewHolder.sub_rv_content.setText(DstTodoitem.getContent());
        myViewHolder.sub_rv_time.setText(DstTodoitem.getEnd_time());





    }

    @Override
    public int getItemCount() { return mTodoList.size(); }


    public Boolean AddTodo(Todoitem item){
        if(mDBHelper.InsertTodo(item.getTitle())){
            mTodoList.add(item);
            notifyItemInserted(mTodoList.indexOf(item));
            return true;
        }

        return false;
    }




    private void RenameTitle(Todoitem dst_item, String new_title){
        if (mDBHelper.CheckTitleAddable(new_title)) {
            String prev_title = dst_item.getTitle();
            int updateIndex = mTodoList.lastIndexOf(dst_item);

            dst_item.setTitle(new_title);
            mDBHelper.UpdateTodo(prev_title , dst_item);
            notifyItemChanged(updateIndex);
        }

        else Toast.makeText(mContext, "중복항목 존재", Toast.LENGTH_SHORT).show();
    }

    public void addImagesToFrame(ArrayList<Uri> uris){
        runningDL_ImageFrame.AddAdapterItems(uris);
    }
}
