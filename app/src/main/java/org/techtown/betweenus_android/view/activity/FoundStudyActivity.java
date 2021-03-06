package org.techtown.betweenus_android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.techtown.betweenus_android.R;
import org.techtown.betweenus_android.base.BaseActivity;
import org.techtown.betweenus_android.databinding.ApplyStudyActivityBinding;
import org.techtown.betweenus_android.databinding.FoundStudyActivityBinding;
import org.techtown.betweenus_android.manager.CurrentUser;
import org.techtown.betweenus_android.manager.Token;
import org.techtown.betweenus_android.manager.ViewModelFactory;
import org.techtown.betweenus_android.model.Member;
import org.techtown.betweenus_android.viewmodel.ApplyStudyViewModel;
import org.techtown.betweenus_android.widget.recyclerview.adapter.StudyListAdapter;

public class FoundStudyActivity extends BaseActivity<FoundStudyActivityBinding> implements NavigationView.OnNavigationItemSelectedListener {

    private ApplyStudyViewModel applyStudyViewModel;

    @Override
    protected int layoutId() {
        return R.layout.found_study_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuSetting();
        initViewModel();
        binding.navView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        binding.studyRecyclerView.setLayoutManager(mGridLayoutManager);

        applyStudyViewModel.getMyStudy();

        applyStudyViewModel.getData().observe(this, studyList -> {
            binding.studyRecyclerView.setAdapter(new StudyListAdapter(studyList.getFoundStudies(), this, this, 1));
        });

        clickEvent();
    }

    private void initViewModel() {
        applyStudyViewModel = ViewModelProviders.of(this, new ViewModelFactory(this)).get(ApplyStudyViewModel.class);
    }

    private void clickEvent() {

        binding.menuBtn.setOnClickListener(v -> binding.main.openDrawer(GravityCompat.START));
    }

    @Override
    public void onBackPressed() {

        if (binding.main.isDrawerOpen(GravityCompat.START)) {
            binding.main.closeDrawers();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.menu_me :
                intent = new Intent(this, MyPageActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_found:
                intent = new Intent(this, FoundStudyActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_apply:
                intent = new Intent(this, ApplyStudyActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_logout:
                new CurrentUser(this,"betweenUs.db",null,2).delete();
                new Token(this).setToken("");
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "문제가 발생하였습니다", Toast.LENGTH_SHORT).show();
        }

        overridePendingTransition(0, 0);
        binding.main.closeDrawers();

        return false;
    }


    private void menuSetting() {

        CurrentUser currentUser = new CurrentUser(this, "betweenUs.db", null, 2);
        Member myInfo = currentUser.getResult();

        ImageView profileImage = (ImageView) binding.navView.getHeaderView(0).findViewById(R.id.profile);
        TextView nameText = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.name);
        TextView schoolText = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.school);

        Log.d("imgTag", myInfo.getprofileImg());
        if (!myInfo.getprofileImg().isEmpty()) {
            Log.d("imgTag", "Pass");
            Glide.with(this).load(myInfo.getprofileImg()).into(profileImage);
        }

        nameText.setText(myInfo.getName());
        schoolText.setText(myInfo.getSchool());
    }
}
