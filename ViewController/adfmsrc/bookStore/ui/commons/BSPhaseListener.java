package bookStore.ui.commons;

import oracle.adf.controller.faces.context.FacesPageLifecycleContext;
import oracle.adf.controller.v2.lifecycle.Lifecycle;
import oracle.adf.controller.v2.lifecycle.PagePhaseEvent;
import oracle.adf.controller.v2.lifecycle.PagePhaseListener;
import oracle.adf.share.ADFContext;

//Book Store phase listener
public class BSPhaseListener implements PagePhaseListener {

    public void afterPhase(PagePhaseEvent pagePhaseEvent) {
    }

    public void beforePhase(PagePhaseEvent pagePhaseEvent) {
        FacesPageLifecycleContext ctx =
            (FacesPageLifecycleContext)pagePhaseEvent.getLifecycleContext();
        if (pagePhaseEvent.getPhaseId() == Lifecycle.PREPARE_MODEL_ID) {
            onPageLoad();
        }
    }

    public void onPageLoad() {
        System.out.println("------------------BSPhaseListener.onPageLoad------------------");

        //temprory for debugging of UserRegistration.jspx
        System.out.println("isAuthenticated : " +
                           ADFContext.getCurrent().getSecurityContext().isAuthenticated());
        System.out.println("getUserName : " +
                           ADFContext.getCurrent().getSecurityContext().getUserName());
    }
}
