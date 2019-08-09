package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.domain.Date;


public class QueryLib {
    private RichTable queryRecord;

    public QueryLib() {
    }

    public void updateQueryDialogListener(DialogEvent dialogEvent) {
        System.out.println("------------------QueryLib.updateQueryDialogListener[Outcome : " + dialogEvent.getOutcome() +
                           "]------------------");

        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
            doCommit();
            refreshPartialTargets();
        }
    }

    public void updateQueryCancelListener(PopupCanceledEvent popupCanceledEvent) {
        System.out.println("------------------QueryLib.updateQueryCancelListener------------------");

        doRollback();
        refreshPartialTargets();
    }

    public void updateQueryPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        System.out.println("------------------QueryLib.updateQueryPopupFetchListener------------------");

        commitQuerySeenForLibrarian();
        Object uName = ADFUtils.evaluateEL("#{stDashboard.currentUser}");

        //TODO for unit testing, remove this in intgration phase
        //uName = "Sun";

        ADFUtils.setEL("#{bindings.ResolvedBy.inputValue}", uName);
        ADFUtils.setEL("#{bindings.ResolvedOn.inputValue}", new Date(Date.getCurrentDate()));
        //flip student seen to unseen
        ADFUtils.setEL("#{bindings.Seen.inputValue}", "01");
    }

    private void commitQuerySeenForLibrarian() {
        //set query seen for Librarian and keep student seen flag as it is
        String currentSeenFlag = (String)ADFUtils.evaluateEL("#{bindings.Seen.inputValue}");
        ADFUtils.setEL("#{bindings.Seen.inputValue}", currentSeenFlag.charAt(0) + "1");
        doCommit();
    }

    private void doCommit() {
        OperationBinding op = ADFUtils.executeOperationBinding("Commit");

        if (op.getErrors() != null && op.getErrors().size() > 0)
            System.out.println("executeOperationBinding.errors[Commit] : " + op.getErrors());
    }

    private void doRollback() {
        OperationBinding op = ADFUtils.executeOperationBinding("Rollback");

        if (op.getErrors() != null && op.getErrors().size() > 0)
            System.out.println("executeOperationBinding.errors[Rollback] : " + op.getErrors());
    }

    private void refreshPartialTargets() {
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(this.getQueryRecord());
    }

    public String getStudentSeenAsReadable() {
        String stSeen = (String)ADFUtils.evaluateEL("#{row.Seen}");
        return stSeen.charAt(0) == '1' ? "\u2714" : (stSeen.charAt(0) == '0' ? "X" : "-");
    }

    public String getLibrarianSeenAsReadable() {
        String libSeen = (String)ADFUtils.evaluateEL("#{row.Seen}");
        return libSeen.charAt(1) == '1' ? "\u2714" : (libSeen.charAt(1) == '0' ? "X" : "-");
    }

    public String getRowBGColor() {
        String libSeen = (String)ADFUtils.evaluateEL("#{row.Seen}");
        switch (libSeen.charAt(1)) {
        case '1':
            return "transparent";
        case '0':
            return "Yellow";
        default:
            return "transparent";
        }
    }

    public String getStatusColor() {
        String status = (String)ADFUtils.evaluateEL("#{row.Status}");
        if (status.equals("OPEN"))
            return "Red";
        else if (status.equals("RESOLVED"))
            return "Green";
        else if (status.equals("REOPENED"))
            return "Blue";
        else
            return "Black";
    }

    public void setQueryRecord(RichTable queryRecord) {
        this.queryRecord = queryRecord;
    }

    public RichTable getQueryRecord() {
        return queryRecord;
    }
}
