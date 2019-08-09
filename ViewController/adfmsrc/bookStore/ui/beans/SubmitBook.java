package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.OperationBinding;

import oracle.jbo.domain.Date;


public class SubmitBook {
    private RichTable issueRecord;

    public SubmitBook() {
    }

    public void submitBookCTBActionListener(ActionEvent actionEvent) {
        System.out.println("------------------SubmitBook.submitBookCTBActionListener[BookId : " +
                           ADFUtils.evaluateEL("#{bindings.BookIdFromIssueRecord.inputValue}") + "]------------------");

        try {
            ADFUtils.setEL("#{bindings.Status.inputValue}", "AVAILABLE");
            ADFUtils.setEL("#{bindings.ReturnDate.inputValue}", new Date(Date.getCurrentDate()));
            doCommit();
            ADFUtils.addFacesMessageInfo(String.format("Book [%s, %s] issued to [%s] is submited",
                                                       ADFUtils.evaluateEL("#{bindings.BookIdFromIssueRecord.inputValue}"),
                                                       ADFUtils.evaluateEL("#{bindings.Title.inputValue}"),
                                                       ADFUtils.evaluateEL("#{bindings.UName.inputValue}")));
        } catch (Throwable t) {
            doRollback();
            ADFUtils.addFacesMessageError("Could not submit! error : " + t);
            t.printStackTrace();
        }
        refreshPartialTargets();
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
        adfFacesContext.addPartialTarget(this.getIssueRecord());
    }

    public void setIssueRecord(RichTable issueRecord) {
        this.issueRecord = issueRecord;
    }

    public RichTable getIssueRecord() {
        return issueRecord;
    }
}
