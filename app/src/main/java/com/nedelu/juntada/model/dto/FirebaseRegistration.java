package com.nedelu.juntada.model.dto;

public class FirebaseRegistration {

    private Long userId;
    private String firebaseId;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getFirebaseId() {
        return firebaseId;
    }
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }


}
