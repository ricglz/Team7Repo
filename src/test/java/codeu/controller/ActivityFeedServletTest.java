package codeu.controller;

import codeu.model.store.basic.UserStore;
import codeu.model.data.User;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ActivityFeedServletTest {

    private ActivityFeedServlet activityFeedServlet;
    private HttpServletRequest mockRequest;
    private RequestDispatcher mockRequestDispatcher;
    private HttpSession mockSession;    
    private HttpServletResponse mockResponse;
    private UserStore mockUserStore;
    private User mockUser;

    @Before
    public void before() {
        activityFeedServlet = new ActivityFeedServlet();

        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);                
        mockSession = Mockito.mock(HttpSession.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockUserStore = Mockito.mock(UserStore.class);

        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/activityfeed.jsp"))
        .thenReturn(mockRequestDispatcher);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        activityFeedServlet.setUserStore(mockUserStore);
    }

    @ Test

    public void testDoGet() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
        mockUser = Mockito.mock(User.class);        
        Mockito.when(mockUserStore.getUser("test_username")).thenReturn(mockUser);

        activityFeedServlet.doGet(mockRequest, mockResponse);
        
        Mockito.verify(mockSession).getAttribute("user");
        Mockito.verify(mockUserStore).getUser("test_username");
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @ Test

    public void testDoGet_UserNotLoggedIn() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

        activityFeedServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login");
    }

    @ Test

    public void testDoGet_InvalidUser() throws IOException, ServletException {
        Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_username");
        Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);   
        
        activityFeedServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login");
    }
}