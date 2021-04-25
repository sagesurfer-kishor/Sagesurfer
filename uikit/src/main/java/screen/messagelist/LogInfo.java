package screen.messagelist;

public class LogInfo {
    String comet_chat_type,chat_type,group_id,user_id;

    public LogInfo(String comet_chat_type, String chat_type, String group_id, String user_id) {
        this.comet_chat_type = comet_chat_type;
        this.chat_type = chat_type;
        this.group_id = group_id;
        this.user_id = user_id;
    }

    public String getComet_chat_type() {
        return comet_chat_type;
    }

    public void setComet_chat_type(String comet_chat_type) {
        this.comet_chat_type = comet_chat_type;
    }

    public String getChat_type() {
        return chat_type;
    }

    public void setChat_type(String chat_type) {
        this.chat_type = chat_type;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
