package bookStore.ui.beans;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.layout.RichDeck;

import org.apache.myfaces.trinidad.context.RequestContext;


public class LibDashboard {
    private RichDeck deck;

    public LibDashboard() {
    }

    public void setDeck(RichDeck deck) {
        this.deck = deck;
    }

    public RichDeck getDeck() {
        return deck;
    }

    public void submitBookActionListener(ActionEvent actionEvent) {
        System.out.println("------------------LibDashboard.submitBookActionListener------------------");

        getDeck().setDisplayedChild("submitBookPSL");
        RequestContext.getCurrentInstance().addPartialTarget(getDeck());
    }

    public void queriesActionListener(ActionEvent actionEvent) {
        System.out.println("------------------LibDashboard.queriesActionListener------------------");

        getDeck().setDisplayedChild("queriesPSL");
        RequestContext.getCurrentInstance().addPartialTarget(getDeck());
    }

    public void dashboardActionListener(ActionEvent actionEvent) {
        System.out.println("------------------LibDashboard.dashboardActionListener------------------");

        getDeck().setDisplayedChild("dashboardPSL");
        RequestContext.getCurrentInstance().addPartialTarget(getDeck());
    }
}
