package com.example.soundonline.presentation.library;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundonline.Adapter.CategoryAdapter;
import com.example.soundonline.R;
import com.example.soundonline.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewCategories);
        Button btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(categoryAdapter);

        btnBack.setOnClickListener(v -> finish());

        client = new OkHttpClient();
        fetchCategories();
    }

    private void fetchCategories() {
        String url = "http://10.0.2.2:7146/api/categories";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CategoryActivity", "Lỗi kết nối API: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(CategoryActivity.this, "Lỗi khi tải danh sách category: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("CategoryActivity", "Dữ liệu API: " + responseData);
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        categoryList.clear();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Category category = new Category();
                            category.setCategoryId(jsonObject.getInt("categoryId"));
                            category.setCategoryName(jsonObject.getString("categoryName"));
                            category.setDescription(jsonObject.getString("description"));
                            category.setImageUrl(jsonObject.getString("imageUrl"));
                            category.setIsActive(jsonObject.isNull("isActive") ? true : jsonObject.getBoolean("isActive"));
                            try {
                                category.setCreatedAt(jsonObject.isNull("createdAt") ? null : dateFormat.parse(jsonObject.getString("createdAt")));
                            } catch (ParseException e) {
                                Log.e("CategoryActivity", "Lỗi parse createdAt: " + e.getMessage());
                                category.setCreatedAt(null);
                            }
                            categoryList.add(category);
                        }
                        runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        Log.e("CategoryActivity", "Lỗi phân tích JSON: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(CategoryActivity.this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                } else {
                    Log.e("CategoryActivity", "Phản hồi không thành công: " + response.code());
                    runOnUiThread(() -> Toast.makeText(CategoryActivity.this, "Lỗi khi tải danh sách category: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}