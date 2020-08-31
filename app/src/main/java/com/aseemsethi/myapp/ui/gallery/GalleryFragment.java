package com.aseemsethi.myapp.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.aseemsethi.myapp.R;
import com.aseemsethi.myapp.ui.home.HomeViewModel;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private HomeViewModel homeViewModel;
    final String TAG = "MyApp: Gallery";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView");
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        homeViewModel.getLoggedin().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "onChanged Logged in: " + s);
                Toast.makeText(getActivity().getApplicationContext(),s,
                        Toast.LENGTH_LONG).show();
            }
        });
        final Button s = root.findViewById(R.id.a);
        s.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_nav_gallery_to_nav_home);
            }
        });
        return root;
    }
}