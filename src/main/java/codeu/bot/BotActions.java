package codeu.bot;

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

    public enum action {
        SET_DESCRIPTION{
            @Override
            public void doAction(Object ... argsObjects) {
                String description = (String) argsObjects[0];
                user.setDescription(description);
                userStore.updateUser(user);
            }
        },
        CREATE_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) {
                String title = (String) argsObjects[0];
                UUID owner = user.getId();
                Conversation conversation = new Conversation(UUID.randomUUID(), owner, title, Instant.now());
                conversationStore.addConversation(conversation);
                String content = "<a href=\"/chat/"+ title+ "\">"+ title + "</a> has been created !";
                addAnswerMessageToStorage(content);
            }
        },
        SEND_MESSAGE{
            @Override
            public void doAction(Object ... argsObjects) {
                String title = (String) argsObjects[0];
                String content = (String) argsObjects[1];
                Conversation conversation = conversationStore.getConversationWithTitle(title);
                UUID id = conversation.getId();
                UUID author = user.getId();
                Message message = new Message(UUID.randomUUID(), id, author, content, Instant.now());
                messageStore.addMessage(message);
                String  answerContent= content + " sent to <a href=\"/chat/"+ title+ "\">"+ title + "</a>";
                addAnswerMessageToStorage(answerContent);
            }
        },
        GET_MESSAGES_FROM_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) {
                String title = (String) argsObjects[0];
                Conversation conversation = conversationStore.getConversationWithTitle(title);
                UUID id = conversation.getId();
                List<Message> messages = messageStore.getMessagesInConversation(id);
                String  content= "Messages in the conversation <a href=\"/chat/"+ title+ "\">"+ title + "</a>";
                addAnswerMessageToStorage(content);
            }
        },/*
        SET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },*/
        NAVIGATE{
            @Override
            public void doAction(Object ... argsObjects) throws IOException {
                String location = (String) argsObjects[0];
                if(!location.contains("/")){
                    location = "/" + location;
                }
                String content;
                if (validLocation(location)) {
                    content = "You have been redirect to: <a href=\"/chat/"+ location+ "\">"+ location + "</a>";
                    goTo(location);
                }
                else {
                    content = "Location is invalid";
                }
                addAnswerMessageToStorage(content);
            }
        },/*
        GET_STAT{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },*/
        GET_MESSAGES_AT_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Message> messages = messageStore.getMessagesInTime(time);
                String  content= "Messages within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(content);
            }
        },/*
        GET_TUTORIAL{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_MY_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },*/
        GET_CONVERSATION_BY_CREATION_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Conversation> conversations = conversationStore.getConversationsByTime(time);
                String  content= "Conversations within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(content);
            }
        },
        GET_CONVERSATION_BY_CREATOR{
            @Override
            public void doAction(Object ... argsObjects) {
                String username = (String) argsObjects[0];
                User user = userStore.getUser(username);
                UUID ownerId = user.getId();
                List<Conversation> conversations = conversationStore.getConversationsByAuthor(ownerId);
                String  content= "Conversations done by " + username + " have been retrieved";
                addAnswerMessageToStorage(content);
            }
        },
        GET_CONVERSATIONS{
            @Override
            public void doAction(Object ... argsObjects) {        
                String  content= "All conversations have been retrieved";
                addAnswerMessageToStorage(content);
            }
        },/*
        GET_STATS{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_SETTINGS{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        NAVIGATE_TO_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_CONVERSATION_SUMMARY{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_MESSAGES_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_CONVESATION_WITH_CONTENT_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_CONVERSATION_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        },
        GET_CONVERSATION_WITH_UNREAD_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                
            }
        }*/;
        public abstract void doAction(Object ... argsObjects) throws IOException;
    }

    private static ActivityStore activityStore;
    private static User user;
    private static Conversation conversation;
    private static ConversationStore conversationStore;
    private static HttpServletResponse response;
    private static MessageStore messageStore;
    private static UserStore userStore;

    public BotActions(String username) {
        setActivityStore(ActivityStore.getInstance());
        setConversationStore(ConversationStore.getInstance());
        setMessageStore(MessageStore.getInstance());
        setUserStore(UserStore.getInstance());
        user = userStore.getUser(username);
        conversation = conversationStore.getBotConversation(user.getId());
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

    private static void addAnswerMessageToStorage(String content) {
        Message message = new Message(UUID.randomUUID(), conversation.getId(), 
                                        user.getId(), content, Instant.now());
        messageStore.addMessage(message);
    }

    private static boolean validLocation(String location) {
        List<String> valid_locations = Lists.newArrayList("/register", "/login", "/about.jsp", "/activity",
                                                        "/admin", "/profile", "/conversations");
        return valid_locations.contains(location);
    }

    private static void goTo(String location) throws IOException {
        response.sendRedirect(location);
    }

}