package bookStore.model.module.common;

import oracle.jbo.ApplicationModule;
import oracle.jbo.domain.Number;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Jun 30 15:33:41 IST 2018
// ---------------------------------------------------------------------
public interface StudentChoresAM extends ApplicationModule {
    void applyVCForBooksTextualVO(String bindVariableValue);


    void applyVCForUsersVO(String bindVariableValue);

    void setBooksReservedVOBindVar(String b_UName);

    void applyVCForQueryRecordVO(String b_UName);
}