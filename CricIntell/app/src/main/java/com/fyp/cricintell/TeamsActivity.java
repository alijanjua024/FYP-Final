package com.fyp.cricintell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeamsActivity extends AppCompatActivity {
    FirebaseFirestore mFirestore;
    ListView teamsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);
        teamsList = findViewById(R.id.teamsListView);
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("Teams").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final List<DocumentSnapshot> teamDocuments = task.getResult().getDocuments();
                    Log.d("FirestoreData", teamDocuments.toString());
                    teamsList.setAdapter(new ListAdapter() {
                        @Override
                        public boolean areAllItemsEnabled() {
                            return false;
                        }

                        @Override
                        public boolean isEnabled(int i) {
                            return false;
                        }

                        @Override
                        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

                        }

                        @Override
                        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

                        }

                        @Override
                        public int getCount() {
                            return teamDocuments.size();
                        }

                        @Override
                        public Object getItem(int i) {
                            return null;
                        }

                        @Override
                        public long getItemId(int i) {
                            return 0;
                        }

                        @Override
                        public boolean hasStableIds() {
                            return false;
                        }

                        @Override
                        public View getView(final int i, View convertView, ViewGroup viewGroup) {
                            View v = convertView;

                            if (v == null) {
                                LayoutInflater vi;
                                vi = LayoutInflater.from(TeamsActivity.this);
                                v = vi.inflate(R.layout.team_list_adapter, null);
                            }
                            ImageView teamLogo = v.findViewById(R.id.teamListAdapterImage);
                            final TextView teamName = v.findViewById(R.id.teamListAdapterTitle);
                            Picasso.get().load(teamDocuments.get(i).getString("Flag")).into(teamLogo);
                            teamName.setText(teamDocuments.get(i).getId());
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(TeamsActivity.this, TeamDetailsActivity.class);
                                    intent.putExtra("team", teamDocuments.get(i).getId().toString());
                                    intent.putExtra("teamFlag", teamDocuments.get(i).getString("Flag"));
                                    startActivity(intent);
                                }
                            });
                            return v;
                        }

                        @Override
                        public int getItemViewType(int i) {
                            return 0;
                        }

                        @Override
                        public int getViewTypeCount() {
                            return teamDocuments.size();

                        }

                        @Override
                        public boolean isEmpty() {
                            return false;
                        }
                    });
                } else {
                    Log.d("FirestoreData", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
