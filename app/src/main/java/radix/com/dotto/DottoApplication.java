package radix.com.dotto;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 *
 */
public class DottoApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  }
}
