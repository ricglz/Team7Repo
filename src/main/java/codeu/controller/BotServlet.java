package codeu.controller;


import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.data.Conversation;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentDataStoreException;
import codeu.bot.ActionMatcher;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Servlet class responsible for the BotChat page. */
public class BotServlet extends HttpServlet {


    public static final String DEFULT_BOT_CONVERSATION_TITLE="Bot-Conversation ";

    /**
     * Store class that gives access to Messages.
     */
    private MessageStore messageStore;

    /**
     * Store class that gives access to Users.
     */
    private UserStore userStore;

    /** Store class that gives access to Conversations. */
    private ConversationStore conversationStore;

    private ActionMatcher actionMatcher;

    /**
     * Set up state for handling chat requests.
     */

    @Override
    public void init() throws ServletException {
        super.init();
        setMessageStore(MessageStore.getInstance());
        userStore = UserStore.getInstance();
        conversationStore = ConversationStore.getInstance();
        actionMatcher = ActionMatcher.getInstance();
    }

    /**
     * Sets the MessageStore used by this servlet. This function provides a common setup method for
     * use by the test framework or the servlet's init() function.
     */
    void setMessageStore(MessageStore messageStore) {
        this.messageStore = messageStore;
    }
    /**
     * This function fires when a user navigates to the bot chat page. It gets the conversation title "bot-conversation" from
     * the URL, finds the corresponding Conversation, and fetches the messages in that Conversation.
     * It then forwards to bot chat.jsp for rendering.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String username = (String) request.getSession().getAttribute("user");
        if (username == null) {
            //redirect to login
            response.sendRedirect("/login");
            return;
        }
        User user = userStore.getUser(username);
        if (user == null) {
            // couldn't find author, redirect to loging Page
            response.sendRedirect("/login");
            return;
        }

        Conversation botConversation = conversationStore.getBotConversation(user);
        if (botConversation == null){
            botConversation = new Conversation(UUID.randomUUID(), user.getId(), DEFULT_BOT_CONVERSATION_TITLE+username, Instant.now());
            conversationStore.addConversation(botConversation);
        }
        System.out.println("get"+botConversation);
        List<Message> messages = messageStore.getMessagesInConversation(botConversation.getId());
        request.setAttribute("botmessage", messages);
        request.getRequestDispatcher("/WEB-INF/view/bot.jsp").forward(request, response);
    }

    /**
     * This function fires when a user submits the form on the bot chat page. It gets the logged-in
     * username from the session,  and the chat message from the
     * submitted form data. It creates a new Message from that data, adds it to the model, and then
     * redirects back to the chat page.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String username = (String) request.getSession().getAttribute("user");
        if (username == null) {
            response.sendRedirect("/login");
            return;
        }
        User user = userStore.getUser(username);
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        String userInput = request.getParameter("botmessage");

        Conversation botConversation = conversationStore.getBotConversation(user);
        if (botConversation == null){
            botConversation = new Conversation(user.getId(), user.getId(), DEFULT_BOT_CONVERSATION_TITLE +username, Instant.now());
            conversationStore.addConversation(botConversation);
        }
        System.out.println("post"+botConversation);
        // sanitizes
        // This removes any HTML from the description content
        String cleanedMessageContent = Jsoup.clean(userInput, Whitelist.none());
        Message message = new Message(UUID.randomUUID(),botConversation.getId(), user.getId(),cleanedMessageContent, Instant.now());
        messageStore.addMessage(message);

        try {
            // TODO Check why here is nullpointer in the test
            actionMatcher.matchAction(cleanedMessageContent, username, response);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Message> messages = messageStore.getMessagesInConversation(botConversation.getId());
        response.sendRedirect("/bot");
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public ConversationStore getConversationStore() {
        return conversationStore;
    }

    public void setConversationStore(ConversationStore conversationStore) {
        this.conversationStore = conversationStore;
    }
}