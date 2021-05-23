package com.example.instagram;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SocialMediaActivity extends AppCompatActivity {
    private static final int PERMISSION_EXTERNAL = 3000;
    private static final int CAMERA_ACTIVITY_RESULT = 4000;

    private TabAdapter mTabAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mTabAdapter = new TabAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setAdapter(mTabAdapter);

        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.postImageItem:
                if (android.os.Build.VERSION.SDK_INT >= 23 &&
                        ActivityCompat.checkSelfPermission(SocialMediaActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_EXTERNAL);
                } else {
                    captureImage();
                }
                break;
            case R.id.logoutUserItem:
                logoutUser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void captureImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CAMERA_ACTIVITY_RESULT);
    }

    private void logoutUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    SocialMediaActivity.this.finish();
                    Intent intent = new Intent(SocialMediaActivity.this, SignInActivity.class);
                    startActivity(intent);

                } else {
                    FancyToast.makeText(SocialMediaActivity.this, e.getMessage(),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR,false).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_EXTERNAL && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImage();
        } else {
            FancyToast.makeText(this, "Storage Permission Denied.",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_ACTIVITY_RESULT && resultCode == Activity.RESULT_OK &&
                data != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Posting...");
            progressDialog.show();
            try {
                Uri capturedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), capturedImage);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                byte[] bytes = byteArrayOutputStream.toByteArray();
                ParseFile parseFile = new ParseFile("pic.png", bytes);

                ParseObject parseObject = new ParseObject("Photo");
                parseObject.put(TabSharePost.USER_POST_IMAGE, parseFile);
                parseObject.put(TabUsers.USERNAME, ParseUser.getCurrentUser().getUsername());

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        if (e == null) {
                            FancyToast.makeText(SocialMediaActivity.this, "Posted Successfully.",
                                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                        } else {
                            FancyToast.makeText(SocialMediaActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT,
                                    FancyToast.ERROR, false).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
