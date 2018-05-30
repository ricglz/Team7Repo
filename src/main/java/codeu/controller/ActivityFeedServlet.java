package codeu.controller;

import codeu.model.store.basic.UserStore;
import codeu.model.data.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for the activity feed page. */
public class ActivityFeedServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /**
   * Set up state for handling activity feed-related requests. This method is only called when running in a
   * server, not when running in a test.
   */
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

  // Forwards the request to activityfeed.jsp.
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    String username = (String) request.getSession().getAttribute("user");
    if (username == null) {
      // user is not logged in, don't let them access the activity feed
      response.sendRedirect("/login");
      return;
    }

    User user = userStore.getUser(username);
    if (user == null) {
      // user is not logged in, don't let them access the activity feed
      response.sendRedirect("/login");
      return;
    }
    
    request.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp").forward(request, response);
  }

  // Right now there are no submissions for the activity feed page, so this does nothing.
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
  }
}
