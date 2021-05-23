package com.example.instagram;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class TabProfile extends Fragment {
    public static String PROFILE_NAME_KEY = "name";
    public static String PROFILE_BIO_KEY = "bio";
    public static String PROFILE_PROFESSION_KEY = "profession";
    public static String PROFILE_HOBBIES_KEY = "hobbies";
    public static String PROFILE_FAVSPORT_KEY = "favSport";

    private EditText edtProfileName, edtBio, edtProfession, edtHobbies, edtFavSport;
    private Button btnUpdateInfo;

    private ParseUser currentUser;

    public TabProfile() {
        // Required empty public constructor
    }

    public static TabProfile newInstance() {
        TabProfile fragment = new TabProfile();
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
        View view = inflater.inflate(R.layout.fragment_tab_profile, container, false);
        edtProfileName = view.findViewById(R.id.edtProfileName);
        edtBio = view.findViewById(R.id.edtProfileBio);
        edtProfession = view.findViewById(R.id.edtProfileProfession);
        edtHobbies = view.findViewById(R.id.edtProfileHobbies);
        edtFavSport = view.findViewById(R.id.edtProfileFavourite);

        btnUpdateInfo = view.findViewById(R.id.btnProfileUpdate);

        currentUser = ParseUser.getCurrentUser();

        Object name, bio, profession, hobbies, favSport;
        name = currentUser.get(PROFILE_NAME_KEY);
        bio = currentUser.get(PROFILE_BIO_KEY);
        profession = currentUser.get(PROFILE_PROFESSION_KEY);
        hobbies = currentUser.get(PROFILE_HOBBIES_KEY);
        favSport = currentUser.get(PROFILE_FAVSPORT_KEY);

        edtProfileName.setText(name == null ? "" : name.toString());
        edtBio.setText(bio == null ? "" : bio.toString());
        edtProfession.setText(profession == null ? "" : profession.toString());
        edtHobbies.setText(hobbies == null ? "" : hobbies.toString());
        edtFavSport.setText(favSport == null ? "" : favSport.toString());

        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        return view;
    }

    private void updateUserInfo() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating Info.");
        progressDialog.show();

        currentUser.put(PROFILE_NAME_KEY, edtProfileName.getText().toString());
        currentUser.put(PROFILE_BIO_KEY, edtBio.getText().toString());
        currentUser.put(PROFILE_PROFESSION_KEY, edtProfession.getText().toString());
        currentUser.put(PROFILE_HOBBIES_KEY, edtHobbies.getText().toString());
        currentUser.put(PROFILE_FAVSPORT_KEY, edtFavSport.getText().toString());

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    FancyToast.makeText(getContext(), "Profile Updated.",
                            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,
                            false).show();
                } else {
                    FancyToast.makeText(getContext(), e.getMessage(),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR,
                            false).show();
                }
            }
        });
    }
}
