package bookStore.ui.beans;


import bookStore.ui.commons.ADFUtils;
import bookStore.ui.commons.BSPhaseListener;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import oracle.adf.share.ADFContext;
import oracle.adf.share.security.SecurityContext;


public class AuthorizationBasedRedirector extends BSPhaseListener {

    public static final String LOGIN_PAGE_URL =
        "/bookstore/ui/pages/Login.jspx";
    public static final Map<String, String> targetPageMap =
        new HashMap<String, String>();
    static {
        targetPageMap.put("BS_E_LIBRARIAN",
                          "/bookstore/ui/pages/LibrarianDashboard.jspx");
        targetPageMap.put("BS_E_STUDENT",
                          "/bookstore/ui/pages/StudentDashboard.jspx");
    }

    public void onPageLoad() {
        System.out.println("------------------AuthorizationBasedRedirector.onPageLoad------------------");

        String targetPageUrl = getTargetPageUrl();
        if (targetPageUrl == null) {
            // if not logged out than on page refresh user will keep getting above faces message
            ADFUtils.invokeEL("#{loginBean.doLogout}");
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        ViewHandler vh = ctx.getApplication().getViewHandler();
        UIViewRoot targetPageView = vh.createView(ctx, targetPageUrl);
        ctx.setViewRoot(targetPageView);
        ctx.renderResponse();
    }

    public String getTargetPageUrl() {
        SecurityContext sctx = ADFContext.getCurrent().getSecurityContext();

        System.out.println("isAuthenticated : " + sctx.isAuthenticated());
        System.out.println("getUserName : " + sctx.getUserName());

        if (sctx.isAuthenticated())
            for (String role : targetPageMap.keySet())
                if (sctx.isUserInRole(role))
                    return targetPageMap.get(role);

        FacesContext.getCurrentInstance().addMessage(null,
                                                     new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                      "Insufficient credentials to access application!",
                                                                      "Insufficient credentials to access application!"));
        return null;
    }
}
