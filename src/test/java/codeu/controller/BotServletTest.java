package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BotServletTest {
    private BotServlet botServlet;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private HttpSession mockSession;
    private RequestDispatcher mockRequestDispatcher;
    private UserStore mockUserStore;
    private ConversationStore mockConversationStore;
    private MessageStore mockMessageStore;


    @Before
    public void setup() throws IOException
    {
        botServlet = new BotServlet();
        mockRequest = Mockito.mock(HttpServletRequest.class);

        mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/bot.jsp"))
                .thenReturn(mockRequestDispatcher);

        mockConversationStore = Mockito.mock(ConversationStore.class);
        botServlet.setConversationStore(mockConversationStore);

        mockMessageStore = Mockito.mock(MessageStore.class);
        botServlet.setMessageStore(mockMessageStore);

        mockUserStore = Mockito.mock(UserStore.class);
        botServlet.setUserStore(mockUserStore);
    }

    @Test
    public void testDoGet() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn("user_name");
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
        User user =
                new User(
                        UUID.randomUUID(),
                        "test_user",
                        "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
                        Instant.now());
        UserStore mockUserStore = Mockito.mock(UserStore.class);
        botServlet.setUserStore(mockUserStore);
        botServlet.setMessageStore(mockMessageStore);
        Mockito.when(mockUserStore.getUser("user_name")).thenReturn(user);
        UUID fakeConversationId = UUID.randomUUID();
        Conversation fakeConversation =
                new Conversation(fakeConversationId, user.getId(), "test_conversation", Instant.now());

        mockConversationStore.addConversation(fakeConversation);
        Mockito.when(mockConversationStore.getBotConversation(user)).thenReturn(fakeConversation);
        List<Message> fakeMessageList = new ArrayList<>();
        Message fakeMessage = new Message(
                UUID.randomUUID(),
                fakeConversationId,
                user.getId(),
                "test message",
                Instant.now());
        fakeMessageList.add(fakeMessage);
        mockMessageStore.addMessage(fakeMessage);
        Mockito.when(mockMessageStore.getMessagesInConversation(fakeConversationId)).thenReturn(fakeMessageList);

        botServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockRequest).setAttribute("botmessage", fakeMessageList);
        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }


    @Test
    public void testDoGet_UserNotLoggedIn() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

        botServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login");
    }
    @Test
    public void testDoPost_UserNotLoggedIn() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

        botServlet.doPost(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login");
    }

    @Test
    public void testDoPost_StoresMessage() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
        User fakeUser =
                new User(
                        UUID.randomUUID(),
                        "test_username",
                        "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
                        Instant.now());

        Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

        Conversation fakeConversation =
                new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());
        Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
                .thenReturn(fakeConversation);
        Mockito.when(mockRequest.getParameter("botmessage")).thenReturn("Test message.");

        botServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(mockMessageStore).addMessage(messageArgumentCaptor.capture());

        Assert.assertEquals("Test message.", messageArgumentCaptor.getValue().getContent());
        Mockito.verify(mockResponse).sendRedirect("/bot");
        }

    @Test
    public void testDoPost_SanitizeMessage() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn("test_username");
        User fakeUser =
                new User(
                        UUID.randomUUID(),
                        "test_username",
                        "$2a$10$bBiLUAVmUFK6Iwg5rmpBUOIBW6rIMhU1eKfi3KR60V9UXaYTwPfHy",
                        Instant.now());

        Mockito.when(mockUserStore.getUser("test_username")).thenReturn(fakeUser);

        Conversation fakeConversation =
                new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());
        Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
                .thenReturn(fakeConversation);
        Mockito.when(mockRequest.getParameter("botmessage")).thenReturn("Contains <b>html</b> and <script>JavaScript</script> content.");

        botServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(mockMessageStore).addMessage(messageArgumentCaptor.capture());


        Assert.assertEquals("Contains html and  content.", messageArgumentCaptor.getValue().getContent());
        Mockito.verify(mockResponse).sendRedirect("/bot");
    }
        }




