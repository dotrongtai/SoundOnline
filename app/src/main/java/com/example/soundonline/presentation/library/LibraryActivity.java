package com.example.soundonline.presentation.library;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.LibraryAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.LibrarySection;
import com.example.soundonline.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LibraryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_library);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LibraryAdapter(getLibrarySections());

        adapter.setOnItemClickListener(section -> {
            String title = section.getTitle();
            if ("Liked tracks".equals(title)) {
                Intent intent = new Intent(this, LikedTrackActivity.class);
                startActivity(intent);
            }else
            if ("Albums".equals(title)) {
                Intent intent = new Intent(this, UserAlbumsActivity.class);
                startActivity(intent);
            }else
            if ("Following".equals(title)) {
                Intent intent = new Intent(this, FollowingActivity.class);
                startActivity(intent);
            }else  if ("Your uploads".equals(title)) {
                Intent intent = new Intent(this, YourUploadActivity.class);
                startActivity(intent);
            }else
            if ("Recently played".equals(title)) {
                Intent intent = new Intent(this, RecentlyPlayedActivity.class);
                startActivity(intent);
            }else

            if ("Listening history".equals(title)) {
                Intent intent = new Intent(this, ListeningHistoryActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Clicked: " + title, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);


        // Set up bottom navigation

        setupBottomNavigation();
    }

    private List<LibrarySection> getLibrarySections() {
        List<LibrarySection> sections = new ArrayList<>();
        sections.add(new LibrarySection("Liked tracks", false));
        sections.add(new LibrarySection("Albums", false));
        sections.add(new LibrarySection("Following", false));
        sections.add(new LibrarySection("Listening history", true));
        return sections;
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Uncomment if needed:
        // findViewById(R.id.btnSearch).setOnClickListener(v -> {
        //     startActivity(new Intent(this, SearchActivity.class));
        //     finish();
        // });

        // findViewById(R.id.btnLibrary).setOnClickListener(v -> {
        //     // Already on LibraryActivity
        // });

        // findViewById(R.id.btnCategory).setOnClickListener(v -> {
        //     startActivity(new Intent(this, CategoryActivity.class));
        //     finish();
        // });

        // findViewById(R.id.btnPlaylist).setOnClickListener(v -> {
        //     startActivity(new Intent(this, PlaylistActivity.class));
        //     finish();
        // });
    }
}
