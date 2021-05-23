package com.example.instagram;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;

public class TabSharePost extends Fragment implements View.OnClickListener {
    private static int PERMISSION_EXTERNAL = 1000;
    private static int IMAGE_CHOSEN_ACTIVITY_CODE = 2000;

    public static String USER_POST_IMAGE = "picture";
    public static String USER_POST_CAPTION = "img_caption";

    private ImageView imgShare;
    private EditText edtCaption;
    private Button btnPostImage;

    private Bitmap receivedImageBitmap;

    public TabSharePost() {
        // Required empty public constructor
    }

    public static TabSharePost newInstance(String param1, String param2) {
        TabSharePost fragment = new TabSharePost();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_share_post, container, false);

        imgShare = view.findViewById(R.id.selectedImage);
        edtCaption = view.findViewById(R.id.edtImageCaption);
        btnPostImage = view.findViewById(R.id.btnPostImage);

        imgShare.setOnClickListener(TabSharePost.this);
        btnPostImage.setOnClickListener(TabSharePost.this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectedImage:
                if (android.os.Build.VERSION.SDK_INT >= 23 &&
                        ActivityCompat.checkSelfPermission(getContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            TabSharePost.PERMISSION_EXTERNAL);
                } else {
                    getChosenImage();
                }
                break;
            case R.id.btnPostImage:
                if (receivedImageBitmap != null) {
                    if (edtCaption.getText().toString().equals("")) {
                        FancyToast.makeText(getContext(), "Caption required.",
                                FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    } else {
                        postImage();
                    }
                } else {
                    FancyToast.makeText(getContext(), "No image selected.",
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                break;
        }
    }

    private void getChosenImage() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, TabSharePost.IMAGE_CHOSEN_ACTIVITY_CODE);
    }

    private void postImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Posting...");
        progressDialog.show();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        receivedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("pic.png", bytes);

        ParseObject parseObject = new ParseObject("Photo");
        parseObject.put(USER_POST_IMAGE, parseFile);
        parseObject.put(USER_POST_CAPTION, edtCaption.getText().toString());
        parseObject.put(TabUsers.USERNAME, ParseUser.getCurrentUser().getUsername());

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    FancyToast.makeText(getContext(), "Posted Successfully.",
                            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                } else {
                    FancyToast.makeText(getContext(), e.getMessage(), FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == TabSharePost.PERMISSION_EXTERNAL && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getChosenImage();
        } else {
            FancyToast.makeText(getContext(), "Storage Permission Denied.",
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CHOSEN_ACTIVITY_CODE && resultCode == Activity.RESULT_OK
            && data != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Processing...");
            try {
                progressDialog.show();
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver()
                        .query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                receivedImageBitmap = BitmapFactory.decodeFile(picturePath);
                imgShare.setImageBitmap(receivedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }
}
