package com.example.v2navigasifatisda;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HalamanKeduaActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_kedua);

        // Tombol kembali
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ArrayList<Langkah> Jalan = (ArrayList<Langkah>) getIntent().getSerializableExtra("Jalan");

        recyclerView = findViewById(R.id.recyclerViewLangkah);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new LangkahAdapter(this,Jalan));
    }
}
