package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;


public class ProfileServlet extends HttpServlet  {

    /**
     * Store class that gives access to Users.
     */
    private UserStore userStore;

    @Override
    public void init() throws ServletException {
        super.init();
        setUserStore(UserStore.getInstance());
    }

    /**
     * Sets the UserStore used by this servlet. This function provides a common setup method for use
     * by the test framework or the servlet's init() function.
     */
    void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    /**
     * This function fires when a user navigates to the profile page. It gets all of the
     * profile from the model and forwards to profile.jsp for rendering the list.
     */

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //Checks whether user is logged in or not
        String userName = (String) request.getSession().getAttribute("user");
        if (userName == null) {
            response.sendRedirect("/login");
            return;
        }
        User loggedInUser = userStore.getUser(userName);
        //Checks whether user is loggedin or not
        if (loggedInUser == null) {
            response.sendRedirect("/login");
            return;
        }
        request.getSession().setAttribute("description", loggedInUser.getDescription());
        request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
    }



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String description = request.getParameter("description");
        String userName = (String) request.getSession().getAttribute("user");
        if (userName == null){
            response.sendRedirect("/login");
            return;
        }
        //Some Error checking in this code
        // read user from database using username
        // get all the information of that loggedin user from datastore
        User userToUpdate = this.userStore.getUser(userName);
        //Setting Description of that Particular User in datastore
        if (userToUpdate == null){
            response.sendRedirect("/login");
            return;
        }

        // this removes any HTML from the description content
        String cleanedMessageContent = Jsoup.clean(description, Whitelist.none());
        userToUpdate.setDescription(cleanedMessageContent);
        userStore.updateUser(userToUpdate);
        request.getSession().setAttribute("description", cleanedMessageContent);
        response.sendRedirect("/profile");
    }
}
