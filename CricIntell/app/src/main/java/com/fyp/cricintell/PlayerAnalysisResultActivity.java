package com.fyp.cricintell;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp.cricintell.models.Ground;
import com.fyp.cricintell.models.OdiTeamMatch;
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
import java.util.Objects;
import java.util.Random;

public class PlayerAnalysisResultActivity extends AppCompatActivity {
    BarChart barChart;
    String team, opponent, ground, location, teamFlag, opponentFlag, player;
    FirebaseFirestore mFirestore;
    List<PlayerInnings> playerInnings;
    ArrayList<String> labels = new ArrayList<>();
    List<Team> opponentTeams;
    List<Ground> grounds;
    List<Team> teamsData;
    List<String> years;
    ImageView teamFlagIv, opponentFlagIv;
    TextView teamTv, opponentTv, playerTv, totalMatchesTv, totalRunsTv, strikeRateTv, economyRateTv;
    ArrayList<BarEntry> entries;
    Dialog filtersDialog;
    Spinner filterOpponentsSpinner = null, filterGroundsSpinner = null, filterYearsSpinner = null, filterLocationSpinner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_analysis_result);
        mFirestore = FirebaseFirestore.getInstance();
        totalMatchesTv = findViewById(R.id.playerAnalysisTotalInnings);
        totalRunsTv = findViewById(R.id.playerAnalysisTotalRuns);
        entries = new ArrayList<>();
        strikeRateTv = findViewById(R.id.playerAnalysisStrikeRate);
        economyRateTv = findViewById(R.id.playerAnalysisEconomyRate);
        playerInnings = new ArrayList<>();
        barChart = findViewById(R.id.barchart);
        teamTv = findViewById(R.id.playerAnalysisResultTeamTv);
        opponentTv = findViewById(R.id.playerAnalysisResultOpponentTv);
        playerTv = findViewById(R.id.playerAnalysisResultPlayer);
        team = getIntent().getExtras().getString("team");
        teamFlag = getIntent().getExtras().getString("teamFlag");
        ground = getIntent().getExtras().getString("ground");
        player = getIntent().getExtras().getString("player");
        location = getIntent().getExtras().getString("location");
        opponent = getIntent().getExtras().getString("opponent");
        opponentFlag = getIntent().getExtras().getString("opponentFlag");
        teamFlagIv = findViewById(R.id.playerAnalysisResultTeamFlag);
        opponentFlagIv = findViewById(R.id.playerAnalysisResultOpponentFlag);
        Picasso.get().load(teamFlag).into(teamFlagIv);
        Picasso.get().load(opponentFlag).into(opponentFlagIv);
        teamTv.setText(team.toString());
        opponentTv.setText(opponent.toString());
        playerTv.setText(player.toString());
        opponentTeams = new ArrayList<>();
        grounds = new ArrayList<>();
        years = new ArrayList<>();
        teamsData = new ArrayList<>();
        mFirestore.collection("ODIPlayers").whereEqualTo("Country", team).whereEqualTo("Player", player).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : documents) {
                        playerInnings.add(new PlayerInnings(doc.getId(), doc.getString("Player"), doc.getString("Country"), doc.getString("Opposition").substring(2), doc.getString("Ground"), doc.getString("InningsDate"), (doc.get("Innings Runs Scored Num").toString().equals("") || doc.get("Innings Runs Scored Num").toString().equals("-")) ? 0 : Float.parseFloat(doc.get("Innings Runs Scored Num").toString()), (doc.get("InningsEconomyRate").toString().equals("") || doc.get("InningsEconomyRate").toString().equals("-")) ? 0 : Float.parseFloat(doc.get("InningsEconomyRate").toString()),(doc.get("InningsBattingStrikeRate").toString().equals("") || doc.get("InningsBattingStrikeRate").toString().equals("-")) ? 0 : Float.parseFloat(doc.get("InningsBattingStrikeRate").toString())));
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

    public void proccessData() {
        float totalRuns = 0;
        float strikeRate = 0;
        float economyRate = 0;
        int totalMatches = 0;
        years.clear();
        years.add("Select a year");
        opponentTeams.clear();
        opponentTeams.add(new Team("Select a team", ""));
        grounds.clear();
        grounds.add(new Ground("Select a ground"));
        for (PlayerInnings p : playerInnings) {

            if (!inGrounds(p.getGround()))
                grounds.add(new Ground(p.getGround()));
            if (!years.contains(p.getInningDate().split("/")[2])) {
                years.add(p.getInningDate().split("/")[2]);
            }
            if (!inOpponents(p.getOpponent())) {
                opponentTeams.add(new Team(p.getOpponent(), ""));
            }
            if (!opponent.equals(p.getOpponent())) {
                continue;
            }
            if (!ground.equals(p.getGround())) {
                continue;
            }
            totalMatches++;
            totalRuns += p.getScore();
            economyRate += p.getEconomyRate();
            strikeRate += p.getStrikeRate();
        }
        totalMatchesTv.setText("" + totalMatches);
        strikeRateTv.setText("" + (totalMatches>0?String.format("%.02f", (strikeRate/totalMatches)):0));
        totalRunsTv.setText("" + totalRuns);
        economyRateTv.setText("" + economyRate);
        int sum=0;
        entries = new ArrayList<>();
        int i1;
        Random r = new Random();
//        for (int i = 1; i < years.size(); i++) {
//            i1= r.nextInt(8-0)-0;
//                entries.add(new BarEntry(6, i));
//        }
        entries.add(new BarEntry(totalRuns, 0));
        entries.add(new BarEntry(strikeRate, 1));
        BarDataSet bardataset = new BarDataSet(entries,"Runs");
        labels.add("2019");
        labels.add("");
//        for (int i = 1; i < years.size(); i++) {
//            labels.add(years.get(i));
//        }
        BarData data = new BarData(labels  , bardataset);
        barChart.setData(data);
        barChart.setDescription("Strike Rate");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(5000);
    }
    public void filterData(){
        Picasso.get().load(getOpponentFlag()).into(opponentFlagIv);
        opponentTv.setText(filterOpponentsSpinner.getSelectedItem().toString());
        Toast.makeText(this,"Test",Toast.LENGTH_LONG).show();
        float totalRuns = 0;
        float strikeRate = 0;
        float economyRate = 0;
        int totalMatches = 0;

        for (PlayerInnings p : playerInnings) {

            if (!inGrounds(p.getGround()))
                grounds.add(new Ground(p.getGround()));
            if (!years.contains(p.getInningDate().split("/")[2])) {
                years.add(p.getInningDate().split("/")[2]);
            }
            if (!inOpponents(p.getOpponent())) {
                opponentTeams.add(new Team(p.getOpponent(), ""));
            }

            if(!filterGroundsSpinner.getSelectedItem().toString().equals("Select a ground")&&!filterGroundsSpinner.getSelectedItem().toString().equals(p.getGround())){
                continue;
            }

            if(!filterOpponentsSpinner.getSelectedItem().toString().equals("Select a team")&&!filterOpponentsSpinner.getSelectedItem().toString().equals(p.getOpponent())){
                continue;
            }

            totalMatches++;
            totalRuns += p.getScore();
            economyRate += p.getEconomyRate();
            strikeRate += p.getStrikeRate();
        }
        strikeRate = strikeRate/totalMatches;
        totalMatchesTv.setText("" + totalMatches);
        strikeRateTv.setText("" + (totalMatches>0?String.format("%.02f", (strikeRate)):0));
        totalRunsTv.setText("" + totalRuns);
        economyRateTv.setText("" + economyRate);

        entries = new ArrayList<>();
//        int i1;
//        Random r = new Random();
//        for (int i = 1; i < years.size(); i++) {
//            i1= r.nextInt(8-0)-0;
//                entries.add(new BarEntry(6, i));
//        }
        entries.add(new BarEntry(totalRuns, 0));
        entries.add(new BarEntry(economyRate, 1));
        entries.add(new BarEntry(strikeRate, 2));
        BarDataSet bardataset = new BarDataSet(entries,"Runs");
        labels.add("");
        labels.add("");
//        for (int i = 1; i < years.size(); i++) {
//            labels.add(years.get(i));
//        }
        BarData data = new BarData(labels  , bardataset);
        barChart.setData(data);
        barChart.setDescription("Strike Rate");  // set the description
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
        filtersDialog = new Dialog(PlayerAnalysisResultActivity.this);
        filtersDialog.setContentView(R.layout.player_analysis_filter);
        Window window = filtersDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        filtersDialog.setTitle("Filters");
        filterOpponentsSpinner = filtersDialog.findViewById(R.id.playerAnalysisFilterOpponents);
        ArrayAdapter<Team> opponentsAdapter =
                new ArrayAdapter<Team>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, opponentTeams);
        opponentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterOpponentsSpinner.setAdapter(opponentsAdapter);
        filterOpponentsSpinner.setSelection(getOpponentTeamIndex(opponent));
        filterGroundsSpinner = filtersDialog.findViewById(R.id.playerAnalysisFilterGrounds);
        ArrayAdapter<Ground> groundsAdapter =
                new ArrayAdapter<Ground>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, grounds);
        groundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterGroundsSpinner.setAdapter(groundsAdapter);
        filterGroundsSpinner.setSelection(getGroundIndex(ground));
        filterYearsSpinner = filtersDialog.findViewById(R.id.playerAnalysisFilterYears);
        ArrayAdapter<String> yearsAdapter =
                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterYearsSpinner.setAdapter(yearsAdapter);
        filterLocationSpinner = filtersDialog.findViewById(R.id.playerAnalysisFilterLocation);
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

    private boolean inOpponents(String team) {
        for (Team opponent :
                opponentTeams) {
            if (opponent.getName().equals(team)) {
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
