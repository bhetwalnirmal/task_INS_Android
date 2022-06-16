package com.nirmalbhetwal.task_ins_android;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.nirmalbhetwal.task_ins_android.databinding.ActivityMainBinding;
import com.nirmalbhetwal.task_ins_android.model.Task;
import com.nirmalbhetwal.task_ins_android.utils.RecyclerAdapter;
import com.nirmalbhetwal.task_ins_android.utils.RecyclerViewClickInterface;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public final static int REQUEST_CODE_ADD_TASK = 1;
    public final static int REQUEST_CODE_UPDATE_TASK = 2;
    public final static int REQUEST_CODE_SHOW_TASKS = 3;
    public final static int REQUEST_CODE_SELECT_IMAGE = 4;
    public final static int REQUEST_CODE_STORAGE_PERMISSION = 5;
    public final static int REQUEST_AUDIO_PERMISSION = 6;

    private int taskClickedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        

    }

    String deletedMovie = null;
    List<String> archivedMovies = new ArrayList<>();




    //    These are the interface Methods from our custom RecyclerViewClickInterface
    @Override
    public void onItemClick(int position) {
        // TODO: 15/06/2022  Handle when clicked 
//        Intent intent = new Intent(this, NewActivity.class);
//        intent.putExtra("MOVIE_NAME", moviesList.get(position));
//        startActivity(intent);
    }

    @Override
    public void onLongItemClick(final int position) {
        // TODO: 15/06/2022 Handle when long pressed
//        moviesList.remove(position);
//        recyclerAdapter.notifyItemRemoved(position);
    }
}