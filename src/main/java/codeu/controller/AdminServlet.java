package codeu.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminServlet extends HttpServlet {

  /**
   * Set up state for handling registration-related requests. This method is only called when
   * running in a server, not when running in a test.
   */
  @Override
  public void init() throws ServletException {
    super.init();
  }

  /** 
   * Makes that the actual page is the admin.jsp .
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
        request.getRequestDispatcher("/WEB-INF/view/admin.jsp").forward(request, response);
  }
  /**
   * It allows to import the data of a play or any text.
  */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
  }
}
