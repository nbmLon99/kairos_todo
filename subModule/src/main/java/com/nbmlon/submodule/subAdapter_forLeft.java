package com.nbmlon.submodule;

import android.content.Context;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.managemodule.DBHelper;
import com.nbmlon.managemodule.Todoitem;

import java.util.ArrayList;

public class subAdapter_forLeft extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;


    private ArrayList<Todoitem> mTodoList;
    public String mTodoDate;
    private DBHelper mDBHelper;

    private boolean mIsPast;
    private boolean canModifying = true;

    private inputListener il;

    public void setInputListener(inputListener il) {
        this.il = il;
    }

    //empty_adapter
    public subAdapter_forLeft(){
        mTodoList = new ArrayList<>();
    }


    public subAdapter_forLeft(Context mContext, String mTodoDate, DBHelper mDBHelper) {
        this.mContext = mContext;
        this.mTodoDate = mTodoDate;
        this.mDBHelper = mDBHelper;
        this.mTodoList = mDBHelper.GetLeftTodoList();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, inputListener {
        ViewHolder dst_holder;

        RelativeLayout df_Frame;
        TextView df_title;
        ImageView df_icMore;

        RelativeLayout in_Frame;
        EditText_withBackAlert in_title;
        TextView in_done;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dst_holder = this;

            df_Frame = (RelativeLayout) itemView.findViewById(R.id.sub_rvLeft_DefaultFrame);
            df_title = (TextView) itemView.findViewById(R.id.sub_rvLeft_tv);
            df_icMore = (ImageView) itemView.findViewById(R.id.sub_rvLeft_icon_more);

            in_Frame = (RelativeLayout) itemView.findViewById(R.id.sub_rvLeft_InputFrame);
            in_title =  itemView.findViewById(R.id.sub_rvLeft_etTitle);
            in_done = (TextView) itemView.findViewById(R.id.sub_rvLeft_btnDone);
            if (mIsPast)
                df_icMore.setVisibility(View.GONE);
            else{
                in_title.setInputListener(this);
                df_icMore.setOnCreateContextMenuListener(this);
                df_icMore.setOnTouchListener(new View.OnTouchListener() {
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
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //Left && today or future -> Delete / Rename / Check For Todays Done
            int position = getAdapterPosition();
            Todoitem dstTodo = mTodoList.get(position);

            menu.setHeaderTitle(dstTodo.getTitle());
            menu.add(0, v.getId(), 0, "삭제").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mTodoList.remove(position);
                    notifyItemRemoved(position);
                    mDBHelper.DeleteTodo(dstTodo);
                    return true;
                }
            });

            menu.add(0, v.getId(), 0, "수정").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (canModifying){
                        canModifying = false;
                        df_Frame.setVisibility(View.GONE);
                        in_Frame.setVisibility(View.VISIBLE);
                        in_title.setText(dstTodo.getTitle());
                        in_title.setHint(dstTodo.getTitle());
                        in_title.requestFocus();

                        //ml.setInit(dst_holder, in_title);
                        in_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(in_title.getText().toString().length()>0){
                                    String inputTitle = in_title.getText().toString();
                                    if(inputTitle.equals(dstTodo.getTitle()))
                                        Toast.makeText(mContext, "이전 제목과 동일합니다.", Toast.LENGTH_SHORT).show();

                                    else if(mDBHelper.CheckTitleAddable(inputTitle)){
                                        String prevTitle = dstTodo.getTitle();
                                        dstTodo.setTitle(inputTitle);
                                        mDBHelper.UpdateTodo(prevTitle, dstTodo);
                                        notifyItemChanged(getAdapterPosition());
                                        Toast.makeText(mContext, "변경완료", Toast.LENGTH_SHORT).show();

                                        df_Frame.setVisibility(View.VISIBLE);
                                        in_Frame.setVisibility(View.GONE);
                                        canModifying = true;

                                    }
                                    else
                                        Toast.makeText(mContext, "중복 항목 존재", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(mContext, "수정할 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //ml.startModify();
                    }
                    else
                        Toast.makeText(mContext, "진행 중인 수정 작업을 완료해주세요", Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        @Override
        public void inputCancel(int etID) {
            il.inputCancel(etID);

            in_title.clearFocus();
            in_title.getText().clear();
            in_Frame.setVisibility(View.GONE);
            df_Frame.setVisibility(View.VISIBLE);
            canModifying = true;

        }

        @Override
        public void inputStart(int etId) {
            il.inputStart(etId);
        }

        @Override
        public void inputDone(int etId) {
            in_done.callOnClick();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_detailtodo_rv_left, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myViewHolder = (ViewHolder) holder;
        Todoitem todoitem = mTodoList.get(position);
        myViewHolder.df_title.setText(todoitem.getTitle());
    }

    @Override
    public int getItemCount() { return mTodoList.size(); }


    public boolean AddTodo(String new_title){
        if(mDBHelper.InsertTodo(new_title)){
            mTodoList.add(new Todoitem(new_title));
            notifyItemInserted(mTodoList.size()-1);
            return true;
        }
        else
            return false;
    }

    public void setPast(boolean isPast){
        mIsPast = isPast;
    }

}
