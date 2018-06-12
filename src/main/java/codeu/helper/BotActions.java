package codeu.helper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ActivityStore;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

/** Helper class responsible of the bot's actions */
public class BotActions {

    private ActivityStore activityStore;
    private User user;
    private ConversationStore conversationStore;
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

    public void createConversation(String title) {
        UUID owner = user.getId();
        Conversation conversation = new Conversation(UUID.randomUUID(), owner, title, Instant.now());
        conversationStore.addConversation(conversation);
    }

    public void sendMessage(String content, String title) {
        Conversation conversation = conversationStore.getConversationWithTitle(title);
        UUID id = conversation.getId();
        UUID author = user.getId();
        Message message = new Message(UUID.randomUUID(), id, author, content, Instant.now());
        messageStore.addMessage(message);
    }

    public List<Message> getMessage(Instant time) {
        List<Message> messages = messageStore.getMessagesInTime(time);
        return messages;
    }
}