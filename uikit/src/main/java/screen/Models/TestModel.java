package screen.Models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;

import java.io.Serializable;

public class TestModel implements Parcelable {
    BaseMessage baseMessage;

    public TestModel(Parcel in) {
    }

    public static final Creator<TestModel> CREATOR = new Creator<TestModel>() {
        @Override
        public TestModel createFromParcel(Parcel in) {
            return new TestModel(in);
        }

        @Override
        public TestModel[] newArray(int size) {
            return new TestModel[size];
        }
    };

    public TestModel() {

    }

    public BaseMessage getBaseMessage() {
        return baseMessage;
    }

    public void setBaseMessage(BaseMessage baseMessage) {
        this.baseMessage = baseMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
