package radix.com.dotto.models;

import android.graphics.Point;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import radix.com.dotto.controllers.DotInfo;

public class WorldModel implements IModelInterface {
  private final String TAG = WorldModel.class.toString();
  private final List<DotInfo> mPixelData;
  private final List<DotInfo> mReturnedPixelData;
  private DatabaseReference mPixelPathReference;
  private PixelChildChangeListener mPixelChildChangeListener;
  private FirebaseDatabase mFirebaseDatabase;
  private boolean mIsOnline;

  private int mConfigTimeoutSeconds = -1;
  private long mLastServerWriteTime = -1L;

  public WorldModel() {
    mPixelData = new CopyOnWriteArrayList<>();
    mReturnedPixelData = new ArrayList<>();

    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPixelPathReference = mFirebaseDatabase.getReference(ProtocolConstants.PIXEL_PATH);
    mPixelPathReference.keepSynced(true);

    mPixelChildChangeListener = new PixelChildChangeListener();
    setupConnectivityUpdateListener();
    setupTimeoutUpdateListener();
  }

  @Override
  public void setIsPlaying(boolean isPlaying) {
    Log.d(TAG, "Set playing to : " + isPlaying);
    if (isPlaying) {
      mPixelPathReference.addChildEventListener(mPixelChildChangeListener);
    } else {
      mPixelPathReference.removeEventListener(mPixelChildChangeListener);
    }
  }

