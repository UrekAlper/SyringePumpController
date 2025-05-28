package com.example.syringepumpcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MeasurementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MeasurementAdapter adapter;
    private List<Measurement> measurementList;
    private DatabaseHelper dbHelper;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Measurement Records");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize components
        recyclerView = findViewById(R.id.recyclerView);
        textEmpty = findViewById(R.id.textEmpty);
        FloatingActionButton fabClear = findViewById(R.id.fabClear);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // RecyclerView settings
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Load measurements and setup adapter
        loadMeasurements();

        // Clear button click event
        fabClear.setOnClickListener(v -> {
            if (measurementList.isEmpty()) {
                Toast.makeText(MeasurementsActivity.this, "No records to delete!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(MeasurementsActivity.this)
                    .setTitle("Delete All Records")
                    .setMessage("Are you sure you want to delete all measurement records?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteAllMeasurements();
                        loadMeasurements();
                        Toast.makeText(MeasurementsActivity.this, "All records deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadMeasurements() {
        measurementList = dbHelper.getAllMeasurements();

        if (measurementList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);

            adapter = new MeasurementAdapter(this, measurementList);
            recyclerView.setAdapter(adapter);

            // Setup item click events
            adapter.setOnItemClickListener(new MeasurementAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Show item details (optional)
                    Measurement selectedMeasurement = adapter.getMeasurementAt(position);
                    Toast.makeText(MeasurementsActivity.this,
                            "Selected record: " + selectedMeasurement.getDateTime(),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeleteClick(int position) {
                    Measurement selectedMeasurement = adapter.getMeasurementAt(position);

                    new AlertDialog.Builder(MeasurementsActivity.this)
                            .setTitle("Delete Record")
                            .setMessage("Are you sure you want to delete this measurement record?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dbHelper.deleteMeasurement(selectedMeasurement.getId());
                                loadMeasurements();
                                Toast.makeText(MeasurementsActivity.this, "Record deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }

                @Override
                public void onViewGraphClick(int position) {
                    // Seçilen ölçümü grafikte göster
                    Measurement selectedMeasurement = adapter.getMeasurementAt(position);
                    showGraphForMeasurement(selectedMeasurement);
                }
            });
        }
    }

    // Seçilen ölçüm için grafik göster
    private void showGraphForMeasurement(Measurement measurement) {
        Intent intent = new Intent(MeasurementsActivity.this, GraphActivity.class);

        // Ölçüm verilerini intent'e ekle
        intent.putExtra("MEASUREMENT_ID", measurement.getId());
        intent.putExtra("MEASUREMENT_DATETIME", measurement.getDateTime());
        intent.putExtra("MEASUREMENT_FLOW_RATE", measurement.getFlowRate());
        intent.putExtra("MEASUREMENT_VOLUME", measurement.getVolume());
        intent.putExtra("MEASUREMENT_DURATION", measurement.getDuration());
        intent.putExtra("MEASUREMENT_NOTES", measurement.getNotes());

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Aktivite yeniden görünür olduğunda ölçümleri yenile
        loadMeasurements();
    }
}