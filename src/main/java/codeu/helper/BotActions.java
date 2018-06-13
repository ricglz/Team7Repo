package codeu.helper;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

import com.google.common.collect.Lists;

/** Helper class responsible of the bot's actions */
public class BotActions {

    private ActivityStore activityStore;
    private User user;
    private ConversationStore conversationStore;
    private HttpServletResponse response;
    private MessageStore messageStore;
    private UserStore userStore;

    public BotActions(String username) {
        setActivityStore(activityStore);
        setConversationStore(conversationStore);
        setMessageStore(messageStore);
        setUserStore(userStore);
        user = userStore.getUser(username);
    }

	/**
	 * @param activityStore the activityStore to set
	 */
	public void setActivityStore(ActivityStore activityStore) {
		this.activityStore = activityStore;
    }
    
    /**
     * @param conversationStore the conversationStore to set
     */
    public void setConversationStore(ConversationStore conversationStore) {
        this.conversationStore = conversationStore;
    }

    /**
     * @param messageStore the messageStore to set
     */
    public void setMessageStore(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    /**
     * @param userStore the userStore to set
     */
    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    public void setDescription(String description) {
        user.setDescription(description);
        userStore.updateUser(user);
    }

    private boolean validLocation(String location) {
        List<String> valid_locations = Lists.newArrayList("/register", "/login", "/about.jsp", "/activity",
                                                        "/admin", "/profile", "/conversations");
        return valid_locations.contains(location);
    }

    public void goTo(String location) throws IOException {
        if(!location.contains("/")){
            location = "/" + location;
        }
        if (validLocation(location)) {
            response.sendRedirect(location);   
        }
    }

    public void createConversation(String title) throws IOException {
        UUID owner = user.getId();
        Conversation conversation = new Conversation(UUID.randomUUID(), owner, title, Instant.now());
        conversationStore.addConversation(conversation);
        response.sendRedirect("/conversations/" + title);
    }

    public void sendMessage(String content, String title) throws IOException {
        Conversation conversation = conversationStore.getConversationWithTitle(title);
        UUID id = conversation.getId();
        UUID author = user.getId();
        Message message = new Message(UUID.randomUUID(), id, author, content, Instant.now());
        messageStore.addMessage(message);
        response.sendRedirect("/conversations/" + title);
    }

    public List<Message> getMessages(Instant time) {
        List<Message> messages = messageStore.getMessagesInTime(time);
        return messages;
    }

    public List<Message> getMessages(String title) {
        Conversation conversation = conversationStore.getConversationWithTitle(title);
        UUID id = conversation.getId();
        List<Message> messages = messageStore.getMessagesInConversation(id);
        return messages;
    }

    public List<Conversation> getConversations(Instant time) {
        List<Conversation> conversations = conversationStore.getConversationsByTime(time);
        return conversations;
    }

    public List<Conversation> getConversations(String username) {
        User user = userStore.getUser(username);
        UUID ownerId = user.getId();
        List<Conversation> conversations = conversationStore.getConversationsByAuthor(ownerId);
        return conversations;
    }

    public List<Conversation> getConversations() {
        return conversationStore.getAllConversations();
    }

}