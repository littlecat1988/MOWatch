package care.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wid3344 on 2015/8/26.
 */
public class DisturbBean implements Parcelable {
    private int week;

    private String timeFirst;

    private String timeSecond;

    private String timeThirth;

    private String timeFour;

    private String timeFive;

    private String timeSix;

    public DisturbBean() {
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getTimeFirst() {
        return timeFirst;
    }

    public void setTimeFirst(String timeFirst) {
        this.timeFirst = timeFirst;
    }

    public String getTimeSecond() {
        return timeSecond;
    }

    public void setTimeSecond(String timeSecond) {
        this.timeSecond = timeSecond;
    }

    public String getTimeThirth() {
        return timeThirth;
    }

    public void setTimeThirth(String timeThirth) {
        this.timeThirth = timeThirth;
    }

    public String getTimeFour() {
        return timeFour;
    }

    public void setTimeFour(String timeFour) {
        this.timeFour = timeFour;
    }

    public String getTimeFive() {
        return timeFive;
    }

    public void setTimeFive(String timeFive) {
        this.timeFive = timeFive;
    }

    public String getTimeSix() {
        return timeSix;
    }

    public void setTimeSix(String timeSix) {
        this.timeSix = timeSix;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.week);
        dest.writeString(this.timeFirst);
        dest.writeString(this.timeSecond);
        dest.writeString(this.timeThirth);
        dest.writeString(this.timeFour);
        dest.writeString(this.timeFive);
        dest.writeString(this.timeSix);
    }

    protected DisturbBean(Parcel in) {
        this.week = in.readInt();
        this.timeFirst = in.readString();
        this.timeSecond = in.readString();
        this.timeThirth = in.readString();
        this.timeFour = in.readString();
        this.timeFive = in.readString();
        this.timeSix = in.readString();
    }

    public static final Creator<DisturbBean> CREATOR = new Creator<DisturbBean>() {
        public DisturbBean createFromParcel(Parcel source) {
            return new DisturbBean(source);
        }

        public DisturbBean[] newArray(int size) {
            return new DisturbBean[size];
        }
    };
}
