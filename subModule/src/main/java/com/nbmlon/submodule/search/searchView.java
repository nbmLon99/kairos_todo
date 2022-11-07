package com.nbmlon.submodule.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nbmlon.managemodule.DBHelper;
import com.nbmlon.submodule.R;

import java.util.ArrayList;

public class searchView extends FrameLayout {

    private SearchResClickListener listener;

    public void setListener(SearchResClickListener listener) {
        this.listener = listener;
    }

    public searchView(Context context, ViewGroup root_view) {
        super(context);
        DBHelper tmpDB = new DBHelper(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout inflated = (LinearLayout) inflater.inflate(R.layout.layout_search, root_view, false);

        ImageView inputBack = inflated.findViewById(R.id.search_in_btnBack);
        EditText inputET = inflated.findViewById(R.id.search_in_ET);
        ImageView inputClear = inflated.findViewById(R.id.search_in_btnClear);
        ImageView inputSearch = inflated.findViewById(R.id.search_in_btnSearch);

        RadioGroup radioGroup = inflated.findViewById(R.id.search_radioGroup);
        ((RadioButton)inflated.findViewById(R.id.search_btn_ForTitle)).setChecked(true);
        RecyclerView resRV = inflated.findViewById(R.id.searchRes_RV);
        resRV.setLayoutManager(new LinearLayoutManager(context));
        resRV.setAdapter(new searchResRV_adpater());

        LayoutParams params = new LayoutParams(-1,-1);
        inputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    inputClear.setVisibility(VISIBLE);
                }
                else
                    inputClear.setVisibility(GONE);
            }
        });

        inputET.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });
        inputET.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        inputET.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                inputSearch.callOnClick();
                return true;
            }

            return false;
        });

        inputClear.setOnClickListener(v -> inputET.getText().clear());

        inputSearch.setOnClickListener(v -> ((Runnable) () -> {
            if(inputET.getText().toString().length() <= 0 ) {
                Toast.makeText(context, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            String mTargetCol = "Title";
            int clicked = radioGroup.getCheckedRadioButtonId();
            if (clicked == R.id.search_btn_ForContent)
                mTargetCol = "Content";
            else if (clicked == R.id.search_btn_ForTiCon)
                mTargetCol = "TiCon";



            ArrayList<String[]> Rets = tmpDB.Search(inputET.getText().toString(), mTargetCol);
            if (Rets.size() > 0)
                resRV.setAdapter(new searchResRV_adpater(Rets, listener));
            else
                Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        }).run());

        inputBack.setOnClickListener(v -> root_view.removeView(searchView.this));

        setLayoutParams(params);
        setOnTouchListener((v, event) -> true);
        addView(inflated);

        post(() -> {
            inputET.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(inputET ,InputMethodManager.SHOW_IMPLICIT);
        });
    }


}
