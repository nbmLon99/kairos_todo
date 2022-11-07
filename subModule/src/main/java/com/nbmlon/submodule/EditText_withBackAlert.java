package com.nbmlon.submodule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EditText_withBackAlert extends androidx.appcompat.widget.AppCompatEditText {

    private InputMethodManager imm;
    private inputListener il;
    private boolean ALERT_OPEN = false;

    public EditText_withBackAlert(@NonNull Context context) {
        this(context,null);
    }

    public EditText_withBackAlert(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        imm = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                il.inputDone(EditText_withBackAlert.this.getId());
                return true;
            }
            return false;
        });
    }

    public void setInputListener(inputListener il) {

        this.il = il;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused){
            il.inputStart(this.getId());
            if(imm.isActive(EditText_withBackAlert.this))
                imm.showSoftInput(EditText_withBackAlert.this ,InputMethodManager.SHOW_FORCED);
            else
                imm.toggleSoftInput(getPaintFlags(),0);
        }
        else{
            il.inputCancel(this.getId());
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }

    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if( !ALERT_OPEN){
                // User has pressed Back key. So hide the keyboard
               imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
               new AlertDialog.Builder(getContext())
                        .setTitle("작성을 중지하시겠습니까?")
                        .setNegativeButton("중지", (dialog, which) -> {
                            clearFocus();
                            getText().clear();
                            dialog.dismiss();
                            il.inputCancel(EditText_withBackAlert.this.getId());
                        }).setPositiveButton("계속 작성하기", (dialog, which) -> {
                            dialog.dismiss();
                            imm.showSoftInput(EditText_withBackAlert.this ,InputMethodManager.SHOW_IMPLICIT);
                        }).setOnKeyListener((dialog, keyCode1, event1) -> {
                            if(keyCode1 == KeyEvent.KEYCODE_BACK)
                                return true;
                            return false;
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ALERT_OPEN =false;
                            }
                        }).setCancelable(false).show();
            ALERT_OPEN = true;
            }
        }

        return true;
    }

}
