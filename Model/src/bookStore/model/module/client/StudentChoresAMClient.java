package bookStore.model.module.client;

import bookStore.model.module.common.StudentChoresAM;

import oracle.jbo.client.remote.ApplicationModuleImpl;
import oracle.jbo.domain.Number;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Jun 30 15:33:42 IST 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class StudentChoresAMClient extends ApplicationModuleImpl implements StudentChoresAM {
    /**
     * This is the default constructor (do not remove).
     */
    public StudentChoresAMClient() {
    }


    public void applyVCForBooksTextualVO(String bindVariableValue) {
        Object _ret =
            this.riInvokeExportedMethod(this,"applyVCForBooksTextualVO",new String [] {"java.lang.String"},new Object[] {bindVariableValue});
        return;
    }

    public void applyVCForUsersVO(String bindVariableValue) {
        Object _ret =
            this.riInvokeExportedMethod(this,"applyVCForUsersVO",new String [] {"java.lang.String"},new Object[] {bindVariableValue});
        return;
    }

    public void setBooksReservedVOBindVar(String b_UName) {
        Object _ret =
            this.riInvokeExportedMethod(this,"setBooksReservedVOBindVar",new String [] {"java.lang.String"},new Object[] {b_UName});
        return;
    }

    public void applyVCForQueryRecordVO(String b_UName) {
        Object _ret =
            this.riInvokeExportedMethod(this,"applyVCForQueryRecordVO",new String [] {"java.lang.String"},new Object[] {b_UName});
        return;
    }
}