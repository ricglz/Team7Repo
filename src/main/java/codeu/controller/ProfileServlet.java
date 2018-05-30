package codeu.controller;

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {

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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String userPage = requestUrl.substring("/users/".length());
        request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String username = (String) request.getSession().getAttribute("user");
        if (username == null) {
            // user is not logged in, don't let them create a conversation
            response.sendRedirect("/login");
            return;
        }
        User user = userStore.getUser(username);
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }
        String description = request.getParameter("description");
        request.getSession().setAttribute("description", username);
        String requestUrl = request.getRequestURI();
        String userPage = requestUrl.substring("/users/".length());
        response.sendRedirect("/users/" + username);
    }
}
