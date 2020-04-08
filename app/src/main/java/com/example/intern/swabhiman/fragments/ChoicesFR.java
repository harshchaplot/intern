package com.example.intern.swabhiman.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.intern.R;
import com.example.intern.databinding.ActivitySwabhimanDpBinding;
import com.example.intern.swabhiman.SwabhimanVM;

public class ChoicesFR extends Fragment {
	private ActivitySwabhimanDpBinding binding;
	private SwabhimanVM viewModel;
	public ChoicesFR() {
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		viewModel = new ViewModelProvider(requireActivity()).get(SwabhimanVM.class);
		binding = ActivitySwabhimanDpBinding.inflate(inflater, container,false);
		View view = binding.getRoot();
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(viewModel.isHasAcceptedTnC()) {
			//TODO: Set on click listeners for choices
			binding.donar.setOnClickListener(v -> {
				viewModel.getNavController().navigate(R.id.action_choicesFR_to_donorFR);
			});
			binding.employment.setOnClickListener(v -> {
				viewModel.getNavController().navigate(R.id.action_choicesFR_to_employement);
			});
			binding.investor.setOnClickListener(v -> {
				viewModel.getNavController().navigate(R.id.action_choicesFR_to_investorFR);
			});
			binding.businessAssociate.setOnClickListener(v -> {
				viewModel.getNavController().navigate(R.id.action_choicesFR_to_businessAssociateFR);
			});
		}else{
			viewModel.getNavController().navigate(R.id.action_choicesFR_to_tnCFR);
		}
	}
}
