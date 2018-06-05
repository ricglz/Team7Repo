package codeu.controller;

import codeu.model.data.User;
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
import java.time.Instant;
import java.util.UUID;

public class ProfileServletTest {
    private ProfileServlet profileServlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockRequestDispatcher;
    private HttpSession mockSession;
    private UserStore mockUserStore;
    private User fakeUser;
    private final String TEST_USERNAME = "test_username";

    @Before
    public void setup() throws IOException {

        profileServlet = new ProfileServlet();
        mockRequest = Mockito.mock(HttpServletRequest.class);

        mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        setupFakeUser();

        mockUserStore = Mockito.mock(UserStore.class);
        profileServlet.setUserStore(mockUserStore);


        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);

        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
                .thenReturn(mockRequestDispatcher);


    }

    //Creating fakeUser
    private void setupFakeUser() {
        fakeUser =
                new User(
                        UUID.randomUUID(),
                        "test username",
                        "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",Instant.now(),0,false,
                       "test description");
    }

    @Test
    public void testdoGet() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(TEST_USERNAME);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        Mockito.when(mockUserStore.getUser(TEST_USERNAME)).thenReturn(fakeUser);

        profileServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockSession).setAttribute("description", "test description");
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost() throws IOException, ServletException {
        Mockito.when(mockRequest.getParameter("description")).thenReturn("updated test description");
        Mockito.when(mockSession.getAttribute("user")).thenReturn(TEST_USERNAME);
        Mockito.when(mockUserStore.getUser(TEST_USERNAME)).thenReturn(fakeUser);
        profileServlet.doPost(mockRequest, mockResponse);
        assert (mockUserStore.getUser(TEST_USERNAME).getDescription().equals("updated test description"));
        Mockito.verify(mockSession).setAttribute("description", "updated test description");
        Mockito.verify(mockResponse).sendRedirect("/profile");
    }
    @Test
    public void testDoPost_Sanitation()throws IOException, ServletException{
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        Mockito.when(mockRequest.getParameter("description")).thenReturn("Contains <b>html</b> and <script>JavaScript</script> content.");
        Mockito.when(mockSession.getAttribute("user")).thenReturn(TEST_USERNAME);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        Mockito.when(mockUserStore.getUser(TEST_USERNAME)).thenReturn(fakeUser);
        profileServlet.doPost(mockRequest, mockResponse);
        assert (mockUserStore.getUser(TEST_USERNAME).getDescription().equals("Contains html and  content."));
        Mockito.verify(mockSession).setAttribute("description", "Contains html and  content.");
        Mockito.verify(mockResponse).sendRedirect("/profile");
    }
}

