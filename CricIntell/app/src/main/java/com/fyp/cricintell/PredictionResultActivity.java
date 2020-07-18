package com.fyp.cricintell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.cricintell.models.MatchPrediction;
import com.fyp.cricintell.models.OdiTeamMatch;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionResultActivity extends AppCompatActivity {
    String team, opponent, ground, location, teamFlag, opponentFlag;
    List<MatchPrediction> matchPredictions, matchPredictionsOpponent;
    TextView predictionTv, teamTv, opponentTv;
    FirebaseFirestore mFirestore;
    ImageView teamFlagIv, opponentFlagIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);
        mFirestore = FirebaseFirestore.getInstance();
        matchPredictions = new ArrayList<>();
        matchPredictionsOpponent = new ArrayList<>();
        predictionTv = findViewById(R.id.predictionResultTv);
        teamTv = findViewById(R.id.predicationResultTeamTv);
        opponentTv = findViewById(R.id.predicationResultOpponentTv);
        team = getIntent().getExtras().getString("team");
        teamFlag = getIntent().getExtras().getString("teamFlag");
        ground = getIntent().getExtras().getString("ground");
        location = getIntent().getExtras().getString("location");
        opponent = getIntent().getExtras().getString("opponent");
        opponentFlag = getIntent().getExtras().getString("opponentFlag");
        teamFlagIv = findViewById(R.id.predictionResultTeamFlag);
        opponentFlagIv = findViewById(R.id.predictionResultOpponentFlag);
        Picasso.get().load(teamFlag).into(teamFlagIv);
        Picasso.get().load(opponentFlag).into(opponentFlagIv);
        teamTv.setText(team.toString());
        opponentTv.setText(opponent.toString());
        mFirestore.collection("ODIMatches").whereEqualTo("Country", team).whereEqualTo("Ground", ground).whereIn("Match", Arrays.asList(team + " v " + opponent, opponent + " v " + team)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : documents) {

                        MatchPrediction matchPrediction = new MatchPrediction();
                        String result = doc.getString("Result");
                        String margin = doc.getString("Margin");
                        String matchDate = doc.getString("MatchDate");
                        // Point for Result
                        if (location.equals("Home") && result.equals("Won")) {
                            matchPrediction.setGround(0);
                        } else if (location.equals("Home") && result.equals("Lost")) {
                            matchPrediction.setGround(0);
                        } else {
                            matchPrediction.setGround(0);
                        }
                        if (location.equals("Away") && result.equals("Won")) {
                            matchPrediction.setGround(0.2f);
                        } else if (location.equals("Away") && result.equals("Lost")) {
                            matchPrediction.setGround(0.2f);
                        } else {
                            matchPrediction.setGround(0);
                        }
                        if (result.equals("Won")) {
                            matchPrediction.setResult(1);
                        } else if (result.equals("Lost")) {
                            matchPrediction.setResult(0);
                        } else {
                            matchPrediction.setResult(1);
                        }

                        // Point wicket margin

                        if (margin.toLowerCase().contains("wicket")) {
                            int wickets = Integer.parseInt(doc.getString("Margin").split(" ")[0]);
                            if (result.equals("Won")) {
                                // Margin <=2
                                if (wickets <= 1) {
                                    matchPrediction.setWicketsMargin(0.8f);
                                } else {
                                    matchPrediction.setWicketsMargin(1);
                                }
                                matchPrediction.setRunsMargin(0.5f);
                            } else if (result.equals("Lost")) {
                                if (wickets <= 1) {
                                    matchPrediction.setWicketsMargin(0.2f);
                                } else {
                                    matchPrediction.setWicketsMargin(0);
                                }
                                matchPrediction.setRunsMargin(0.5f);

                            } else {
                                matchPrediction.setWicketsMargin(1);
                                matchPrediction.setRunsMargin(1);
                            }

                        } else if (margin.toLowerCase().contains("runs")) {
                            int runs = Integer.parseInt(doc.getString("Margin").split(" ")[0]);
                            if (result.equals("Won")) {
                                // Runs <= 20
                                if (runs <= 10) {
                                    matchPrediction.setRunsMargin(0.8f);
                                } else {
                                    matchPrediction.setRunsMargin(1);
                                }
                                matchPrediction.setWicketsMargin(0.5f);
                            } else if (result.equals("Lost")) {
                                if (runs <= 10) {
                                    matchPrediction.setRunsMargin(0.2f);
                                } else {
                                    matchPrediction.setRunsMargin(0);
                                }
                                matchPrediction.setWicketsMargin(0.5f);

                            } else {
                                matchPrediction.setWicketsMargin(1);
                                matchPrediction.setRunsMargin(1);
                            }


                        }

                        int year = Integer.parseInt(matchDate.split("/")[2]);
                        if (year >= 2016) {
                            matchPrediction.setRecentYearsFactor(1);
                        } else {
                            matchPrediction.setRecentYearsFactor(0);

                        }
                        matchPredictions.add(matchPrediction);

                    }
                    predict();

                } else {
                    Log.d("FirestoreData", "Error getting documents: ", task.getException());
                }
            }
        });
        mFirestore.collection("ODIMatches").whereEqualTo("Country", opponent).whereEqualTo("Ground", ground).whereIn("Match", Arrays.asList(team + " v " + opponent, opponent + " v " + team)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot doc : documents) {

                        MatchPrediction matchPrediction = new MatchPrediction();
                        String result = doc.getString("Result");
                        String margin = doc.getString("Margin");
                        String matchDate = doc.getString("MatchDate");
                        // Point for Result
                        if (location.equals("Home") && result.equals("Won")) {
                            matchPrediction.setGround(0);
                        } else if (location.equals("Home") && result.equals("Lost")) {
                            matchPrediction.setGround(0);
                        } else {
                            matchPrediction.setGround(0);
                        }
                        if (location.equals("Away") && result.equals("Won")) {
                            matchPrediction.setGround(0.2f);
                        } else if (location.equals("Away") && result.equals("Lost")) {
                            matchPrediction.setGround(0.2f);
                        } else {
                            matchPrediction.setGround(0);
                        }
                        if (result.equals("Won")) {
                            matchPrediction.setResult(1);
                        } else if (result.equals("Lost")) {
                            matchPrediction.setResult(0);
                        } else {
                            matchPrediction.setResult(1);
                        }

                        // Point wicket margin

                        if (margin.toLowerCase().contains("wicket")) {
                            int wickets = Integer.parseInt(doc.getString("Margin").split(" ")[0]);
                            if (result.equals("Won")) {
                                // Margin <=2
                                if (wickets <= 1) {
                                    matchPrediction.setWicketsMargin(0.8f);
                                } else {
                                    matchPrediction.setWicketsMargin(1);
                                }
                                matchPrediction.setRunsMargin(0.5f);
                            } else if (result.equals("Lost")) {
                                if (wickets <= 1) {
                                    matchPrediction.setWicketsMargin(0.2f);
                                } else {
                                    matchPrediction.setWicketsMargin(0);
                                }
                                matchPrediction.setRunsMargin(0.5f);

                            } else {
                                matchPrediction.setWicketsMargin(1);
                                matchPrediction.setRunsMargin(1);
                            }

                        } else if (margin.toLowerCase().contains("runs")) {
                            int runs = Integer.parseInt(doc.getString("Margin").split(" ")[0]);
                            if (result.equals("Won")) {
                                // Runs <= 20
                                if (runs <= 10) {
                                    matchPrediction.setRunsMargin(0.8f);
                                } else {
                                    matchPrediction.setRunsMargin(1);
                                }
                                matchPrediction.setWicketsMargin(0.5f);
                            } else if (result.equals("Lost")) {
                                if (runs <= 10) {
                                    matchPrediction.setRunsMargin(0.2f);
                                } else {
                                    matchPrediction.setRunsMargin(0);
                                }
                                matchPrediction.setWicketsMargin(0.5f);

                            } else {
                                matchPrediction.setWicketsMargin(1);
                                matchPrediction.setRunsMargin(1);
                            }


                        }

                        int year = Integer.parseInt(matchDate.split("/")[2]);
                        if (year >= 2016) {
                            matchPrediction.setRecentYearsFactor(1);
                        } else {
                            matchPrediction.setRecentYearsFactor(0);

                        }
                        matchPredictionsOpponent.add(matchPrediction);

                    }
                    predict();

                } else {
                    Log.d("FirestoreData", "Error getting documents: ", task.getException());
                }
            }
        });

        Log.i("Prediction", team + "," + ground + "," + location + "," + opponent);
    }

    private void predict() {

        float sum = 0, sumOpponent = 0;

        for (MatchPrediction matchPrediction :
                matchPredictions) {
            sum += getSinglePredictionFactor(matchPrediction);
        }

        for (MatchPrediction matchPrediction :
                matchPredictionsOpponent) {
            sumOpponent += getSinglePredictionFactor(matchPrediction);
        }
        float chanceTeam = sum / matchPredictions.size();
        float chanceOpponet = sumOpponent / matchPredictionsOpponent.size();
//        Toast.makeText(this, "Prediction in Favour of " + team + ": " + (chanceTeam) + " and of " + opponent + " : " + (chanceOpponet), Toast.LENGTH_SHORT).show();
//        Log.d("Prediction", "Begins");
//        Log.d("Prediction", "" + team + ":" + chanceTeam);
//        Log.d("Prediction", "" + opponent + ":" + chanceOpponet);
//        Log.d("Prediction", "DRAW : " + (100 - chanceTeam - chanceOpponet));
        predictionTv.setText("" + team + " has a wining chance of " + chanceTeam + " % ");
        PieChart pieChart = findViewById(R.id.predicationResultPieChart);
        ArrayList percentages = new ArrayList();
        percentages.add(new Entry(chanceTeam, 0));
        percentages.add(new Entry(chanceOpponet, 1));
        percentages.add(new Entry((100 - chanceTeam - chanceOpponet), 2));
        PieDataSet dataSet = new PieDataSet(percentages, "(Prediction)");
        int[] colors = new int[3];
        colors[0] = Color.rgb(0, 255, 0);
        colors[1] = Color.rgb(255, 0, 0);
        colors[2] = Color.rgb(200, 200, 200);

        dataSet.setColors(colors);
        ArrayList predictionType = new ArrayList();
        predictionType.add(team);
        predictionType.add(opponent);
        predictionType.add("Draw");

        PieData data = new PieData(predictionType, dataSet);
        pieChart.setData(data);

        pieChart.animateXY(5000, 5000);
    }

    private float getSinglePredictionFactor(MatchPrediction matchPrediction) {
//        return ((matchPrediction.getResult() + matchPrediction.getGround() + matchPrediction.getRunsMargin() + matchPrediction.getWicketsMargin() ) / 4) * 100;
        return ((matchPrediction.getResult() + matchPrediction.getRunsMargin() + matchPrediction.getWicketsMargin()) / 3) * 100;
    }
}
