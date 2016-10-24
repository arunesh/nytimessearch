package com.codepath.nytimessearch.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by arunesh on 10/23/16.
 */

public class Filters implements Parcelable {

    int year;
    int month;
    int dayOfMonth;

    boolean sortOrderNewest;  // False indicates oldest, true newest.

    boolean hasArts;
    boolean hasFashion;
    boolean hasSports;

    public static Filters getDefault() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new Filters(year, month, day, false /* sort order oldest */,
                false /* has arts */,
                false /* has fashion */,
                false /* has sports */);
    }

    public Filters(int year, int month, int dayOfMonth, boolean sortOrderNewest, boolean hasArts,
                   boolean hasFashion, boolean hasSports) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.sortOrderNewest = sortOrderNewest;
        this.hasArts = hasArts;
        this.hasFashion = hasFashion;
        this.hasSports = hasSports;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public boolean isSortOrderNewest() {
        return sortOrderNewest;
    }

    public boolean isHasArts() {
        return hasArts;
    }

    public boolean isHasFashion() {
        return hasFashion;
    }

    public boolean isHasSports() {
        return hasSports;
    }

    public String getDateString() {
        return "" + month + "/" + dayOfMonth + "/" + year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setSortOrderNewest(boolean sortOrderNewest) {
        this.sortOrderNewest = sortOrderNewest;
    }

    public void setHasArts(boolean hasArts) {
        this.hasArts = hasArts;
    }

    public void setHasFashion(boolean hasFashion) {
        this.hasFashion = hasFashion;
    }

    public void setHasSports(boolean hasSports) {
        this.hasSports = hasSports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Filters{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", sortOrderNewest=" + sortOrderNewest +
                ", hasArts=" + hasArts +
                ", hasFashion=" + hasFashion +
                ", hasSports=" + hasSports +
                '}';
    }

    public ArrayList<String> getNewsDeskValues() {
        ArrayList<String> newsDeskList = new ArrayList<>();
        if (hasArts) newsDeskList.add("\"Arts\"");
        if (hasSports) newsDeskList.add("\"Sports\"");
        if (hasFashion) newsDeskList.add("\"Fashion & Style\"");
        return newsDeskList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.dayOfMonth);
        dest.writeByte(this.sortOrderNewest ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasArts ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasFashion ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasSports ? (byte) 1 : (byte) 0);
    }

    protected Filters(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.dayOfMonth = in.readInt();
        this.sortOrderNewest = in.readByte() != 0;
        this.hasArts = in.readByte() != 0;
        this.hasFashion = in.readByte() != 0;
        this.hasSports = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Filters> CREATOR = new Parcelable.Creator<Filters>() {
        @Override
        public Filters createFromParcel(Parcel source) {
            return new Filters(source);
        }

        @Override
        public Filters[] newArray(int size) {
            return new Filters[size];
        }
    };
}
