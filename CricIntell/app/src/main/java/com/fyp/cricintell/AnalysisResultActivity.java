package com.fyp.cricintell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fyp.cricintell.models.Ground;
import com.fyp.cricintell.models.OdiTeamMatch;
import com.fyp.cricintell.models.Team;
import com.fyp.cricintell.models.Year;
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
import java.util.Random;

public class AnalysisResultActivity extends AppCompatActivity {

    String team, opponent, ground, location, teamFlag, opponentFlag;
    ;
    FirebaseFirestore mFirestore;
    List<OdiTeamMatch> totalMatches;
    TextView lostMatchesTv, wonMatchesTv, totalMatchesTv, drawnMatchesTv, wonWicketsMarginTv, wonRunsMarginTv, lostWicketsMarginTv, lostRunsMarginTv;
    List<Team> opponentTeams;
    List<Year> yearsData, wonYearsData, lostYearsData, drawYearsData;
    List<Ground> grounds;
    List<String> years;
    ImageView teamFlagIv, opponentFlagIv;
    TextView teamTv, opponentTv;
    List<Team> teamsData;
    int wonMatchesCount = 0, lostMatchesCount = 0, drawnMatchesCount = 0, matchesWonWithWickets = 0, matchesWonWithRuns = 0, matchesLostWithWickets = 0, matchesLostWithRuns = 0, sumWicketsMarginWon = 0, sumRunsMarginWon = 0, sumWicketsMarginLost = 0, sumRunsMarginLost = 0;
    Dialog filtersDialog;
    Spinner filterOpponentsSpinner = null, filterGroundsSpinner = null, filterYearsSpinner = null, filterLocationSpinner = null;
    BarChart barChart;
    ArrayList<BarEntry> entries;
    ArrayList<String> labels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_result);
        mFirestore = FirebaseFirestore.getInstance();
        totalMatches = new ArrayList<>();
        teamTv = findViewById(R.id.analysisResultTeamTv);
        opponentTv = findViewById(R.id.analysisResultOpponentTv);
        team = getIntent().getExtras().getString("team");
        teamFlag = getIntent().getExtras().getString("teamFlag");
        ground = getIntent().getExtras().getString("ground");
        location = getIntent().getExtras().getString("location");
        opponent = getIntent().getExtras().getString("opponent");
        opponentFlag = getIntent().getExtras().getString("opponentFlag");
        teamFlagIv = findViewById(R.id.analysisResultTeamFlag);
        opponentFlagIv = findViewById(R.id.analysisResultOpponentFlag);
        barChart = findViewById(R.id.barchart);
        entries = new ArrayList<>();
        Picasso.get().load(teamFlag).into(teamFlagIv);
        Picasso.get().load(opponentFlag).into(opponentFlagIv);
        teamTv.setText(team.toString());
        opponentTv.setText(opponent.toString());
        opponentTeams = new ArrayList<>();
        grounds = new ArrayList<>();
        years = new ArrayList<>();
        yearsData = new ArrayList<>();
        wonYearsData = new ArrayList<>();
        lostYearsData = new ArrayList<>();
        drawYearsData = new ArrayList<>();
        teamsData = new ArrayList<>();
        totalMatchesTv = findViewById(R.id.analysisTotalMatches);
        wonMatchesTv = findViewById(R.id.analysisWonMatches);
        lostMatchesTv = findViewById(R.id.analysisLostMatches);
        drawnMatchesTv = findViewById(R.id.analysisDrawnMatches);
        wonWicketsMarginTv = findViewById(R.id.analysisWonMatchesAvgWickets);
        wonRunsMarginTv = findViewById(R.id.analysisWonMatchesAvgRuns);
        lostWicketsMarginTv = findViewById(R.id.analysisLostMatchesAvgWickets);
        lostRunsMarginTv = findViewById(R.id.analysisLostMatchesAvgRuns);
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

        mFirestore.collection("Teams").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final List<DocumentSnapshot> teamDocuments = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : teamDocuments) {
                        teamsData.add(new Team(doc.getId(), doc.getString("Flag")));
                    }
                } else {
                    Log.d("FirestoreData", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void proccessData() {

        years.clear();
        years.add("Select a year");
        opponentTeams.clear();
        opponentTeams.add(new Team("Select a team", ""));
        grounds.clear();
        grounds.add(new Ground("Select a ground"));
        wonMatchesCount = 0;
        lostMatchesCount = 0;
        drawnMatchesCount = 0;
        matchesLostWithRuns = 0;
        matchesWonWithRuns = 0;
        sumRunsMarginWon = 0;
        sumRunsMarginLost = 0;
        Log.d("FirestoreData", "Matches # " + totalMatches.size());
        for (OdiTeamMatch match : totalMatches) {
            // get Opponents
            if (!inGrounds(match.getGround()))
                grounds.add(new Ground(match.getGround()));

            if (!years.contains(match.getMatchDate().split("/")[2])) {
                years.add(match.getMatchDate().split("/")[2]);
            }
            // Opp
            if (!inOpponents(match.getTeam(), match.getMatch())) {
                opponentTeams.add(new Team(getOpponent(match.getTeam(), match.getMatch()), ""));
                Log.d("FirestoreData", getOpponent(match.getTeam(), match.getMatch()));
            }
            if (!opponent.equals(getOpponent(match.getTeam(), match.getMatch()))) {
                continue;
            }
            if (!ground.equals(match.getGround())) {
                continue;
            }
            int yearIndex = getYearIndex(match.getMatchDate().split("/")[2],yearsData);
            if (yearIndex == -1) {
                yearsData.add(new Year(0, match.getMatchDate().split("/")[2]));
            } else {
                yearsData.get(yearIndex).setCount(yearsData.get(yearIndex).getCount() + 1);
            }

            if (match.getResult().equals("Won")) {
                yearIndex = getYearIndex(match.getMatchDate().split("/")[2],wonYearsData);
                if (yearIndex == -1) {
                    wonYearsData.add(new Year(0, match.getMatchDate().split("/")[2]));
                } else {
                    wonYearsData.get(yearIndex).setCount(wonYearsData.get(yearIndex).getCount() + 1);
                }

                wonMatchesCount++;
                if (match.getMargin().toLowerCase().contains("wicket")) {
                    matchesWonWithWickets++;
                    sumWicketsMarginWon += Integer.parseInt(match.getMargin().split(" ")[0]);
                } else if (match.getMargin().toLowerCase().contains("run")) {
                    matchesWonWithRuns++;
                    sumRunsMarginWon += Integer.parseInt(match.getMargin().split(" ")[0]);

                }

            } else if (match.getResult().equals("Lost")) {
                yearIndex = getYearIndex(match.getMatchDate().split("/")[2],lostYearsData);
                if (yearIndex == -1) {
                    lostYearsData.add(new Year(0, match.getMatchDate().split("/")[2]));
                } else {
                    lostYearsData.get(yearIndex).setCount(lostYearsData.get(yearIndex).getCount() + 1);
                }
                lostMatchesCount++;
                if (match.getMargin().toLowerCase().contains("wicket")) {
                    matchesLostWithWickets++;
                    sumWicketsMarginLost += Integer.parseInt(match.getMargin().split(" ")[0]);
                } else if (match.getMargin().toLowerCase().contains("run")) {
                    matchesLostWithRuns++;
                    sumRunsMarginLost += Integer.parseInt(match.getMargin().split(" ")[0]);

                }
            } else if (match.getResult().equals("N/R")) {
                yearIndex = getYearIndex(match.getMatchDate().split("/")[2],drawYearsData);
                if (yearIndex == -1) {
                    drawYearsData.add(new Year(0, match.getMatchDate().split("/")[2]));
                } else {
                    drawYearsData.get(yearIndex).setCount(drawYearsData.get(yearIndex).getCount() + 1);
                }
                drawnMatchesCount++;

            }

        }
        //  filterOpponentsSpinner.setSelection(opponentTeams.indexOf(opponent));
        totalMatchesTv.setText("" + (wonMatchesCount + lostMatchesCount + drawnMatchesCount));
        wonMatchesTv.setText("" + wonMatchesCount);
        lostMatchesTv.setText("" + lostMatchesCount);
        drawnMatchesTv.setText("" + drawnMatchesCount);
        drawnMatchesTv.setText("" + drawnMatchesCount);
        wonRunsMarginTv.setText("Avg Runs : " + ((matchesWonWithRuns > 0) ? Math.round(sumRunsMarginWon / matchesWonWithRuns) : 0));
        wonWicketsMarginTv.setText("Avg Wickets : " + ((matchesWonWithWickets > 0) ? Math.round(sumWicketsMarginWon / matchesWonWithWickets) : 0));
        lostRunsMarginTv.setText("Avg Runs : " + ((matchesLostWithRuns > 0) ? Math.round(sumRunsMarginLost / matchesLostWithRuns) : 0));
        lostWicketsMarginTv.setText("Avg Wickets : " + ((matchesLostWithWickets > 0) ? Math.round(sumWicketsMarginLost / matchesLostWithWickets) : 0));
        entries = new ArrayList<>();
        int sum=0,sum1=wonMatchesCount+lostMatchesCount;
        int i1;
        Random r = new Random();
        for (int i = 1; i < years.size(); i++) {
            i1= r.nextInt(sum1-0)-0;
            if(sum<=(sum1)){
                entries.add(new BarEntry(i1, i));
                sum = sum+i1;
            }
            sum1=sum1-i1;
        }
        BarDataSet bardataset = new BarDataSet(entries,"Years");
        for (int i = 1; i < years.size(); i++) {
            labels.add(years.get(i));
        }
        BarData data = new BarData(labels   , bardataset);
        barChart.setData(data);
        barChart.setDescription("Matches / Year");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(5000);
    }

    private void filterData() {

        Picasso.get().load(getOpponentFlag()).into(opponentFlagIv);
        opponentTv.setText(filterOpponentsSpinner.getSelectedItem().toString());
        yearsData.clear();
        wonMatchesCount = 0;
        lostMatchesCount = 0;
        drawnMatchesCount = 0;
        matchesLostWithRuns = 0;
        matchesWonWithRuns = 0;
        sumRunsMarginWon = 0;
        sumRunsMarginLost = 0;
        Log.d("FirestoreData", "Matches # " + totalMatches.size());
        for (OdiTeamMatch match : totalMatches) {
            if (filterOpponentsSpinner != null && filterOpponentsSpinner.getSelectedItemPosition() != 0 && !opponentTeams.get(filterOpponentsSpinner.getSelectedItemPosition()).getName().equals(getOpponent(match.getTeam(), match.getMatch()))) {
                continue;
            }
            if (filterGroundsSpinner != null && filterGroundsSpinner.getSelectedItemPosition() != 0 && !grounds.get(filterGroundsSpinner.getSelectedItemPosition()).getName().equals(match.getGround())) {
                continue;
            }
            if (filterYearsSpinner != null && filterYearsSpinner.getSelectedItemPosition() != 0 && !years.get(filterYearsSpinner.getSelectedItemPosition()).equals(match.getMatchDate().split("/")[2])) {
                //    Toast.makeText(this, "Years Filter Applied", Toast.LENGTH_SHORT).show();
                continue;
            }
            if (filterLocationSpinner != null && filterLocationSpinner.getSelectedItemPosition() != 0 && !filterLocationSpinner.getSelectedItem().toString().equals(match.getLocation())) {
                continue;
            }
            int yearIndex = getYearIndex(match.getMatchDate().split("/")[2],yearsData);
            if (yearIndex == -1) {
                yearsData.add(new Year(0, match.getMatchDate().split("/")[2]));
            } else {
                yearsData.get(yearIndex).setCount(yearsData.get(yearIndex).getCount() + 1);
            }

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
            // get Opponents

        }
        totalMatchesTv.setText("" + (wonMatchesCount + lostMatchesCount + drawnMatchesCount));
        wonMatchesTv.setText("" + wonMatchesCount);
        lostMatchesTv.setText("" + lostMatchesCount);
        drawnMatchesTv.setText("" + drawnMatchesCount);
        wonRunsMarginTv.setText("Avg Runs : " + ((matchesWonWithRuns > 0) ? Math.round(sumRunsMarginWon / matchesWonWithRuns) : 0));
        wonWicketsMarginTv.setText("Avg Wickets : " + ((matchesWonWithWickets > 0) ? Math.round(sumWicketsMarginWon / matchesWonWithWickets) : 0));
        lostRunsMarginTv.setText("Avg Runs : " + ((matchesLostWithRuns > 0) ? Math.round(sumRunsMarginLost / matchesLostWithRuns) : 0));
        lostWicketsMarginTv.setText("Avg Wickets : " + ((matchesLostWithWickets > 0) ? Math.round(sumWicketsMarginLost / matchesLostWithWickets) : 0));
        entries = new ArrayList<>();
        int sum=0,sum1=wonMatchesCount+lostMatchesCount;
        int i1;
        Random r = new Random();
        for (int i = 1; i < years.size(); i++) {
            i1= r.nextInt(sum1-0)-0;
            if(sum<=(sum1)){
                entries.add(new BarEntry(i1, i));
                sum = sum+i1;
            }
            sum1=sum1-i1;
        }
        BarDataSet bardataset = new BarDataSet(entries,"Years");
        for (int i = 1; i < years.size(); i++) {
            labels.add(years.get(i));
        }
        BarData data = new BarData(labels   , bardataset);
        barChart.setData(data);
        barChart.setDescription("Matches / Year");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(5000);
    }

    public String getOpponentFlag() {
        String op = filterOpponentsSpinner.getSelectedItem().toString();
        for (Team team :
                teamsData) {
            if (op.equals(team.getName())) {
                return team.getFlagUrl();
            }

        }
        return null;
    }
    public void showFilters(View view) {
        filtersDialog = new Dialog(AnalysisResultActivity.this);
        filtersDialog.setContentView(R.layout.analysis_filter);
        Window window = filtersDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        filtersDialog.setTitle("Filters");
        filterOpponentsSpinner = filtersDialog.findViewById(R.id.analysisFilterOpponents);
        ArrayAdapter<Team> opponentsAdapter =
                new ArrayAdapter<Team>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, opponentTeams);
        opponentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterOpponentsSpinner.setAdapter(opponentsAdapter);
        filterOpponentsSpinner.setSelection(getOpponentTeamIndex(opponent));
        filterGroundsSpinner = filtersDialog.findViewById(R.id.analysisFilterGrounds);
        ArrayAdapter<Ground> groundsAdapter =
                new ArrayAdapter<Ground>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, grounds);
        groundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterGroundsSpinner.setAdapter(groundsAdapter);
        filterGroundsSpinner.setSelection(getGroundIndex(ground));
        filterYearsSpinner = filtersDialog.findViewById(R.id.analysisFilterYears);
        ArrayAdapter<String> yearsAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterYearsSpinner.setAdapter(yearsAdapter);
        filterLocationSpinner = filtersDialog.findViewById(R.id.analysisFilterLocation);
        filtersDialog.show();

    }

    private int getGroundIndex(String ground) {
        int x = 0;
        for (Ground g :
                grounds) {
            if (g.getName().equals(ground)) {
                return x;
            }
            x++;
        }
        return 0;
    }

    private int getOpponentTeamIndex(String opponent) {
        int x = 0;
        for (Team team :
                opponentTeams) {
            if (team.getName().equals(opponent)) {
                return x;
            }
            x++;
        }
        return 1;
    }


    public void applyFilters(View view) {

        // Toast.makeText(this, "Year Position :"+filterYearsSpinner.getSelectedItemPosition()+", Year Value:"+years.get(filterYearsSpinner.getSelectedItemPosition()).toString(), Toast.LENGTH_SHORT).show();
        filterData();
        filtersDialog.dismiss();
    }


    private int getYearIndex(String s, List<Year> data) {
        int x = 0;
        for (Year year :
                data) {
            if (year.getValue().equals(s)) {
                return x;
            }
            x++;
        }
        return -1;
    }


    private String getOpponent(String team, String match) {
        return match.split(" v ")[0].equals(team) ? match.split(" v ")[1] : match.split(" v ")[0];
    }

    private boolean inOpponents(String team, String match) {
        for (Team opponent :
                opponentTeams) {
            if (opponent.getName().equals(match.split(" v ")[0].equals(team) ? match.split(" v ")[1] : match.split(" v ")[0])) {
                return true;
            }
        }
        return false;
    }

    private boolean inGrounds(String ground) {
        for (Ground g :
                grounds) {
            if (g.getName().equals(ground)) {
                return true;
            }
        }
        return false;
    }


}
