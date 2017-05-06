package ogiba.styleablesharedialog.ShareDialog.Utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ogiba on 06.05.2017.
 * <p>
 * Contains information about percentage values of {@link ogiba.styleablesharedialog.ShareDialog.ShareDialog}
 * width and height
 */

public class Ratio implements Parcelable {
    private double x;
    private double y;

    /**
     * Default constructor that create new instance of {@link Ratio}. Allows to set percentage values
     * of width and height view covering
     *
     * @param x {@link Double} value that represent percentage width
     *          covering of {@link ogiba.styleablesharedialog.ShareDialog.ShareDialog}.
     *          Should be between: 0.0 and 1.0
     * @param y {@link Double} value that represent percentage height
     *          covering of {@link ogiba.styleablesharedialog.ShareDialog.ShareDialog}.
     *          Should be between: 0.0 and 1.0
     */
    public Ratio(double x, double y) {
        this.x = x;
        this.y = y;
    }


    protected Ratio(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ratio> CREATOR = new Parcelable.Creator<Ratio>() {
        @Override
        public Ratio createFromParcel(Parcel in) {
            return new Ratio(in);
        }

        @Override
        public Ratio[] newArray(int size) {
            return new Ratio[size];
        }
    };

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
