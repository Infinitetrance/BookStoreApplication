package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;
import bookStore.ui.commons.Utils;

import javax.faces.application.FacesMessage;

import oracle.adf.share.ADFContext;
import oracle.adf.share.security.SecurityContext;

import oracle.jbo.domain.Date;


public class StDashboard {

    public String getCurrentUser() {
        System.out.println("------------------StDashboard.getCurrentUser------------------");

        SecurityContext sctx = ADFContext.getCurrent().getSecurityContext();
        if (sctx.isAuthenticated() && sctx.getUserName() != null) {

            System.out.println("{sctx.isAuthenticated() && sctx.getUserName() != null} uname : " + sctx.getUserName());

            return sctx.getUserName();
        }
        String errorMsg =
            String.format("At StDashboard.getCurrentUser isAuthenticated : %s, UName : %s", sctx.isAuthenticated(), sctx.getUserName());
        ADFUtils.addFacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg);

        System.out.println(errorMsg);
        return null;
    }

    public Date getReturnDateFromIssueDate() {
        Date before = (Date)ADFUtils.evaluateEL("#{row.IssueDate}");

        System.out.println("------------------StDashboard.getReturnDateFromIssueDate[before : " + before + "]------------------");

        return Utils.addDaystoDate("yyyy-MM-dd", before, 7);
    }

    public String getReturnDateColor() {
        Date returnDate = getReturnDateFromIssueDate();
        Date today = new Date(Date.getCurrentDate());

        System.out.println("------------------StDashboard.returnDateColor[returnDate : " + returnDate + ", today : " + today +
                           "]------------------");

        return today.equals(returnDate) ? "Red" : "Navy";
    }

}
