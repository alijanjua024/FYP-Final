package com.fyp.cricintell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.cricintell.database.DBManager;
import com.fyp.cricintell.models.AnalysisLog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnalysisLogActivity extends AppCompatActivity {

    ListView analysisLogList;
    DBManager dbManager;
    List<AnalysisLog> analysisLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_log);
        analysisLogList = findViewById(R.id.analysisLogList);
        dbManager = new DBManager(getApplicationContext());
        dbManager.open();
        analysisLogs = dbManager.fetch();
        analysisLogList.setAdapter(new ListAdapter() {
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
                return analysisLogs.size();
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
                    vi = LayoutInflater.from(AnalysisLogActivity.this);
                    v = vi.inflate(R.layout.analysis_adapter, null);
                }
                ImageView teamLogo = v.findViewById(R.id.teamFlagImage);
                final TextView teamName = v.findViewById(R.id.teamNameTv);
                ImageView opponentLogo = v.findViewById(R.id.opponentFlagImage);
                final TextView opponentName = v.findViewById(R.id.opponentNameTv);
                final TextView groundName = v.findViewById(R.id.groundNameTv);
                final TextView entryType = v.findViewById(R.id.analysisTypeTv);
                final TextView location = v.findViewById(R.id.locationNameTv);
                Picasso.get().load(analysisLogs.get(i).getTeamFlag()).into(teamLogo);
                Picasso.get().load(analysisLogs.get(i).getOpponentFlag()).into(opponentLogo);
                teamName.setText(analysisLogs.get(i).getTeam());
                opponentName.setText(analysisLogs.get(i).getOpponent());
                groundName.setText(analysisLogs.get(i).getGround());
                location.setText("Location: " + analysisLogs.get(i).getLocation());
                if (analysisLogs.get(i).getEntryType() == 1) {
                    entryType.setText("Match Prediction ");

                } else {
                    entryType.setText("Team Analysis");

                }

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (analysisLogs.get(i).getEntryType() == 0) {

                            Intent intent = new Intent(AnalysisLogActivity.this, AnalysisResultActivity.class);
                            intent.putExtra("team", analysisLogs.get(i).getTeam());
                            intent.putExtra("teamFlag", analysisLogs.get(i).getTeamFlag());
                            intent.putExtra("opponent", analysisLogs.get(i).getOpponent());
                            intent.putExtra("opponentFlag", analysisLogs.get(i).getOpponentFlag());
                            intent.putExtra("ground", analysisLogs.get(i).getGround());
                            intent.putExtra("location", analysisLogs.get(i).getLocation());
                            startActivity(intent);
                        }
                        else{

                            Intent intent = new Intent(AnalysisLogActivity.this, PredictionResultActivity.class);
                            intent.putExtra("team", analysisLogs.get(i).getTeam());
                            intent.putExtra("teamFlag", analysisLogs.get(i).getTeamFlag());
                            intent.putExtra("opponent", analysisLogs.get(i).getOpponent());
                            intent.putExtra("opponentFlag", analysisLogs.get(i).getOpponentFlag());
                            intent.putExtra("ground", analysisLogs.get(i).getGround());
                            intent.putExtra("location", analysisLogs.get(i).getLocation());
                            startActivity(intent);
                        }
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
                return 1;

            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        });

    }
}
