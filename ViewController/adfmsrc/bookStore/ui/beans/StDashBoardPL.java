package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import oracle.adf.model.BindingContext;
import oracle.adf.model.RegionContext;
import oracle.adf.model.RegionController;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

// Inactive Using method call in TF to achive same results, as this is called too many times unnesscarily here
public class StDashBoardPL implements RegionController {

    public void onPageLoad() {
        System.out.println("------------------StDashBoardPL.onPageLoad------------------");

        BindingContainer bc =
            BindingContext.getCurrent().getCurrentBindingsEntry();
        //Learnig: this can be done without code by creating method call for "setBooksReservedVOBindVar" in StDashboardTF and make a control flow case from method to StDashboard.jspx
        OperationBinding op =
            bc.getOperationBinding("setBooksReservedVOBindVar");
        Map params = op.getParamsMap();
        params.put("b_UName",
                   ADFUtils.invokeEL("#{stDashboard.getCurrentUser}"));

        System.out.println("params.size : " + params.size());
        for (Object key : params.keySet()) {
            System.out.println("key : " + key + " - value : " +
                               params.get(key));
        }

        op.execute();
    }

    public boolean refreshRegion(RegionContext regionContext) {
        int refreshFlag = regionContext.getRefreshFlag();
        FacesContext fctx = FacesContext.getCurrentInstance();
        //check internal request parameter
        Map requestMap = fctx.getExternalContext().getRequestMap();
        PhaseId currentPhase =
            (PhaseId)requestMap.get("oracle.adfinternal.view.faces.lifecycle.CURRENT_PHASE_ID");

        System.out.println("------------------StDashBoardPL.refreshRegion[currentPhase : " +
                           currentPhase + " - ordinal : " +
                           currentPhase.getOrdinal() + "]------------------");

        if (currentPhase.getOrdinal() ==
            PhaseId.RENDER_RESPONSE.getOrdinal()) {
            onPageLoad();
            regionContext.getRegionBinding().refresh(refreshFlag);
        }
        return false;
    }

    public boolean validateRegion(RegionContext regionContext) {
        regionContext.getRegionBinding().validate();
        return false;
    }

    public boolean isRegionViewable(RegionContext regionContext) {
        return regionContext.getRegionBinding().isViewable();
    }

    public String getName() {
        return null;
    }
}
