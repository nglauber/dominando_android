package dominando.android.ex31_mapas;

import com.google.android.gms.location.Geofence;

public class GeofenceInfo {

    final String mId;
    final double mLatitude;
    final double mLongitude;
    final float mRadius;
    long mExpirationDuration;
    int mTransitionType;

    public GeofenceInfo(String geofenceId, double latitude, double longitude,
                        float radius, long expiration, int transition) {
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }

    public Geofence getGeofence() {
        return new Geofence.Builder()
                .setRequestId(mId)
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(mLatitude, mLongitude, mRadius)
                .setExpirationDuration(mExpirationDuration)
                .build();
    }
}

