package radix.com.dotto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends AppCompatActivity {
  private static final String TAG = AuthActivity.class.toString();

  private static final int RC_SIGN_IN = 9001;
  private static final List<AuthUI.IdpConfig> AUTH_PROVIDERS = new ArrayList<>();
  static {
    AUTH_PROVIDERS.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
//        .setPermissions(Arrays.asList(Scopes.GAMES))
        .build());
    AUTH_PROVIDERS.add(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
//        .setPermissions(Collections.singletonList("public_profile"))
        .build());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // TODO: 5/13/2017 Can add a formal "press this to sign in" button later
    // Can also add an "Anon" sign-in as well
    FirebaseAuth auth = FirebaseAuth.getInstance();
    if (auth.getCurrentUser() != null && !auth.getCurrentUser().isAnonymous()) {
      // already signed in
      Log.d(TAG, "Already signed in");
      startGameActivity();
    } else {
      // not signed in
      startActivityForResult(
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setIsSmartLockEnabled(true)
              .setProviders(AUTH_PROVIDERS)
              .build(),
          RC_SIGN_IN);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "Got result from sign-in");

    // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      // Successfully signed in
      if (resultCode == ResultCodes.OK) {
        Log.d(TAG, "Sign in was OK");
        startGameActivity();
        finish();
        return;
      } else {
        // Sign in failed
        if (response == null) {
          // User pressed back button
          Log.d(TAG, "User cancelled back button");
        } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
          Log.d(TAG, "No network");
        } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
          Log.d(TAG, "Unknown error");
        }
        finish();
      }
      Log.d(TAG, "Who knows lol");
    }
  }

  private void startGameActivity() {
    startActivity(new Intent(this, DottoActivity.class));
    Log.d(TAG, "Going to game now");
    finish();
  }
}
