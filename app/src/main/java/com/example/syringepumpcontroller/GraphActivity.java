package com.example.syringepumpcontroller;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GraphActivity extends AppCompatActivity {

    private LineChart chart;
    private TextView tvMeasurementInfo;
    private int measurementId = -1;
    private String dateTime = "";
    private double flowRate = 0;
    private double volume = 0;
    private String duration = "";
    private String notes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_graph);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Measurement Graph");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        chart = findViewById(R.id.lineChart);
        tvMeasurementInfo = findViewById(R.id.tvMeasurementInfo);

        // Get data from Intent
        if (getIntent().hasExtra("MEASUREMENT_ID")) {
            measurementId = getIntent().getIntExtra("MEASUREMENT_ID", -1);
            dateTime = getIntent().getStringExtra("MEASUREMENT_DATETIME");
            flowRate = getIntent().getDoubleExtra("MEASUREMENT_FLOW_RATE", 0);
            volume = getIntent().getDoubleExtra("MEASUREMENT_VOLUME", 0);
            duration = getIntent().getStringExtra("MEASUREMENT_DURATION");
            notes = getIntent().getStringExtra("MEASUREMENT_NOTES");

            // Show measurement information
            showMeasurementInfo();

            // Setup chart
            setupChart();
        }
    }

    private void showMeasurementInfo() {
        String infoText = String.format(Locale.getDefault(),
                "Date: %s | Flow Rate: %.2f mL/h | Volume: %.2f mL | Duration: %s",
                dateTime, flowRate, volume, duration);

        tvMeasurementInfo.setText(infoText);
    }

    private void setupChart() {
        // Configure chart
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawBorders(true);

        // Add description
        Description description = new Description();
        description.setText("Time (seconds) - Volume (mL)");
        description.setTextSize(12f);
        chart.setDescription(description);

        // X axis (time) settings
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(15f); // 15 second intervals
        xAxis.setLabelRotationAngle(0);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.0fs", value);
            }
        });

        // Y axis (volume) settings
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(0.1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1f mL", value);
            }
        });

        chart.getAxisRight().setEnabled(false); // Disable right axis

        // Create and set chart data (simulated data)
        createAndSetChartData();

        // Add animation
        chart.animateX(1000);
    }

    private void createAndSetChartData() {
        List<Entry> entries = new ArrayList<>();
        
        // Convert duration to seconds
        int totalSeconds = durationToSeconds(duration);
        
        // Create realistic data points using flow rate and volume
        // Formula: Volume = Flow Rate * (Time / 3600) [converting mL/hour to mL/second]
        double flowRatePerSecond = flowRate / 3600.0;
        double maxVolume = volume;
        
        // Create data points at 15 second intervals
        for (int i = 0; i <= totalSeconds; i += 15) {
            // Calculate actual volume
            double currentVolume = flowRatePerSecond * i;
            if (currentVolume > maxVolume) {
                currentVolume = maxVolume;
            }
            entries.add(new Entry(i, (float) currentVolume));
        }

        // Create and customize dataset
        LineDataSet dataSet = new LineDataSet(entries, "Volume (mL)");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Set data to chart
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private int durationToSeconds(String duration) {
        // Convert duration format (HH:mm:ss) to seconds
        String[] parts = duration.split(":");
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            return hours * 3600 + minutes * 60 + seconds;
        } catch (Exception e) {
            return 300; // Default to 5 minutes in case of error
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}