package com.example.dustbinpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dustbinpro.adapters.IntroSliderAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class IntroSliderActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private IntroSliderAdapter adapter;
    private Button btnNext;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        viewPager = findViewById(R.id.viewPager);
        Button btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new IntroSliderAdapter(this);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Empty as we just want dots
        }).attach();

        btnSkip.setOnClickListener(v -> navigateToLogin());

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                navigateToLogin();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(IntroSliderActivity.this, LoginActivity.class));
        finish();
    }
}