package com.example.intern.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.intern.R;
import com.example.intern.auth.viewmodel.AuthViewModel;
import com.example.intern.database.SharedPrefUtil;
import com.example.intern.databinding.ActivityAuthBinding;
import com.example.intern.mainapp.MainApp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class AuthActivity extends AppCompatActivity {
	private static String TAG = AuthActivity.class.getSimpleName();
	private ActivityAuthBinding binding;
	private AuthViewModel viewModel;
	private boolean hasRecreated;
	private SharedPreferences langPrefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityAuthBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
		viewModel.setPrefUtil(new SharedPrefUtil(this));
		FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
		NavController navController = Navigation.findNavController(this, R.id.auth_nav_host_fr);
		viewModel.setNavController(navController);
		//TODO:Create splash screen
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		viewModel.setFirebaseApp(firebaseApp);
		viewModel.setGoogleSignInClient(GoogleSignIn.getClient(this, gso));
		viewModel.setFirebaseAuth(FirebaseAuth.getInstance(viewModel.getFirebaseApp()));
		viewModel.setFirebaseUser(viewModel.getFirebaseAuth().getCurrentUser());
		viewModel.setLoggedInListener(isLoggedIn -> {
			if(isLoggedIn){
				Intent intent = new Intent(AuthActivity.this, MainApp.class);
				intent.putExtra(MainApp.IS_NEW_USER, true);
				startActivity(intent);
				finish();
			}
		});
		viewModel.setLanguageListener(locale -> {
			Resources resources = getResources();
			DisplayMetrics dm = resources.getDisplayMetrics();
			Configuration configuration = resources.getConfiguration();
			configuration.setLocale(new Locale(locale.toLowerCase()));
			resources.updateConfiguration(configuration, dm);
			langPrefs = getSharedPreferences("lang", MODE_PRIVATE);
			langPrefs.edit().putString("lang", locale).apply();
		});
	}
}
