package codeu.controller;

import codeu.model.store.basic.UserStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ProfileServletTest {
    private ProfileServlet profileServlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockRequestDispatcher;
    private HttpSession mockSession;

    @Before
    public void setup() throws IOException {

        profileServlet = new ProfileServlet();
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
                .thenReturn(mockRequestDispatcher);
    }

    @Test
    public void testdoGet() throws IOException, ServletException {
        profileServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost_NewUser() throws IOException, ServletException {
        Mockito.when(mockRequest.getParameter("description")).thenReturn("test description");
        HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        profileServlet.doPost(mockRequest, mockResponse);
        Mockito.verify(mockSession).setAttribute("description", "test description");
        Mockito.verify(mockResponse).sendRedirect("/profile");
    }
}

