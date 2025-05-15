package com.example.quizflow.models;

import java.util.List;

public class LobbyStatus {
    private Long lid;
    private String status;
    private Long qid;
    private Integer currentQuestionIndex;
    private List<ParticipantStatus> participants;

    public Long getLid() { return lid; }
    public void setLid(Long lid) { this.lid = lid; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getQid() { return qid; }
    public void setQid(Long qid) { this.qid = qid; }
    public Integer getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(Integer currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }
    public List<ParticipantStatus> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantStatus> participants) { this.participants = participants; }
}
