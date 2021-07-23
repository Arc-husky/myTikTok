package com.example.mainacticity.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mainacticity.Constants;
import com.example.mainacticity.MainActivity;
import com.example.mainacticity.My_ImageViewPlus;
import com.example.mainacticity.PersonalPage;
import com.example.mainacticity.R;
import com.example.mainacticity.databinding.FragmentMineBinding;
import com.example.mainacticity.loginActivity;

public class MineFragment extends Fragment {

    private FragmentMineBinding binding;
    private String MY_ID = "";
    private final String TEXT_BEGIN = "ID:";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton personalbtn = root.findViewById(R.id.personalbtn);
        TextView idView = root.findViewById(R.id.userId);
        TextView usernameView = root.findViewById(R.id.username);
        idView.setText(TEXT_BEGIN+MY_ID);
        usernameView.setText(Constants.USER_NAME);
        My_ImageViewPlus head = root.findViewById(R.id.head2);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), loginActivity.class);
                startActivityForResult(intent,1);
            }
        });
        personalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalPage.class);
                intent.putExtra(PersonalPage.PERSON_ID,MY_ID);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MY_ID = ((MainActivity)context).getMY_ID();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        MY_ID=null;
    }
}