package bookStore.model.module.common;

import oracle.jbo.ApplicationModule;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon Jul 09 13:42:50 IST 2018
// ---------------------------------------------------------------------
public interface LibrarianChoresAM extends ApplicationModule {
    void applyQueryRecordByStatusVOCriteria(String b_Status);

    void applySubmitBookByStatusVOCriteria(String b_recordType);
}
