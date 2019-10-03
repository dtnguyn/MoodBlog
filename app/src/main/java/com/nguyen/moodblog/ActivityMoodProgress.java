package com.nguyen.moodblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ActivityMoodProgress extends AppCompatActivity {

    //Static Constant variables
    public static final String MOOD_PROGRESS_UPDATED = "MOOD PROGRESS UPDATED";
    private final String DAILY_QUOTES_URL = "http://quotes.rest/qod.json";

    //Member variables
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ArrayList<BarEntry> mBarEntries;
    private List <Mood> mMoodProgress;
    private int mAverageMoodValue = -1;
    Calendar calendar = Calendar.getInstance();

    //Firebase Variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    //UI elements
    private BarChart mBarChart;
    private TextView mAveragePercent;
    private TextView mStatusLine;
    private ImageView mPieChart;
    private Button mBackButton;
    private TextView mQuoteContent;
    private TextView mQuoteAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        editor = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(ActivityMain.MY_PREFS_NAME, MODE_PRIVATE);
        if(prefs.getString(ActivityAppSettings.THEME_KEY, "light").equals("dark")){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_progress);


        //Set up database elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        //Initialize member variables
        mBarEntries = new ArrayList<>();
        mMoodProgress = new ArrayList<>();


        //Set up UI elements
        mBarChart = findViewById(R.id.mood_progress_bar_chart);
        mAveragePercent = findViewById(R.id.avg_percent);
        mStatusLine = findViewById(R.id.status_line);
        mPieChart = findViewById(R.id.avg_pie_chart);
        mBackButton = findViewById(R.id.id_back_button_mood_progress);
        mQuoteContent = findViewById(R.id.quote_content);
        mQuoteAuthor = findViewById(R.id.quote_author);

        //Update Figures
        updateBarGraph();

        //Update Quotes
        updateQuote(this);








        //Formatter for axis
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return mMoodProgress.get((int) value).getDate();
            }
        };

        ValueFormatter yAxisFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                String label;
                if(value == 0f){
                    label = "BAD";
                } else if (value == 5f){
                    label = "OK";
                } else if (value == 10f){
                    label = "GOOD";
                } else label = "";
                return label;
            }


        };



        //Create and customize bargraph
        Description description = mBarChart.getDescription();
        description.setEnabled(false);

        customizeXAxis(xAxisFormatter);
        customizeYAxis(yAxisFormatter);


        //Set up back Button
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //Customize x-axis
    private void customizeXAxis(ValueFormatter xAxisFormatter){
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(mAveragePercent.getCurrentTextColor());
        xAxis.setTypeface(mAveragePercent.getTypeface());
    }

    //Customize y-axis
    private void customizeYAxis(ValueFormatter yAxisFormatter){
        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setTypeface(mAveragePercent.getTypeface());
        yAxis.setTextColor(mAveragePercent.getCurrentTextColor());
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(1f);
        yAxis.setTextSize(14);
        yAxis.setAxisMaximum(10f);
        yAxis.setAxisMinimum(0f);
        yAxis.setValueFormatter(yAxisFormatter);
        yAxis.setLabelCount(1);

        YAxis yAxisR = mBarChart.getAxisRight();
        yAxisR.setEnabled(false);
    }

    public void updateBarGraph(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userID = mAuth.getCurrentUser().getUid();
                mBarEntries = new ArrayList<>();
                if(dataSnapshot.child("users").child(userID).child("moodProgress").getValue() != null){
                    for(int i = 0; i < dataSnapshot.child("users").child(userID).child("moodProgress").getChildrenCount(); i++){
                        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
                        Mood mood = new Mood();
                        mood.setMoodValue(dataSnapshot.child("users").child(userID).child("moodProgress").child("" + i).getValue(Mood.class).getMoodValue());
                        mood.setDate(dataSnapshot.child("users").child(userID).child("moodProgress").child("" + i).getValue(Mood.class).getDate());
                        mMoodProgress.add(mood);
                        mBarEntries.add(new BarEntry(i,mood.getMoodValue()));
                    }
                }
                MyBarDataSet barDataSet = new MyBarDataSet(mBarEntries, "Mood");
                barDataSet.setColors(new int[]{ContextCompat.getColor(getApplicationContext(), R.color.loveButton),
                        ContextCompat.getColor(getApplicationContext(), R.color.happyButton),
                        ContextCompat.getColor(getApplicationContext(), R.color.nervousButton),
                        ContextCompat.getColor(getApplicationContext(), R.color.sadButton)});
                BarData theData = new BarData(barDataSet);
                mBarChart.setData(theData);
                mBarChart.setMaxVisibleValueCount(0);
                mBarChart.fitScreen();
                mBarChart.setVisibleXRangeMaximum(4f);
                mBarChart.setPinchZoom(false);
                mBarChart.setDoubleTapToZoomEnabled(false);
                mBarChart.moveViewToX(2f);
                mBarChart.getLegend().setEnabled(false);
                mBarChart.setExtraTopOffset(5);
                updateMoodAverage();
                Log.d("MoodBlog", "mood size: " + mMoodProgress.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateMoodAverage(){
        int totalValue = 0;
        mAverageMoodValue += 1;
        for (int i = 0; i < mMoodProgress.size(); i++){
            totalValue += mMoodProgress.get(i).getMoodValue();
        }

        if(mMoodProgress.isEmpty()){
            mAveragePercent.setText("No data");
            mPieChart.setImageResource(R.drawable.default_percent);
            mStatusLine.setText("Enter data!");
        } else {
            mAverageMoodValue = (totalValue / mMoodProgress.size()) * 10;
            mAveragePercent.setText("" + mAverageMoodValue + "%");
            setUpCircle(mAverageMoodValue);
        }


    }

    private void setUpCircle(int averageValue){
        if(averageValue == 100){
            mPieChart.setImageResource(R.drawable.hundred_percent);
            mStatusLine.setText("You are doing excellent!");
        } else if(averageValue >= 75){
            mPieChart.setImageResource(R.drawable.above_seventyfive_percent);
            mStatusLine.setText("You are doing Great!");
        } else if(averageValue > 50){
            mPieChart.setImageResource(R.drawable.above_fifty_percent);
            mStatusLine.setText("You are doing Great!");
        } else if(averageValue == 50){
            mPieChart.setImageResource(R.drawable.fifty_percent);
            mStatusLine.setText("You are doing Okay!");
        } else if(averageValue > 30){
            mPieChart.setImageResource(R.drawable.below_fifty_percent);
            mStatusLine.setText("Try to be more positive!");
        } else if(averageValue > 10){
            mPieChart.setImageResource(R.drawable.below_thirty_percent);
            mStatusLine.setText("Everything will be okay!");
        } else if(averageValue > 0){
            mPieChart.setImageResource(R.drawable.below_ten_percent);
            mStatusLine.setText("You need help!");
        } else if(averageValue == 0){
            mPieChart.setImageResource(R.drawable.zero_percent);
            mStatusLine.setText("You need help!");
        } else {
            mPieChart.setImageResource(R.drawable.default_percent);
            mStatusLine.setText("Update your Mood Progress!");
        }
    }


    private static class MyBarDataSet extends BarDataSet{

        public MyBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
        }

        @Override
        public int getColor(int index) {
            float number = getEntryForIndex(index).getY();
            if(number > 8.0){
                return mColors.get(0);
            } else if(number >= 5.0){
                return mColors.get(1);
            } else if(number > 2.5){
                return mColors.get(2);
            } else return mColors.get(3);
        }
    }

    //Get information from Brainy Quotes
    private void updateQuoteFromAPIRequest(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d("MoodBlog", "Here??");

        client.get(DAILY_QUOTES_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("MoodBlog", "Success: " + response.toString());
                try {
                    String content = response.getJSONObject("contents").getJSONArray("quotes").getJSONObject(0).getString("quote");
                    String author = response.getJSONObject("contents").getJSONArray("quotes").getJSONObject(0).getString("author");
                    String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());

                    QuoteData quote = new QuoteData(currentDate, content, author);
                    myRef.child("dailyQuote").setValue(quote);
                    Log.d("MoodBlog", "Success: " + content);
                    mQuoteContent.setText("'" + content + "'");
                    mQuoteAuthor.setText(author);
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("MoodBlog", "Failed");
            }
        });
    }

    //Update quote
    private void updateQuote(final ActivityMoodProgress activity){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("dailyQuote").getValue(QuoteData.class) != null){
                    String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
                    Log.d("MoodBlog", "Current date: " + currentDate);
                    if(dataSnapshot.child("dailyQuote").getValue(QuoteData.class).getDate().equals(currentDate)){
                        String content = dataSnapshot.child("dailyQuote").getValue(QuoteData.class).getContent();
                        String author = dataSnapshot.child("dailyQuote").getValue(QuoteData.class).getAuthor();
                        mQuoteAuthor.setText(author);
                        mQuoteContent.setText(content);
                    } else updateQuoteFromAPIRequest(new RequestParams());
                } else updateQuoteFromAPIRequest(new RequestParams());

                String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
                if(!mMoodProgress.get(mMoodProgress.size() - 1).getDate().equals(currentDate)){
                    DialogMoodProgress dialogMoodProgress = new DialogMoodProgress(mMoodProgress, activity);
                    dialogMoodProgress.show(getSupportFragmentManager(), "MoodBlog");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}


