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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fyp.cricintell.models.Ground;
import com.fyp.cricintell.models.OdiTeamMatch;
import com.fyp.cricintell.models.Player;
import com.fyp.cricintell.models.PlayerInnings;
import com.fyp.cricintell.models.Team;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailsActivity extends AppCompatActivity {

    String team, teamFlag;
    TextView teamTv;
    ImageView teamFlagIv;
    ListView teamPlayersListView;
    List<String> teamPlayers;
    List<PlayerInnings> teamPlayerInnings;
    int wonMatchesCount = 0, lostMatchesCount = 0, drawnMatchesCount = 0, matchesWonWithWickets = 0, matchesWonWithRuns = 0, matchesLostWithWickets = 0, matchesLostWithRuns = 0, sumWicketsMarginWon = 0, sumRunsMarginWon = 0, sumWicketsMarginLost = 0, sumRunsMarginLost = 0;
    FirebaseFirestore mFirestore;
    List<OdiTeamMatch> totalMatches;
    TextView lostMatchesTv, wonMatchesTv, totalMatchesTv, drawnMatchesTv, wonWicketsMarginTv, wonRunsMarginTv, lostWicketsMarginTv, lostRunsMarginTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);
        teamFlagIv = findViewById(R.id.teamDetailsFlag);
        teamTv = findViewById(R.id.teamDetailsName);
        team = getIntent().getExtras().getString("team");
        teamFlag = getIntent().getExtras().getString("teamFlag");
        Picasso.get().load(teamFlag).into(teamFlagIv);
        teamTv.setText(team);
        teamPlayers = new ArrayList<>();
        teamPlayerInnings = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        totalMatches = new ArrayList<>();
        totalMatchesTv = findViewById(R.id.teamTotalMatches);
        wonMatchesTv = findViewById(R.id.teamWonMatches);
        lostMatchesTv = findViewById(R.id.teamLostMatches);
        drawnMatchesTv = findViewById(R.id.teamDrawnMatches);
        wonWicketsMarginTv = findViewById(R.id.teamWonMatchesAvgWickets);
        wonRunsMarginTv = findViewById(R.id.teamWonMatchesAvgRuns);
        lostWicketsMarginTv = findViewById(R.id.teamLostMatchesAvgWickets);
        lostRunsMarginTv = findViewById(R.id.teamLostMatchesAvgRuns);
        teamPlayersListView = findViewById(R.id.teamPlayersListView);
        mFirestore.collection("ODIMatches").whereEqualTo("Country", team).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        totalMatches.add(new OdiTeamMatch(doc.getId(), doc.getString("Country"), doc.getString("Ground"), doc.getString("Location"), doc.getString("Margin"), doc.getString("Match"), doc.getString("MatchDate"), doc.getString("Result")));
                    }
                    proccessData();
                } else {
                    Log.d("FirestoreData", "Error getting documents: ", task.getException());
                }
            }
        });


    }


    private void proccessData() {


        wonMatchesCount = 0;
        lostMatchesCount = 0;
        drawnMatchesCount = 0;
        matchesLostWithRuns = 0;
        matchesWonWithRuns = 0;
        sumRunsMarginWon = 0;
        sumRunsMarginLost = 0;

        Log.d("FirestoreData", "Matches # " + totalMatches.size());
        for (OdiTeamMatch match : totalMatches) {


            if (match.getResult().equals("Won")) {
                wonMatchesCount++;
                if (match.getMargin().toLowerCase().contains("wicket")) {
                    matchesWonWithWickets++;
                    sumWicketsMarginWon += Integer.parseInt(match.getMargin().split(" ")[0]);
                } else if (match.getMargin().toLowerCase().contains("run")) {
                    matchesWonWithRuns++;
                    sumRunsMarginWon += Integer.parseInt(match.getMargin().split(" ")[0]);

                }

            } else if (match.getResult().equals("Lost")) {
                lostMatchesCount++;
                if (match.getMargin().toLowerCase().contains("wicket")) {
                    matchesLostWithWickets++;
                    sumWicketsMarginLost += Integer.parseInt(match.getMargin().split(" ")[0]);
                } else if (match.getMargin().toLowerCase().contains("run")) {
                    matchesLostWithRuns++;
                    sumRunsMarginLost += Integer.parseInt(match.getMargin().split(" ")[0]);

                }
            } else if (match.getResult().equals("N/R")) {
                drawnMatchesCount++;

            }

        }
        totalMatchesTv.setText("" + (wonMatchesCount + lostMatchesCount + drawnMatchesCount));
        wonMatchesTv.setText("" + wonMatchesCount);
        lostMatchesTv.setText("" + lostMatchesCount);
        drawnMatchesTv.setText("" + drawnMatchesCount);
        drawnMatchesTv.setText("" + drawnMatchesCount);
        wonRunsMarginTv.setText("Avg Runs : " + ((matchesWonWithRuns > 0) ? Math.round(sumRunsMarginWon / matchesWonWithRuns) : 0));
        wonWicketsMarginTv.setText("Avg Wickets : " + ((matchesWonWithWickets > 0) ? Math.round(sumWicketsMarginWon / matchesWonWithWickets) : 0));
        lostRunsMarginTv.setText("Avg Runs : " + ((matchesLostWithRuns > 0) ? Math.round(sumRunsMarginLost / matchesLostWithRuns) : 0));
        lostWicketsMarginTv.setText("Avg Wickets : " + ((matchesLostWithWickets > 0) ? Math.round(sumWicketsMarginLost / matchesLostWithWickets) : 0));
        mFirestore.collection("ODIPlayers").whereEqualTo("Country", team).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    Log.d("PLAYERS", Integer.toString(documents.size()));
                    for (DocumentSnapshot doc : documents) {
                        if (!teamPlayers.contains(doc.getString("Player"))) {
                            teamPlayers.add(doc.getString("Player"));
                        }
                    }
                    teamPlayersListView.setAdapter(new ArrayAdapter<String>(TeamDetailsActivity.this, R.layout.support_simple_spinner_dropdown_item, teamPlayers));

                } else {
                    Log.d("FirestoreData", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}