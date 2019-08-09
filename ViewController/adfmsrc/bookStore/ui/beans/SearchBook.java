package bookStore.ui.beans;


import bookStore.model.module.StudentChoresAMImpl;

import bookStore.ui.commons.ADFUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.client.Configuration;
import oracle.jbo.domain.Number;


// keep this in view scope else bookReserved will be read as false during subsequesnt(requests after reserve book due to partial tigger of table on reserve button) PPR request from table
public class SearchBook {

    private RichPopup reserveBook_Popup;
    // bookId field at form inside reserveBook_Popup, during partial submit request of Reserve button we want this information to go with request,
    // else we would not know what bookid is displayed in popup form because Reserve button is not part of form i.e it is not form submit button
    private Number bookId;

    // it is always true, table in search book page is refreshed upon this condition
    private boolean refreshCond = true;
    // flag used to decide whether to refresh book textual VO or not, during table refresh, so when book is reserved it is set so as to refresh table with disabled title link
    // and updated book status
    private boolean bookReserved = false;
    private RichInputText searchField;

    private void refreshBooksTextualVO() {
        if (bookReserved) {
            System.out.println("------------------SearchBook.refreshBooksTextualVO------------------");
            bookReserved = false;
            //Learning : this could have been avoided if reserveBook was added in client interface and called from binding layer in reserveCTB_action instead
            // of calling reserveBook from diffrent AM instance
            ADFUtils.invokeEL("#{bindings.refreshBooksTextualVO.execute}");
        }
    }

    public String reserveCTB_action() {
        System.out.println("------------------SearchBook.reserveCTB_action------------------");
        ADFContext oldContext =
            ADFContext.initADFContext(null, null, null, null);
        FacesContext fctx = FacesContext.getCurrentInstance();
        try {
            StudentChoresAMImpl am =
                (StudentChoresAMImpl)Configuration.createRootApplicationModule("bookStore.model.module.StudentChoresAM",
                                                                               "StudentChoresAMLocal");
            Number bookId = this.getBookId();
            String userName =
                ADFContext.getCurrent().getSecurityContext().getUserName();

            if (userName == null || userName.equals("anonymous")) {
                ADFUtils.addFacesMessageError("Invalid Credentials! userName : " +
                                              userName);
                return null;
            }

            System.out.println("bookId : " + bookId + " - userName : " +
                               userName);
            //TODO for dev phase unit testing, remove in integration phase
            //userName = "Moon";

            // Learning: avoid this apporach of creating new AM instance to access methods in AM at bean,
            // instaed add these methods like "reserveBook" in client interface and and than access them in bean through bindings layer
            // this way all your operations will be withing singel AM and you'll not end up with inconsistency between DB and EO cache due to operations
            // executed through diffrent AM instances
            am.reserveBook(userName, bookId);
            bookReserved = true;
            Configuration.releaseRootApplicationModule(am, true);
            this.getReserveBook_Popup().hide();
            fctx.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Reservation Successful!",
                                             "Reservation Done!"));
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg =
                String.format("Reservation Failed! errMsg : %s", e.getMessage());
            fctx.addMessage(this.getReserveBook_Popup().getId(),
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                             errorMsg, errorMsg));
        } finally {
            ADFContext.resetADFContext(oldContext);
        }
        return null;
    }

    public String cancelCTB_action() {
        System.out.println("------------------SearchBook.cancelCTB_action------------------");
        //debug log
        System.out.println("bookId : " + this.getBookId() + " - userName : " +
                           ADFContext.getCurrent().getSecurityContext().getUserName());

        this.getReserveBook_Popup().hide();
        return null;
    }

    public String resetSearchCB_action() {
        RichInputText searchFieldLocal = getSearchField();
        searchFieldLocal.resetValue();
        searchFieldLocal.setValue("");
        AdfFacesContext.getCurrentInstance().addPartialTarget(searchFieldLocal);
        return null;
    }

    public boolean isRefreshCond() {
        //System.out.println("------------------SearchBook.isRefreshCond------------------");
        refreshBooksTextualVO();
        return refreshCond;
    }

    public void setReserveBook_Popup(RichPopup p1) {
        this.reserveBook_Popup = p1;
    }

    public RichPopup getReserveBook_Popup() {
        return reserveBook_Popup;
    }

    public void setBookId(Number bookId) {
        this.bookId = bookId;
    }

    public Number getBookId() {
        return bookId;
    }

    public void setSearchField(RichInputText searchField) {
        this.searchField = searchField;
    }

    public RichInputText getSearchField() {
        return searchField;
    }

}
