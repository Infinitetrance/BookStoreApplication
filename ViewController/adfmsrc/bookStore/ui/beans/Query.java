package bookStore.ui.beans;


import bookStore.model.module.StudentChoresAMImpl;

import bookStore.ui.commons.ADFUtils;
import bookStore.ui.commons.Utils;

import javax.faces.application.FacesMessage;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.nav.RichCommandToolbarButton;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.client.Configuration;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Date;


public class Query {
    private RichTable queryRecord;
    private RichCommandToolbarButton updateReopenCTB;
    private RichCommandToolbarButton deleteCTB;

    public Query() {
    }

    public void newQueryDialogListener(DialogEvent dialogEvent) {
        System.out.println("------------------Query.newQueryDialogListener------------------");
        System.out.println("DialogEvent.Outcome : " + dialogEvent.getOutcome());

        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
            OperationBinding op = ADFUtils.executeOperationBinding("Commit");
            refreshPartialTargets();

            System.out.println("executeOperationBinding.errors[Commit] : " + op.getErrors());
        }
    }

    public void cancelListener(PopupCanceledEvent popupCanceledEvent) {
        System.out.println("------------------Query.cancelListener------------------");

        doRollback();
        refreshPartialTargets();
    }

    private void doRollback() {
        OperationBinding op = ADFUtils.executeOperationBinding("Rollback");

        System.out.println("executeOperationBinding.errors[Rollback] : " + op.getErrors());
    }

    public String getRowBGColor() {
        String stSeen = (String)ADFUtils.evaluateEL("#{row.Seen}");
        switch (stSeen.charAt(0)) {
        case '1':
            return "transparent";
        case '0':
            return "Yellow";
        default:
            return "transparent";
        }
    }

    public void newQueryPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        System.out.println("------------------Query.newQueryPopupFetchListener------------------");

        OperationBinding op = ADFUtils.executeOperationBinding("CreateInsert");
        ADFUtils.setEL("#{bindings.RecordId.inputValue}", this.getNewRecordId());
        String uName = ADFContext.getCurrent().getSecurityContext().getUserName();
        if (uName == null || uName.equals("anonymous")) {
            ADFUtils.addFacesMessageError("Invalid Credentials! userName : " + uName);
            return;
        }
        //TODO for temp unit testing, remove during integration phase
        //uName = "Moon";

        ADFUtils.setEL("#{bindings.LoggedBy.inputValue}", uName);
        ADFUtils.setEL("#{bindings.LoggedOn.inputValue}", new Date(new java.sql.Date(System.currentTimeMillis())));
        ADFUtils.setEL("#{bindings.Status.inputValue}", "OPEN");
        ADFUtils.setEL("#{bindings.Seen.inputValue}", "10");
    }

    private void commitQuerySeenForStudent() {
        //set query seen for student and keep librarian seen flag as it is
        String currentSeenFlag = (String)ADFUtils.evaluateEL("#{bindings.Seen.inputValue}");
        ADFUtils.setEL("#{bindings.Seen.inputValue}", "1" + currentSeenFlag.charAt(1));
        doCommit();
    }

    public void updateQueryPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        System.out.println("------------------Query.updateQueryPopupFetchListener------------------");

        commitQuerySeenForStudent();
        String currentStatus = (String)ADFUtils.evaluateEL("#{bindings.Status.inputValue}");
        if ("OPEN".equals(currentStatus)) {
            ADFUtils.setEL("#{bindings.Status.inputValue}", "OPEN");
        } else if ("RESOLVED".equals(currentStatus)) {
            ADFUtils.setEL("#{bindings.Status.inputValue}", "REOPENED");
        } else if ("REOPENED".equals(currentStatus)) {
            ADFUtils.setEL("#{bindings.Status.inputValue}", "REOPENED");
        } else {
            System.out.println("Invalid currentStatus : " + currentStatus);
            ADFUtils.addFacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid currentStatus : " + currentStatus);
            return;
        }
        ADFUtils.setEL("#{bindings.Seen.inputValue}", "10");
    }

    public void updateQueryDialogListener(DialogEvent dialogEvent) {
        System.out.println("------------------Query.updateQueryDialogListener------------------");
        System.out.println("DialogEvent.Outcome : " + dialogEvent.getOutcome());

        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
            doCommit();
            refreshPartialTargets();
        }
    }

    private void doCommit() {
        OperationBinding op = ADFUtils.executeOperationBinding("Commit");

        System.out.println("executeOperationBinding.errors[Commit] : " + op.getErrors());
    }

    public boolean getDisableDelete() {
        String fix = (String)ADFUtils.evaluateEL("#{bindings.Fix.inputValue}");

        System.out.println("------------------Query.disableDelete[fix : '" + fix + "']------------------");

        long rowCount = (Long)ADFUtils.invokeEL("#{bindings.QueryRecordVOInstanceIterator.getEstimatedRowCount}");
        if (rowCount <= 0) {
            System.out.println("DisableDelete estimatedRowCount : " + rowCount);
            if (fix == null) {
                //there aren't any rows in table, so diable delete button
                return true;
            }
        }
        if (Utils.isNullOrEmpty(fix)) {
            return false;
        }
        return true;
    }

    public boolean getDisableUpdateReOpen() {
        long rowCount = (Long)ADFUtils.invokeEL("#{bindings.QueryRecordVOInstanceIterator.getEstimatedRowCount}");

        System.out.println("------------------Query.getDisableUpdateReOpen[rowCount : '" + rowCount + "']------------------");

        if (rowCount <= 0) {
            return true;
        }
        return false;
    }

    public void deleteQueryDialogListener(DialogEvent dialogEvent) {
        System.out.println("------------------Query.deleteQueryDialogListener------------------");
        System.out.println("DialogEvent.Outcome : " + dialogEvent.getOutcome());

        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
            String currentStatus = (String)ADFUtils.evaluateEL("#{bindings.Status.inputValue}");

            if (!"OPEN".equals(currentStatus)) {
                System.out.println("Invalid currentStatus : " + currentStatus);
                ADFUtils.addFacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid currentStatus : " + currentStatus);
                return;
            }
            OperationBinding op = ADFUtils.executeOperationBinding("Delete");

            System.out.println("executeOperationBinding.errors[Delete] : " + op.getErrors());

            op = ADFUtils.executeOperationBinding("Commit");
            refreshPartialTargets();

            System.out.println("executeOperationBinding.errors[Commit] : " + op.getErrors());
        }
    }

    private DBSequence getNewRecordId() {
        DBSequence dbSeq;
        ADFContext oldContext = ADFContext.initADFContext(null, null, null, null);
        try {
            StudentChoresAMImpl am =
                (StudentChoresAMImpl)Configuration.createRootApplicationModule("bookStore.model.module.StudentChoresAM", "StudentChoresAMLocal");
            // Learning: avoid this apporach of creating new AM instance to access methods in AM at bean,
            // instaed add these methods like "getNextDBSequence" in client interface and and than access them in bean through bindings layer
            // this way all your operations will be withing singel AM and you'll not end up with inconsistency between DB and EO cache due to operations
            // executed through diffrent AM instances
            dbSeq = am.getNextDBSequence();
            Configuration.releaseRootApplicationModule(am, true);
        } finally {
            ADFContext.resetADFContext(oldContext);
        }
        return dbSeq;
    }

    private void refreshPartialTargets() {
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(this.getQueryRecord());
        adfFacesContext.addPartialTarget(this.getUpdateReopenCTB());
        adfFacesContext.addPartialTarget(this.getDeleteCTB());
    }

    public void setQueryRecord(RichTable queryRecord) {
        this.queryRecord = queryRecord;
    }

    public RichTable getQueryRecord() {
        return queryRecord;
    }

    public void setUpdateReopenCTB(RichCommandToolbarButton updateReopenCTB) {
        this.updateReopenCTB = updateReopenCTB;
    }

    public RichCommandToolbarButton getUpdateReopenCTB() {
        return updateReopenCTB;
    }

    public void setDeleteCTB(RichCommandToolbarButton deleteCTB) {
        this.deleteCTB = deleteCTB;
    }

    public RichCommandToolbarButton getDeleteCTB() {
        return deleteCTB;
    }
}
