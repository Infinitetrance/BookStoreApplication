package bookStore.ui.beans;

import java.util.HashMap;
import java.util.Map;

public class NotificationItem {
    private String notificationText;
    private String actionOutcome;
    private Map<String, Object> auxilaryPayload = new HashMap<String, Object>();

    public NotificationItem() {
    }

    public NotificationItem(String notificationText, String actionOutcome) {
        this.notificationText = notificationText;
        this.actionOutcome = actionOutcome;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setActionOutcome(String actionOutcome) {
        this.actionOutcome = actionOutcome;
    }

    public String getActionOutcome() {
        System.out.println("------------------NotificationItem.getActionOutcome[" + actionOutcome + "]------------------");
        System.out.println("queryID : " + auxilaryPayload.get("queryID"));

        return actionOutcome;
    }

    public Map<String, Object> getAuxilaryPayload() {
        return auxilaryPayload;
    }
}
