package com.nbmlon.customcalender_myfirstapp.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.nbmlon.customcalender_myfirstapp.MainActivity;
import com.nbmlon.customcalender_myfirstapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {


    private static final ArrayList<Integer> texts = new ArrayList<>(Arrays.asList(
            R.string.tutorial_1,
            R.string.tutorial_2,
            R.string.tutorial_3
    ));


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        ViewPager2 viewPager2 = findViewById(R.id.tutorial_vp);
        TextView tv = findViewById(R.id.tutorial_tv);
        TextView btn_cancel = findViewById(R.id.tutorial_cancel);
        TextView bar = findViewById(R.id.tutorial_bar);


        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(TutorialFragment.getFragment(0));
        adapter.addFragment(TutorialFragment.getFragment(1));
        adapter.addFragment(TutorialFragment.getFragment(2));


        tv.setText(TutorialActivity.this.getApplicationContext().getResources().getString(texts.get(0)));
        bar.setText((1) + "/" + texts.size());

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tv.setText(TutorialActivity.this.getApplicationContext().getResources().getString(texts.get(position)));
                bar.setText((position+1) + "/" + texts.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        btn_cancel.setOnClickListener(v -> {
            startActivity(new Intent(TutorialActivity.this, MainActivity.class));
            TutorialActivity.this.finish();
        });
        viewPager2.setAdapter(adapter);
        //섦영서 오픈

    }

    @Override
    public void onClick(View v) {
        Handler handler = new Handler();
        handler.post(() -> {
            startActivity(new Intent(getApplication(), MainActivity.class));
            TutorialActivity.this.finish();
        });
    }

}

