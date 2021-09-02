package com.example.desgarron.Logic.Tasks;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.desgarron.Models.Direction;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class SigurLog implements Parcelable {
    public static final Parcelable.Creator<SigurLog> CREATOR = new Parcelable.Creator<SigurLog>() {
        public SigurLog createFromParcel(Parcel parcel) {
            try {
                return new SigurLog(parcel.readLong(), parcel.readInt(), parcel.readInt(), SigurLog.sFormatter.parse(parcel.readString()));
            } catch (ParseException e) {
                throw new IllegalStateException(e);
            }
        }

        public SigurLog[] newArray(int i) {
            return new SigurLog[i];
        }
    };
    private static final String sDatetimeKey = "datetime";
    private static final String sDirectionKey = "direction";
    private static final String sEmpIdKey = "emp_id";
    /* access modifiers changed from: private */
    public static final SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String sIdKey = "id";
    private final Date mDatetime;
    private final int mDirection;
    private final int mEmpId;
    private final long mId;

    public int describeContents() {
        return 0;
    }

    public int getEmpId() {
        return this.mEmpId;
    }

    public int getDirection() {
        return this.mDirection;
    }

    SigurLog(long j, int i, int i2, Date date) {
        if (j < 1) {
            throw new IllegalArgumentException();
        } else if (i < 1) {
            throw new IllegalArgumentException();
        } else if (i2 != Direction.IN && i2 != Direction.OUT) {
            throw new IllegalArgumentException();
        } else if (date != null) {
            this.mId = j;
            this.mEmpId = i;
            this.mDirection = i2;
            this.mDatetime = date;
            android.util.Log.d("Lizard mId",mId+"");
            android.util.Log.d("Lizard empid",mEmpId+"");
            android.util.Log.d("Lizard direction",mDirection+"");
            android.util.Log.d("Lizard date",mDatetime+"");
        } else {
            throw new IllegalArgumentException();
        }
    }

    /* access modifiers changed from: package-private */
    public JSONObject toJson() {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(sIdKey, this.mId);
            jSONObject.put(sEmpIdKey, this.mEmpId);
            jSONObject.put(sDirectionKey, this.mDirection);
            jSONObject.put(sDatetimeKey, sFormatter.format(this.mDatetime));
            return jSONObject;
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    static SigurLog fromIdAndJson(long j, JSONObject jSONObject) throws JSONException {
        Date date;
        if (j < 1) {
            throw new IllegalArgumentException();
        } else if (jSONObject != null) {
            int i = jSONObject.getInt(sEmpIdKey);
            int i2 = jSONObject.getInt(sDirectionKey);
            try {
                date = sFormatter.parse(jSONObject.getString(sDatetimeKey));
            } catch (ParseException unused) {
                date = null;
            }
            if (i < 1) {
                throw new JSONException("invalid emp id");
            } else if (i2 != Direction.IN && i2 != Direction.OUT) {
                throw new JSONException("invalid direction");
            } else if (date != null) {
                return new SigurLog(j, i, i2, date);
            } else {
                throw new JSONException("invalid datetime");
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    static String toJsonString(List<SigurLog> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"logs\":");
        sb.append("[");
        Iterator<SigurLog> it = list.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toJson().toString());
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    static String toJsonString(int i, int i2, Date date) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(sEmpIdKey, i);
            jSONObject.put(sDirectionKey, i2);
            jSONObject.put(sDatetimeKey, sFormatter.format(date));
            return jSONObject.toString();
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.mId);
        parcel.writeInt(this.mEmpId);
        parcel.writeInt(this.mDirection);
        parcel.writeString(sFormatter.format(this.mDatetime));
    }
}
