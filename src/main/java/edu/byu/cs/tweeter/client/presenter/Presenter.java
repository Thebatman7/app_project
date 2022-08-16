package edu.byu.cs.tweeter.client.presenter;

//VIEWTYPE is generic type
public abstract class Presenter<VIEWTYPE extends Presenter.View> {
    //All fo the presenters have a view View

    private VIEWTYPE view;

    /*
    Because we need a bidirectional communication between view and presenter but presenter can't have
    dependencies on layers above we must create this interface view.
    */
    public interface View {
        //When a message other than error message needs to be displayed in the view
        void displayInfoMessage(String message);
        //When something goes wrong, the view must display these messages
        void displayErrorMessage(String message);
        //When we need to clear messages
        void clearInfoMessage();
    }

    public Presenter(VIEWTYPE view){
        this.view = view;
    }

    public VIEWTYPE getView() {
        return view;
    }

}
