package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;
import bookStore.ui.commons.Utils;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;


public class UpdateStudent {

    //update password form fields
    private String oldPassword, newPassword, confirmPassword;

    public UpdateStudent() {
    }

    public String updateAction() {
        System.out.println("------------------UpdateStudent.updateAction------------------");

        try {
            OperationBinding op = ADFUtils.executeOperationBinding("Commit");
            if (op.getErrors() != null && !op.getErrors().isEmpty()) {
                String errMsg = "Commit has errors! errors[Commit] : " + op.getErrors();
                ADFUtils.addFacesMessageInfo(errMsg);

                System.out.println(errMsg);
            } else
                ADFUtils.addFacesMessageInfo("Profile Updated!");
        } catch (Exception e) {
            OperationBinding op = ADFUtils.executeOperationBinding("Rollback");
            ADFUtils.addFacesMessageError("Update Failed! errors[Commit] : " + e.getMessage());
            if (op.getErrors() != null) {
                String errMsg = "Rollback Failed! errors[Rollback] : " + op.getErrors();
                ADFUtils.addFacesMessageError(errMsg);

                System.out.println(errMsg);
            }
        }
        return null;
    }

    public void setOldPassword(String oldPassword) {
        System.out.println("------------------UpdateStudent.setOldPassword[" + oldPassword + "]------------------");
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        System.out.println("------------------UpdateStudent.setNewPassword[" + newPassword + "]------------------");
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        System.out.println("------------------UpdateStudent.setConfirmPassword[" + confirmPassword + "]------------------");
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    private String getCurrentPassword() {
        return (String)ADFUtils.evaluateEL("#{bindings.UPassword.inputValue}");
    }

    private void updateCurrentPassword(String newPassword) {
        ADFUtils.setEL("#{bindings.UPassword.inputValue}", newPassword);
    }

    public void updatePasswordDialogListener(DialogEvent dialogEvent) {
        System.out.println("------------------UpdateStudent.updatePasswordDialogListener------------------");
        System.out.println("oldPassword : " + getOldPassword());
        System.out.println("newPassword : " + getNewPassword());
        System.out.println("confirmPassword : " + getConfirmPassword());

        String currentPassword = getCurrentPassword();
        if (Utils.isNullOrEmpty(currentPassword)) {
            ADFUtils.addFacesMessageError("Existing password[" + currentPassword + "] violates password criteria or not able to fetch it!");
            return;
        }

        System.out.println("currentPassword : " + currentPassword);

        if (!currentPassword.equals(getOldPassword())) {
            ADFUtils.addFacesMessageError("Old Password didn't match the existing password!");
            return;
        }

        if (currentPassword.equals(getNewPassword())) {
            ADFUtils.addFacesMessageError("New Password should not be same as existing password!");
            return;
        }

        if (!Utils.doesComplyPasswordCriteria(getNewPassword())) {
            ADFUtils.addFacesMessageError("New password doesn't comply with password criteria!" +
                                          "Password Criteria :: Not null, Not Empty, Length atleast 8, Contains atleast one lower case, Contains atleast one upper case, Contains atleast one digit," +
                                          "Contains atleast one special(@#;%) character");
            return;
        }

        if (!getNewPassword().equals(getConfirmPassword())) {
            ADFUtils.addFacesMessageError("New password doesn't match confirm password");
            return;
        }

        try {
            updateCurrentPassword(getNewPassword());
            OperationBinding op = ADFUtils.executeOperationBinding("Commit");
            if (op.getErrors() != null && !op.getErrors().isEmpty()) {
                ADFUtils.executeOperationBinding("Rollback");
                String errMsg = "Commit Failed! errors[Commit] : " + op.getErrors();
                ADFUtils.addFacesMessageError(errMsg);

                System.out.println(errMsg);
                return;
            }
            ADFUtils.addFacesMessageInfo("Password Updated!");
        } catch (Exception e) {
            e.printStackTrace();
            ADFUtils.executeOperationBinding("Rollback");
            String errMsg = "Password Update Failed! errors : " + e.getMessage();
            ADFUtils.addFacesMessageError(errMsg);
        }
    }
}
