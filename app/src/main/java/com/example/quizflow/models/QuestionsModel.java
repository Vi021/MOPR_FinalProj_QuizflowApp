package com.example.quizflow.models;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionsModel implements Parcelable {
    private Long qtid;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String correctAnswer;
    private int score;
    private String id;
    private String clickedAnswer;
    private String[] aids;

    public QuestionsModel(Long qtid, String question, String answer1, String answer2, String answer3, String answer4,
                          String correctAnswer, int score, String id, String clickedAnswer, String[] aids) {
        this.qtid = qtid;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.score = score;
        this.id = id;
        this.clickedAnswer = clickedAnswer;
        this.aids = aids;
    }
    public Long getQtid() { return qtid; }
    public String getQuestion() { return question; }
    public String getAnswer1() { return answer1; }
    public String getAnswer2() { return answer2; }
    public String getAnswer3() { return answer3; }
    public String getAnswer4() { return answer4; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getScore() { return score; }
    public String getId() { return id; }
    public String getClickedAnswer() { return clickedAnswer; }
    public void setClickedAnswer(String clickedAnswer) { this.clickedAnswer = clickedAnswer; }
    public String[] getAids() { return aids; }

    protected QuestionsModel(Parcel in) {
        long tmpQtid = in.readLong();
        qtid = tmpQtid == -1 ? null : tmpQtid;
        question = in.readString();
        answer1 = in.readString();
        answer2 = in.readString();
        answer3 = in.readString();
        answer4 = in.readString();
        correctAnswer = in.readString();
        score = in.readInt();
        id = in.readString();
        clickedAnswer = in.readString();
        aids = in.createStringArray();
    }

    public static final Creator<QuestionsModel> CREATOR = new Creator<QuestionsModel>() {
        @Override
        public QuestionsModel createFromParcel(Parcel in) {
            return new QuestionsModel(in);
        }

        @Override
        public QuestionsModel[] newArray(int size) {
            return new QuestionsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(qtid == null ? -1 : qtid);
        dest.writeString(question);
        dest.writeString(answer1);
        dest.writeString(answer2);
        dest.writeString(answer3);
        dest.writeString(answer4);
        dest.writeString(correctAnswer);
        dest.writeInt(score);
        dest.writeString(id);
        dest.writeString(clickedAnswer);
        dest.writeStringArray(aids);
    }
}
