package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class UsersPosts extends AppCompatActivity {
    private LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);

        mLinearLayout = findViewById(R.id.userPostsLinearLayout);

        Intent receivedIntent = getIntent();
        final String receivedUserName = receivedIntent.getStringExtra(TabUsers.USERNAME);

        setTitle(receivedUserName + "'s Posts");

        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Photo");
        parseQuery.whereEqualTo(TabUsers.USERNAME, receivedUserName);
        parseQuery.orderByDescending("createdAt");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject post : objects) {
                        TextView postCaption = new TextView(UsersPosts.this);
                        postCaption.setText(post.get(TabSharePost.USER_POST_CAPTION) + "");
                        ParseFile postPicture = (ParseFile) post.get(TabSharePost.USER_POST_IMAGE);
                        postPicture.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    ImageView postImage = new ImageView(UsersPosts.this);
                                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            200);
                                    imgParams.setMargins(5, 5, 5, 5);
                                    postImage.setLayoutParams(imgParams);
                                    postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    postImage.setImageBitmap(bitmap);
                                    mLinearLayout.addView(postImage, imgParams);

                                    LinearLayout.LayoutParams capParams = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    capParams.setMargins(5, 5, 5, 15);
                                    postCaption.setLayoutParams(capParams);
                                    postCaption.setGravity(Gravity.CENTER);
                                    postCaption.setBackgroundColor(Color.BLUE);
                                    postCaption.setTextColor(Color.WHITE);
                                    postCaption.setTextSize(30f);
                                    mLinearLayout.addView(postCaption, capParams);
                                }
                            }
                        });
                    }
                    progressDialog.dismiss();
                } else {
                    FancyToast.makeText(UsersPosts.this, "No Posts available",
                            FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    progressDialog.dismiss();
                    UsersPosts.this.finish();
                }
            }
        });
    }
}
