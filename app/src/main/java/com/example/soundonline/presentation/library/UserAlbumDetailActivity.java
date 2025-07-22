package com.example.soundonline.presentation.library;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.soundonline.R;
import com.example.soundonline.model.Album; // Make sure your Album model is Parcelable or Serializable

public class UserAlbumDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM = "extra_album"; // Key for passing Album object

    private ImageView imgDetailAlbumCover;
    private TextView tvDetailAlbumTitle;
    private TextView tvDetailArtistName;
    private TextView tvDetailGenre;
    private TextView tvHeaderTitle; // For the header title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_album_detail);

        // Initialize Views
        imgDetailAlbumCover = findViewById(R.id.imgDetailAlbumCover);
        tvDetailAlbumTitle = findViewById(R.id.tvDetailAlbumTitle);
        tvDetailArtistName = findViewById(R.id.tvDetailArtistName);
        tvDetailGenre = findViewById(R.id.tvDetailGenre);
        tvHeaderTitle = findViewById(R.id.tvAlbumDetailTitle); // Header title TextView
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Setup Back Button
        btnBack.setOnClickListener(v -> finish());

        // Get Album data from Intent
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM); // Use getParcelableExtra if Album is Parcelable
        // If Album is Serializable, use: Album album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);

        if (album != null) {
            // Set data to views
            Glide.with(this)
                    .load(album.getCoverImageUrl())
                    .error(R.drawable.img)
                    .into(imgDetailAlbumCover);

            tvDetailAlbumTitle.setText(album.getAlbumTitle());
            tvDetailArtistName.setText(album.getArtistName());
            tvDetailGenre.setText(album.getGenre() != null ? album.getGenre() : "Chưa rõ thể loại");
            tvHeaderTitle.setText(album.getAlbumTitle()); // Set header title to album title
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin album.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no album data
        }

        // If you add a RecyclerView for songs in the album:
        // RecyclerView recyclerAlbumSongs = findViewById(R.id.recyclerAlbumSongs);
        // recyclerAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        // SongAdapter songAdapter = new SongAdapter(this, album.getSongs()); // Assuming Album has a list of songs
        // recyclerAlbumSongs.setAdapter(songAdapter);
    }
}