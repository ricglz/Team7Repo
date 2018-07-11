package codeu.controller;

import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.ActivityStore;
import codeu.model.data.User;
import codeu.model.data.Activity;

import java.io.IOException;
import java.util.UUID;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import codeu.bot.ActionMatcher;
import codeu.bot.BotActions.Action;

public class ActionMatcherTest {

    private ActionMatcher actionMatcher;
    private ActivityServlet activityServlet;
    private HttpServletRequest mockRequest;
    private RequestDispatcher mockRequestDispatcher;
    private HttpSession mockSession;    
    private HttpServletResponse mockResponse;
    private UserStore mockUserStore;
    private User mockUser;

    @Before
    public void before() {
        actionMatcher = new ActionMatcher();

        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);                
        mockSession = Mockito.mock(HttpSession.class);
    
        mockResponse = Mockito.mock(HttpServletResponse.class);

        mockUserStore = Mockito.mock(UserStore.class);
        activityServlet.setUserStore(mockUserStore);

        mockActivityStore = Mockito.mock(ActivityStore.class);
        activityServlet.setActivityStore(mockActivityStore);
    }

    @Test
    public void testDoGet() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
        mockUser = Mockito.mock(User.class);        
        Mockito.when(mockUserStore.getUser("test_username")).thenReturn(mockUser);

        List<Activity> fakeActivityList = new ArrayList<>();
        Mockito.when(mockActivityStore.getAllSortedActivities()).thenReturn(fakeActivityList);

        activityServlet.doGet(mockRequest, mockResponse);
        
        Mockito.verify(mockSession).getAttribute("user");
        Mockito.verify(mockUserStore).getUser("test_username");
        Mockito.verify(mockActivityStore).getAllSortedActivities();
        Mockito.verify(mockRequest).setAttribute("activities", fakeActivityList);    
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoGet_UserNotLoggedIn() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

        activityServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login");
    }

    @Test
    public void testDoGet_InvalidUser() throws IOException, ServletException {
        Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test_username");
        Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);   
        
        activityServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login");
    }
}