  @Override
  public void onWriteDotInfo(DotInfo info) {
    if (!mIsOnline) {
      Log.w(TAG, "User is offline. Nooping the write");
      return;
    }

    // verify the info is correct
    if (info.getPointX() < 0 || info.getPointX() > getWorldWidth() || info.getPointY() < 0 || info.getPointY() > getWorldHeight()) {
      Log.d(TAG, "Tried to draw out of bounds: " + info);
      return;
    }

    Log.i(TAG, "Adding info at: " + info);
    // turn the x,y into a key
    final String key = ProtocolHandler.getKeyFromCoordinates(info.getPointX(), info.getPointY());
    final int colorValue = info.getColor().getCode();
    // Try to get authorized
    DatabaseReference turnstileRef = mFirebaseDatabase.getReference(ProtocolConstants.TURNSTILE_PATH);

    // Get a map of the values we want
    Map<String, Object> values = new HashMap<>();
    values.put(ProtocolConstants.TURNSTILE_COLOR, colorValue);
    values.put(ProtocolConstants.TURNSTILE_LOCATION, key);
    values.put(ProtocolConstants.TURNSTILE_TIME, ServerValue.TIMESTAMP);

    // Write to our auth.uid
    FirebaseAuth auth = FirebaseAuth.getInstance();

    if (auth.getCurrentUser() != null) {
      final String uid = auth.getCurrentUser().getUid();
      Log.i(TAG, "Uid: " + uid);
      turnstileRef.child(uid).setValue(values, new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
          Log.d(TAG, "Turnstile write finished: " + databaseReference);
          if (databaseError == null) {
            // Success! Now try to write to the actual data list!
            writeToDotPath(key, colorValue);
          } else {
            Log.d(TAG, "Write failed!!! : " + databaseError);
          }
        }
      });
    } else {
      Log.w(TAG, "Couldn't write due to current user being null");
    }
  }

  /**
   * Writes to the dot ref. Will fail without the turnstile token!
   *
   * @param key
   * @param colorValue
   */
  private void writeToDotPath(String key, int colorValue) {
    Log.d(TAG, "Writing dot to path");
    mPixelPathReference.child(key).setValue(colorValue, new DatabaseReference.CompletionListener() {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        Log.d(TAG, "Write finished: " + databaseReference);
        if (databaseError == null) {
          Log.i(TAG, "Write succeeded");
        } else {
          Log.d(TAG, "Write failed!!! : " + databaseError);
        }
      }
    });
  }

  /**
   * @param maxElements max elements to return
   * @return a list of tap infos. These objects are not guaranteed to exist beyond the current frame!
   */
  @Override
  public List<DotInfo> getGridInfo(int maxElements) {
    Log.d(TAG, "Got pixels: " + mPixelData.size());
    mReturnedPixelData.clear();

    List<DotInfo> pixelList = Collections.synchronizedList(mPixelData);
    synchronized (pixelList) {
      // "It's time to pop off" - Obama
      while (!pixelList.isEmpty() && mReturnedPixelData.size() < maxElements) {
        mReturnedPixelData.add(pixelList.remove(0));
      }
    }
    return mReturnedPixelData;
  }

  private void addPixelInfo(DataSnapshot dataSnapshot) {
    String key = dataSnapshot.getKey();
    int colorCode = (int) (long) dataSnapshot.getValue();

    DotInfo info = ProtocolHandler.getPixelInfoFromData(key, colorCode);

    List<DotInfo> infoList = Collections.synchronizedList(mPixelData);
    synchronized (infoList) {
      infoList.add(info);
    }
  }

  /**
   * Listens for changes in pixels from firebase
   */
  private class PixelChildChangeListener implements ChildEventListener {
    private final String TAG = PixelChildChangeListener.class.toString();

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
      addPixelInfo(dataSnapshot);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
      addPixelInfo(dataSnapshot);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
      Log.v(TAG, "onChildRemoved called " + dataSnapshot);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
      Log.v(TAG, "onChildMoved called " + dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      Log.v(TAG, "onCancelled called " + databaseError);
    }
  }

  @Override
  public int getWorldWidth() {
    return 1000;
  }

  @Override
  public int getWorldHeight() {
    return 1000;
  }

  @Override
  public boolean hasGridInfo() {
    return !mPixelData.isEmpty();
  }

  @Override
  public boolean isLocalPointOutsideWorldBounds(Point localPoint) {
    return (localPoint.x < 0 || localPoint.x > getWorldWidth() ||
        localPoint.y < 0 || localPoint.y > getWorldHeight());
  }

  @Override
  public long getTimeUntilNextWrite() {
    // Note, this isn't the server time, so the value might look iffy
    long currentTimeNowMillis = System.currentTimeMillis();
    long diff = (TimeUnit.MILLISECONDS.convert(mConfigTimeoutSeconds, TimeUnit.SECONDS) + mLastServerWriteTime) - currentTimeNowMillis;
    return diff;
  }

  private void setupConnectivityUpdateListener() {
    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    connectedRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        mIsOnline = snapshot.getValue(Boolean.class);
        Log.d(TAG, "Online status: " + mIsOnline);
      }

      @Override
      public void onCancelled(DatabaseError error) {
        System.err.println("Listener was cancelled");
      }
    });
  }

  private void setupTimeoutUpdateListener() {
    // The config value
    DatabaseReference configTimeoutRef = FirebaseDatabase.getInstance().getReference(ProtocolConstants.CONFIG_PATH).child(ProtocolConstants.CONFIG_TIMEOUT_SECONDS);
    configTimeoutRef.keepSynced(true);
    configTimeoutRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        mConfigTimeoutSeconds = snapshot.getValue(Integer.class);
        Log.d(TAG, "Config timeout: " + mConfigTimeoutSeconds);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {}
    });

    // The timeout value in the turnstile
    FirebaseAuth auth = FirebaseAuth.getInstance();
    // Users should be auth'd by the time they get here
    if (auth.getCurrentUser() != null) {
      DatabaseReference userTurnstileRef = FirebaseDatabase.getInstance().getReference(ProtocolConstants.TURNSTILE_PATH)
          .child(auth.getCurrentUser().getUid())
          .child(ProtocolConstants.TURNSTILE_TIME);
      userTurnstileRef.keepSynced(true);
      userTurnstileRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
          mLastServerWriteTime = snapshot.getValue(Long.class);
          Log.d(TAG, "Last server write time: " + mLastServerWriteTime);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}
      });
    } else {
      throw new RuntimeException("User should be authorized by the time WorldModel is ran!");
    }
  }
}
