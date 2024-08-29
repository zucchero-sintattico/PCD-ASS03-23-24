package logic;

public enum MessageTopic {

    NEW_USER_JOINED("grid.user.join"),
    UPDATE_GRID("grid.update");

    private final String topic;

    MessageTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }

}