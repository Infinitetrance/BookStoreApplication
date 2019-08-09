package bookStore.ui.beans;


import bookStore.model.module.UserRegistrationAMImpl;

import bookStore.ui.commons.ADFUtils;
import bookStore.ui.commons.BSPhaseListener;
import bookStore.ui.commons.Utils;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.client.Configuration;
import oracle.jbo.domain.DBSequence;


public class UserRegistrationBean extends BSPhaseListener {
    private static final List USER_ATTR_NAMES = new ArrayList();
    static {
        USER_ATTR_NAMES.add("UName");
        USER_ATTR_NAMES.add("UPassword");
        USER_ATTR_NAMES.add("UDescription");
        USER_ATTR_NAMES.add("UserId");
        USER_ATTR_NAMES.add("FirstName");
        USER_ATTR_NAMES.add("LastName");
        USER_ATTR_NAMES.add("Gender");
        USER_ATTR_NAMES.add("Email");
        USER_ATTR_NAMES.add("UserTypeCode");
        USER_ATTR_NAMES.add("AddressStreet");
        USER_ATTR_NAMES.add("AddressCity");
        USER_ATTR_NAMES.add("AddressState");
        USER_ATTR_NAMES.add("AddressPostalCode");
        USER_ATTR_NAMES.add("AddressCountryCode");
        USER_ATTR_NAMES.add("MobilePhoneNumber");
    }

