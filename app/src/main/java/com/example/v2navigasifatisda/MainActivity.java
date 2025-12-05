package com.example.v2navigasifatisda;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView inputAwal, inputAkhir;
    MapHelper.MapGraph mapGraph;
    MapHelper.Node selectedNode1, selectedNode2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputAwal = findViewById(R.id.inputawal);
        inputAkhir = findViewById(R.id.inputakhir);
        Button btnFindPath = findViewById(R.id.button2);
        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);

        // Buat map dan adapter
        mapGraph = MapHelper.FMIPAFATISDA();
        List<String> nodeNames = mapGraph.getAllNodeNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,nodeNames
        );

        inputAwal.setAdapter(adapter);
        inputAwal.setThreshold(1);

        inputAkhir.setAdapter(adapter);
        inputAkhir.setThreshold(1);

        inputAwal.setOnItemClickListener((parent, view, position, id) -> {
            String selectedNama = (String) parent.getItemAtPosition(position);
            MapHelper.Node selectedNode = mapGraph.getNodeByName(selectedNama);
            if (selectedNode != null) {
                selectedNode1 = selectedNode;
                imageView1.setImageResource(selectedNode.imageResId);
            }
        });

        inputAkhir.setOnItemClickListener((parent, view, position, id) -> {
            String selectedNama = (String) parent.getItemAtPosition(position);
            MapHelper.Node selectedNode = mapGraph.getNodeByName(selectedNama);
            if (selectedNode != null) {
                selectedNode2 = selectedNode;
                imageView2.setImageResource(selectedNode.imageResId);
            }
        });

        imageView1.setOnClickListener(v -> {
            if (selectedNode1 != null) {
                Intent intent = new Intent(MainActivity.this, Zoom_Image.class);
                intent.putExtra("image_res_id", selectedNode1.imageResId);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Pilih titik awal dulu", Toast.LENGTH_SHORT).show();
            }
        });

        imageView2.setOnClickListener(v -> {
            if (selectedNode2 != null) {
                Intent intent = new Intent(MainActivity.this, Zoom_Image.class);
                intent.putExtra("image_res_id", selectedNode2.imageResId);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Pilih titik akhir dulu", Toast.LENGTH_SHORT).show();
            }
        });

        btnFindPath.setOnClickListener(v -> {
            String awalNama = inputAwal.getText().toString().trim();
            String akhirNama = inputAkhir.getText().toString().trim();

            MapHelper.Node startNode = mapGraph.getNodeByName(awalNama);
            MapHelper.Node goalNode = mapGraph.getNodeByName(akhirNama);

            if (startNode == null || goalNode == null) {
                Toast.makeText(MainActivity.this, "Node tidak ditemukan!", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Langkah> Jalan = mapGraph.findShortestPath(startNode, goalNode);
            Toast.makeText(MainActivity.this, "Jalur diproses!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, HalamanKeduaActivity.class);
            intent.putExtra("Jalan", Jalan);
            startActivity(intent);
        });
    }
}
