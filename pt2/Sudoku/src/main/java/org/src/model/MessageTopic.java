package org.src.model;

public enum MessageTopic {
    NEW_USER_JOINED("grid.user.join"),
    USER_LEFT("grid.user.left"),
    UPDATE_GRID("grid.update");

    private final String topic;

    MessageTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

}