    public String registerUser() {
        if (!validatePassword()) {
            ADFUtils.executeOperationBinding("Rollback");
            return null;
        }

        List<Object> attrValues = new ArrayList<Object>();

        //Learning: instead of accessing all fields from binding layer, for this perticular scenario it could have been better if
        //could have bind data members of this bean to fields in jspx page
        attrValues.add(ADFUtils.evaluateEL("#{bindings.UName.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.UPassword.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.UDescription.inputValue}"));
        // UserId is set null as it will be replaced by DBSequnece at AM
        attrValues.add(null);
        attrValues.add(ADFUtils.evaluateEL("#{bindings.FirstName.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.LastName.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.Gender.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.Email.inputValue}"));
        // We would register only students from registration mecanism
        //attrValues.add(ADFUtils.evaluateEL("#{bindings.UserTypeCode.inputValue}"));
        attrValues.add("BS_E_STUDENT");
        attrValues.add(ADFUtils.evaluateEL("#{bindings.AddressStreet.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.AddressCity.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.AddressState.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.AddressPostalCode.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.AddressCountryCode.inputValue}"));
        attrValues.add(ADFUtils.evaluateEL("#{bindings.MobilePhoneNumber.inputValue}"));

        ADFContext oldContext = ADFContext.initADFContext(null, null, null, null);
        FacesContext fctx = FacesContext.getCurrentInstance();
        Integer userId = 0;
        String errorMsg = null;
        try {
            //Learning do not create AM instance in bean try accessing it through binding layer, that way jspx page and bean
            //will have same AM instance and thus same transation context
            UserRegistrationAMImpl am =
                (UserRegistrationAMImpl)Configuration.createRootApplicationModule("bookStore.model.module.UserRegistrationAM",
                                                                                  "UserRegistrationAMLocal");

            System.out.println("------------------UserRegistrationBean.registerUser------------------");
            System.out.println("attrValues : " + attrValues);

            // We would register only students from registration mecanism
            userId = am.registerUser(USER_ATTR_NAMES, attrValues, "BS_E_STUDENT");
            ADFUtils.setEL("#{bindings.UserId.inputValue}", new DBSequence(userId));

            System.out.println("Status : " + userId);

            Configuration.releaseRootApplicationModule(am, true);
        } catch (Exception e) {
            //errorMsg =~ "JBO-26048: Constraint "PK_USERS" is violated" when UName is already taken
            errorMsg = e.getMessage();
            errorMsg =
                    errorMsg.contains("JBO-26048") && errorMsg.contains("PK_USERS") ? "UName is already taken, try different Uname" : errorMsg;
            e.printStackTrace();
        } finally {
            ADFContext.resetADFContext(oldContext);
        }

        String errMsgAndDetail;
        if (userId != 0 && errorMsg == null) {
            errMsgAndDetail = "Registration successful, login now";
            fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, errMsgAndDetail, errMsgAndDetail));
            //return "signin";
            return navigateToLogin();
        } else {
            errMsgAndDetail = String.format("Registration failed! \nuserId : %d, errorMsg : %s", userId, errorMsg);
            fctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsgAndDetail, errMsgAndDetail));
            return null;
        }
    }

    public boolean validatePassword() {
        String password, confirmPassword;
        password = (String)ADFUtils.evaluateEL("#{bindings.UPassword.inputValue}");
        confirmPassword = (String)ADFUtils.evaluateEL("#{bindings.UPasswordConfirm.inputValue}");
        if (!Utils.doesComplyPasswordCriteria(password)) {
            ADFUtils.addFacesMessageError("New password doesn't comply with password criteria!" +
                                          "Password Criteria :: Not null, Not Empty, Length atleast 8, Contains atleast one lower case, Contains atleast one upper case, Contains atleast one digit," +
                                          "Contains atleast one special(@#;%) character");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            ADFUtils.addFacesMessageError("Password doesn't match confirm password");
            return false;
        }
        return true;
    }

    public String navigateToLogin() {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url_ = ectx.getRequestContextPath() + "/faces/bookstore/ui/pages/Login.jspx";

        try {
            ectx.redirect(url_);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------------UserRegistrationBean.navigateToLogin------------------");
        System.out.println("url : " + url_);
        return null;
    }

    @Override
    public void onPageLoad() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String queryString = request.getQueryString();
        System.out.println("queryString : " + queryString);
        if (queryString.contains("oplop=CI")) {
            ADFUtils.executeOperationBinding("Create");
            System.out.println("executeOperationBinding(Create)");
        }
        System.out.println("------------------UserRegistrationBean.onPageLoad------------------");
    }

    //Password
    private String input1;
    //Confirm Password
    private String input2;
    private boolean input1Set;

    public void validatePassword_DEPRECATED(FacesContext context, UIComponent component, Object value) {
        if (input1Set) {
            input2 = (String)value;
            if (input1 == null || input1.length() < 6 || (!input1.equals(input2))) {
                ((EditableValueHolder)component).setValid(false);
                context.addMessage(component.getClientId(context),
                                   new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password must be atleast 6 characters long & both fields identical",
                                                    "Password must be atleast 6 characters long & both fields identical"));
            }
        } else {
            input1Set = true;
            input1 = (String)value;
        }
        System.out.println("------------------UserRegistrationBean.validatePassword------------------");
    }

    public void registerUserDialogListener(DialogEvent dialogEvent) {
        System.out.println("------------------UserRegistrationBean.registerUserDialogListener------------------");

        if (!dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
            System.out.println("DialogEvent.Outcome.NOT_OK execute[Rollback]");
            ADFUtils.executeOperationBinding("Rollback");
            return;
        }

        if (!validatePassword()) {
            System.out.println("NOT of validatePassword return");
            return;
        }

        try {
            ADFUtils.setEL("#{bindings.UserId.inputValue}", ADFUtils.invokeEL("#{bindings.getNextDBSequence.execute}"));
            ADFUtils.setEL("#{bindings.GMember.inputValue}", ADFUtils.evaluateEL("#{bindings.UName.inputValue}"));
            //For now we would register only students from registration mecanism
            ADFUtils.setEL("#{bindings.GName.inputValue}", "BS_E_STUDENT");
            ADFUtils.executeOperationBinding("Commit");
        } catch (Throwable e) {
            //errorMsg =~ "JBO-26048: Constraint "PK_USERS" is violated" when UName is already taken
            String errorMsg = e.getMessage();
            errorMsg =
                    errorMsg.contains("JBO-26048") && errorMsg.contains("PK_USERS") ? "UName is already taken, try different UName" : errorMsg;
            ADFUtils.addFacesMessageError("Registration Failed! error : " + errorMsg);
            e.printStackTrace();
        }
    }

    public void registerUserPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        System.out.println("------------------UserRegistrationBean.registerUserPopupFetchListener------------------");
        ADFUtils.executeOperationBinding("CreateInsertUser");
        ADFUtils.executeOperationBinding("CreateInsertGroupMember");
    }

    public void registerUserPopupCancelListener(PopupCanceledEvent popupCanceledEvent) {
        System.out.println("------------------UserRegistrationBean.registerUserPopupCancelListener------------------");
        ADFUtils.executeOperationBinding("Rollback");
    }
}
