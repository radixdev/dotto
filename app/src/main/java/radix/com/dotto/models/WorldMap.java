package radix.com.dotto.models;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import radix.com.dotto.controllers.PixelInfo;

public class WorldMap implements IModelInterface {
  private final String TAG = WorldMap.class.toString();
  private final List<PixelInfo> mPixelData;
  private List<PixelInfo> result = new ArrayList<>();
  private DatabaseReference mPixelPathReference;

  public WorldMap() {
    mPixelData = new ArrayList<>();
    result = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    mPixelPathReference = database.getReference(ProtocolConstants.PIXEL_PATH);
    mPixelPathReference.keepSynced(true);
    mPixelPathReference.addChildEventListener(new PixelChildChangeListener());
  }

  @Override
  public void onPixelInfoChange(PixelInfo info) {
    // turn the x,y into a key
    Log.d(TAG, "Passing pixel info to firebase: " + info);
    String key = ProtocolHandler.getKeyFromCoordinates(info.getPointX(), info.getPointY());
    Log.d(TAG, "key: " + key);

    mPixelPathReference.child(key).setValue(info.getColor().getCode());
  }

  /**
   * @param maxElements max elements to return
   * @return a list of tap infos. These objects are not guaranteed to exist beyond the current frame!
   */
  @Override
  public List<PixelInfo> getGridInfo(int maxElements) {
    result.clear();

    // "It's time to pop off" - Obama
    while (!mPixelData.isEmpty() && result.size() < maxElements) {
      result.add(mPixelData.remove(0));
    }
    return result;
  }

  private void addPixelInfo(DataSnapshot dataSnapshot) {
    String key = dataSnapshot.getKey();
    int colorCode = (int) (long) dataSnapshot.getValue();

    PixelInfo info = ProtocolHandler.getPixelInfoFromData(key, colorCode);
    Log.d(TAG, "Got pixel info from firebase: " + info);
    mPixelData.add(info);
  }

  /**
   * Listens for changes in pixels from firebase
   */
  private class PixelChildChangeListener implements ChildEventListener {
    private final String TAG = PixelChildChangeListener.class.toString();

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
      Log.d(TAG, "onChildChanged called " + dataSnapshot);
      addPixelInfo(dataSnapshot);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
      Log.d(TAG, "onChildAdded called " + dataSnapshot);
      addPixelInfo(dataSnapshot);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
      Log.d(TAG, "onChildRemoved called " + dataSnapshot);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
      Log.d(TAG, "onChildMoved called " + dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      Log.d(TAG, "onCancelled called " + databaseError);
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
}
