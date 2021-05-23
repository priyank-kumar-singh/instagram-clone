package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class TabUsers extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static String USERNAME = "username";

    private ListView mListView;
    private ArrayList<String> mArrayList;
    private ArrayAdapter<String> mArrayAdapter;

    public TabUsers() {
        // Required empty public constructor
    }

    public static TabUsers newInstance(String param1, String param2) {
        TabUsers fragment = new TabUsers();
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

        View view = inflater.inflate(R.layout.fragment_tab_users, container, false);
        mListView = view.findViewById(R.id.usersListView);
        mArrayList = new ArrayList();
        mArrayAdapter =
                new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mArrayList);

        mListView.setOnItemClickListener(TabUsers.this);
        mListView.setOnItemLongClickListener(TabUsers.this);

        final LinearLayout linearLayout = view.findViewById(R.id.loadingUsersData);

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo(USERNAME, ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser user : objects) {
                        mArrayList.add(user.getUsername());
                    }
                    mListView.setAdapter(mArrayAdapter);
                    linearLayout.animate().alpha(0).setDuration(1000);
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    FancyToast.makeText(getContext(), e.getMessage(), FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR, false).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), UsersPosts.class);
        intent.putExtra(USERNAME, mArrayList.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereEqualTo(USERNAME, mArrayList.get(position));
        parseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    PrettyDialog prettyDialog = new PrettyDialog(getContext());
                    prettyDialog.setTitle(user.getUsername() + "'s Info")
                            .setMessage(user.get(TabProfile.PROFILE_BIO_KEY) + "\n"
                                    + user.get(TabProfile.PROFILE_PROFESSION_KEY) + "\n"
                                    + user.get(TabProfile.PROFILE_HOBBIES_KEY) + "\n"
                                    + user.get(TabProfile.PROFILE_FAVSPORT_KEY) + "\n")
                            .setIcon(R.drawable.person)
                            .addButton("OK",
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            prettyDialog.dismiss();
                                        }
                                    });
                    prettyDialog.show();
                }
            }
        });
        return true;
    }
}
