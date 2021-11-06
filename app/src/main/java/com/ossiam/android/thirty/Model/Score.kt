package com.ossiam.android.thirty.Model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Score() : Parcelable{
    var points: Int = 0
    var pointScoring: String = ""

    constructor(parcel: Parcel) : this() {
        points = parcel.readInt()
        pointScoring = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(points)
        parcel.writeString(pointScoring)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Score> {
        override fun createFromParcel(parcel: Parcel): Score {
            return Score(parcel)
        }

        override fun newArray(size: Int): Array<Score?> {
            return arrayOfNulls(size)
        }
    }
}