package com.example.quizflow.models;

import java.io.Serializable;

public class QuizModel implements Serializable {
    private long qid;
    private String title;
    private String description;
    private String topic;   // category
    private boolean isPublic;
    private String createdDate;
    private int questionCount;
    private long duration;   // in seconds
    private long uid;

    private int attemptCount;
    private byte questionType;   //1:mcq, 2:true/false, 3:short answer, 4: mcq + true/false, 5: mcq + short answer, 6: true/false + short answer, 7: all

    public QuizModel() { }

    public QuizModel(Long qid, String title, String description, String topic, boolean isPublic, String createdDate, int questionCount, long duration, long uid, int attemptCount, byte questionType) {
        this.qid = qid;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.isPublic = isPublic;
        this.createdDate = createdDate;
        this.questionCount = questionCount;
        this.duration = duration;
        this.uid = uid;
        this.attemptCount = attemptCount;
        this.questionType = questionType;
    }

    public Long getQid() {
        return qid;
    }
    public void setQid(Long qid) {
        this.qid = qid;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getQuestionCount() {
        return questionCount;
    }
    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public long getDuration() {
        return duration;
    }
    public String getDurationString() {
        int hours = (int) (duration / 3600);
        int minutes = (int) ((duration % 3600) / 60);
        int seconds = (int) (duration % 60);
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString();
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getAttemptCount() {
        return attemptCount;
    }
    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public byte getQuestionType() {
        return questionType;
    }
    public void setQuestionType(byte questionType) {
        this.questionType = questionType;
    }

    public long getUid() {
        return uid;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
}
