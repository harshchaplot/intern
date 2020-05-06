package com.example.intern.auth.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.example.intern.R;
import com.example.intern.auth.ViewPagerAdapter_for_Login_Register;
import com.example.intern.auth.viewmodel.AuthViewModel;
import com.example.intern.databinding.FragmentRegistrationChoiceFRBinding;

import java.util.Timer;
import java.util.TimerTask;

public class RegistrationChoiceFR extends Fragment {
	private FragmentRegistrationChoiceFRBinding binding;
	private AuthViewModel viewModel;
	
	public RegistrationChoiceFR() {
		// Required empty public constructor
	}

	//ViewPager
	ViewPager viewPager;
	TextView chngText;
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        binding = FragmentRegistrationChoiceFRBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        //ViewPager
        viewPager = view.findViewById(R.id.viewpager_fr);
        chngText = view.findViewById(R.id.chngText);

        //Initialise ViewPager Adapter
        ViewPagerAdapter_for_Login_Register viewPagerAdapter = new ViewPagerAdapter_for_Login_Register(getContext());
        viewPager.setAdapter(viewPagerAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //Do stuff
                        chngText.setText("Samay");
                        break;
                    case 1:
                        //Do stuff
                        chngText.setText("Prakruti");
                        break;
                    //Add other cases for the pages

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(1);
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0);
                } /*else {
                    viewPager.setCurrentItem(0);
                }*/
            }
        };
/*        CountDownTimer countDownTimer=new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long l) {
                Log.e("c_time", l/1000 + "sec");
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(1);
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0);
                }
            }

            @Override
            public void onFinish() {
            }
        };
        countDownTimer.start();*/

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("inside", "Timmer2 ");
                handler.post(Update);
            }
        }, 1000,  2000);

        return view;
    }

	
	@Override
	public void onStart() {
		super.onStart();
		binding.registrationTvAsChild.setOnClickListener(v ->{
			viewModel.setRegChoiceisParent(false);
			Navigation.findNavController(v).navigate(R.id.action_registrationChoiceFR_to_registrationOptionsFR);
		});
		binding.registrationTvAsParent.setOnClickListener(v->{
			viewModel.setRegChoiceisParent(true);
			Navigation.findNavController(v).navigate(R.id.action_registrationChoiceFR_to_registrationOptionsFR);
		});
	}
}
