package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;

import java.util.ArrayList;
import java.util.List;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;

import oracle.jbo.Row;
import oracle.jbo.VariableValueManager;
import oracle.jbo.ViewCriteria;
import oracle.jbo.server.ViewObjectImpl;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;


public class Notifications {
    private List<NotificationItem> notificationItems = new ArrayList<NotificationItem>();

    public Notifications() {
        init();
    }

    public void setNotificationItems(List<NotificationItem> notificationItems) {
        this.notificationItems = notificationItems;
    }

    public List<NotificationItem> getNotificationItems() {
        return notificationItems;
    }

    private void addQueryNotification(String queryID, String query, String status, String updatedBy, String actionOutcome) {
        String notificationText =
            String.format("Query[%s, %s] with status[%s] updated by[%s, Librarian]", queryID, query.substring(0, query.length() < 20 ?
                                                                                                                 query.length() : 20), status,
                          updatedBy);

        System.out.println("------------------Notifications.addQueryNotification------------------");
        System.out.println("notificationText : " + notificationText);

        NotificationItem ni = new NotificationItem(notificationText, actionOutcome);
        ni.getAuxilaryPayload().put("queryID", queryID);
        notificationItems.add(ni);
    }

    private void addQueryNotification(Row row) {
        Object recordId = row.getAttribute("RecordId");
        Object query = row.getAttribute("Query");
        Object status = row.getAttribute("Status");
        Object resolvedBy = row.getAttribute("ResolvedBy");

        addQueryNotification(recordId.toString(), query.toString(), status.toString(), resolvedBy.toString(), "queries");
    }

    private void init() {
        System.out.println("------------------Notifications.init[notificationItems size : " + notificationItems.size() + "]------------------");

        DCBindingContainer container = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();

        JUCtrlHierBinding cntrlOb = (JUCtrlHierBinding)container.findCtrlBinding("QueryRecordVOInstance");
        ViewObjectImpl vo = (ViewObjectImpl)cntrlOb.getViewObject();
        vo.removeApplyViewCriteriaName("QueryRecordByUNameVOCriteria");
        ViewCriteria vc = vo.getViewCriteria("QueryRecordByUserSeensVOCriteria");
        vc.resetCriteria();
        VariableValueManager vvm = vc.ensureVariableManager();
        vvm.setVariableValue("b_UName", ADFUtils.invokeEL("#{stDashboard.getCurrentUser}"));
        vvm.setVariableValue("b_Seen", "01");
        vo.applyViewCriteria(vc, true);
        vo.executeQuery();

        System.out.println("getRowCount: " + vo.getRowCount());
        vo.reset();

        Row first = vo.first();
        if (first != null) {
            System.out.println("--first row");
            addQueryNotification(first);
        }

        int rcnt = 0;
        while (vo.hasNext()) {
            System.out.println("rcnt : " + (rcnt++));
            Row row = vo.next();
            addQueryNotification(row);
        }
    }

}
