package bookStore.ui.beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weblogic.security.SimpleCallbackHandler;
import weblogic.security.services.Authentication;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import java.io.IOException;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;

import oracle.adf.share.ADFContext;

import weblogic.servlet.security.ServletAuthentication;

public class LoginBean {
    private String _username;
    private String _password;

    public void setUsername(String _username) {
        this._username = _username;
    }

    public String getUsername() {
        return _username;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public String getPassword() {
        return _password;
    }

    public String doLogin() {
        System.out.println("------------------LoginBean.doLogin------------------");
        System.out.println("_username : " + _username);
        System.out.println("_password : " + _password);

        String un = _username;
        byte[] pw = _password.getBytes();
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
        CallbackHandler handler = new SimpleCallbackHandler(un, pw);
        try {
            Subject mySubject = Authentication.login(handler);
            ServletAuthentication.runAs(mySubject, request);
            ServletAuthentication.generateNewSessionID(request);
            String loginUrl = "/adfAuthentication?success_url=/faces";

            // if current page viewId is "/Login" than it means it is an explicit(When user directly hits the login page URL) authentication and so successPage will be displaed after authentication
            //but for implicit(when user hit URL to secure page and security container challange user for authentication) authentication this successUrl will be ignored because security mecanism
            //already knows the success page
            loginUrl += "/bookstore/ui/pages/AuthorizationBasedRedirector.jspx";

            System.out.println("loginUrl : " + loginUrl);

            HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
            sendForward(request, response, loginUrl);
        } catch (FailedLoginException fle) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect Username or Password", "An incorrect Username or Password" +
                                 " was specified");
            ctx.addMessage(null, msg);
        } catch (LoginException le) {
            reportUnexpectedLoginError("LoginException", le);
        }
        return null;
    }

    public String doLogout() {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url_ = ectx.getRequestContextPath() + "/faces/bookstore/ui/pages/Login.jspx";
        ServletAuthentication.logout((HttpServletRequest)ectx.getRequest());

        try {
            ectx.redirect(url_);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------------LoginBean.doLogout------------------");
        System.out.println("logout redirect url : " + url_);
        return null;
    }

    public String navigateToUserRegistration() {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        String url_ = ectx.getRequestContextPath() + "/faces/bookstore/ui/pages/UserRegistration.jspx?oplop=CI";

        try {
            ectx.redirect(url_);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("------------------LoginBean.navigateToUserRegistration------------------");
        System.out.println("url : " + url_);
        return null;
    }

    private void sendForward(HttpServletRequest request, HttpServletResponse response, String forwardUrl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        RequestDispatcher dispatcher = request.getRequestDispatcher(forwardUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException se) {
            reportUnexpectedLoginError("ServletException", se);
        } catch (IOException ie) {
            reportUnexpectedLoginError("IOException", ie);
        }
        ctx.responseComplete();
    }

    private void reportUnexpectedLoginError(String errType, Exception e) {
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unexpected error during login", "Unexpected error during login (" + errType +
                             "), please consult logs for detail");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        e.printStackTrace();
    }

}

