package com.example.quizflow.domains;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionModel implements Parcelable {

    private int id;
    private String question;
    private String answer1;
    private String answer2;

    private String answer3;

    private String answer4;

    private String correctAnswer;

    private int score;

    private String picPath;

    private String clickedAnswer;

    public QuestionModel(int id, String question, String answer1, String answer2, String answer3, String answer4,
                         String correctAnswer, int score, String picPath, String clickedAnswer) {
        this.id = id;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.score = score;
        this.picPath = picPath;
        this.clickedAnswer = clickedAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getClickedAnswer() {
        return clickedAnswer;
    }

    public void setClickedAnswer(String clickedAnswer) {
        this.clickedAnswer = clickedAnswer;
    }

    protected QuestionModel(Parcel in) {
        id = in.readInt();
        question = in.readString();
        answer1 = in.readString();
        answer2 = in.readString();
        answer3 = in.readString();
        answer4 = in.readString();
        correctAnswer = in.readString();
        score = in.readInt();
        picPath = in.readString();
        clickedAnswer = in.readString();
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeString(answer1);
        dest.writeString(answer2);
        dest.writeString(answer3);
        dest.writeString(answer4);
        dest.writeString(correctAnswer);
        dest.writeInt(score);
        dest.writeString(picPath);
        dest.writeString(clickedAnswer);
    }
}
