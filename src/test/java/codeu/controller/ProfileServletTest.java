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
    private HttpSession mockSession;
    private UserStore mockUserStore;
    private RequestDispatcher mockRequestDispatcher;

    @Before
    public void setup() throws IOException {

        profileServlet = new ProfileServlet();

        mockSession = Mockito.mock(HttpSession.class);
        mockRequest = Mockito.mock(HttpServletRequest.class);

        Mockito.when(mockRequest.getParameter("username")).thenReturn("test name");
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/users/test username");
        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockUserStore = Mockito.mock(UserStore.class);
        profileServlet.setUserStore(mockUserStore);

        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
                .thenReturn(mockRequestDispatcher);
    }

    @Test
    public void testdoGet() throws IOException, ServletException {
        profileServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }
    }

