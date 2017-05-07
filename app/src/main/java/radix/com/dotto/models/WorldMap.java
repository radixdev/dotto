package radix.com.dotto.models;

import android.graphics.Point;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import radix.com.dotto.controllers.PixelInfo;

public class WorldMap implements IModelInterface {
  private final String TAG = WorldMap.class.toString();
  private final List<PixelInfo> mPixelData;
  private final List<PixelInfo> mReturnedPixelData;
  private DatabaseReference mPixelPathReference;

  public WorldMap() {
    mPixelData = new CopyOnWriteArrayList<>();
    mReturnedPixelData = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    mPixelPathReference = database.getReference(ProtocolConstants.PIXEL_PATH);
    mPixelPathReference.keepSynced(true);
    mPixelPathReference.addChildEventListener(new PixelChildChangeListener());
  }

  @Override
  public void onPixelInfoChange(PixelInfo info) {
    // verify the info is correct
    if (info.getPointX() < 0 || info.getPointX() > getWorldWidth() || info.getPointY() < 0 || info.getPointY() > getWorldHeight()) {
      Log.d(TAG, "Tried to draw out of bounds: " + info);
      return;
    }

    Log.d(TAG, "Adding info at: " + info);
    // turn the x,y into a key
    String key = ProtocolHandler.getKeyFromCoordinates(info.getPointX(), info.getPointY());
    mPixelPathReference.child(key).setValue(info.getColor().getCode());
  }

  /**
   * @param maxElements max elements to return
   * @return a list of tap infos. These objects are not guaranteed to exist beyond the current frame!
   */
  @Override
  public List<PixelInfo> getGridInfo(int maxElements) {
    Log.d(TAG, "Got pixels: " + mPixelData.size());
    mReturnedPixelData.clear();

    List<PixelInfo> pixelList = Collections.synchronizedList(mPixelData);
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

    PixelInfo info = ProtocolHandler.getPixelInfoFromData(key, colorCode);

    List<PixelInfo> infoList = Collections.synchronizedList(mPixelData);
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
//      Log.v(TAG, "onChildChanged called " + dataSnapshot);
      addPixelInfo(dataSnapshot);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//      Log.v(TAG, "onChildAdded called " + dataSnapshot);
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
}
