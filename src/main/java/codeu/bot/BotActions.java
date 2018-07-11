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

    public enum Action {
        MISSING_PARAMETER{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("missing parameters");
            }
        },
        NO_IDEA_PLACEHOLDER{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("sorry idk");
            }
        },
        SET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("set %s",(String) argsObjects[0]);
            }
        },
        GET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get %s",(String) argsObjects[0]);
            }
        },
        GET_SETTINGS{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get settings");
            }
        },
        GET_STAT{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get %s",(String) argsObjects[0]);
            }
        },
        GET_STATS{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get stats");
            }
        },
        GET_MY_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get my messages");
            }
        },
        GET_MESSAGES_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get messages like keyword");
            }
        },
        GET_MESSAGES_BY_AUTHOR{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get messages by author");
            }
        },
        GET_CONVERSATIONS_WITH_UNREAD_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get conversations with unread messages");
            }
        },
        GET_CONVERSATIONS_ABOUT_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get conversations about keyword");
            }
        },
        GET_CONVERSATION_SUMMARY{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get conversation summary");
            }
        },
        NAVIGATE_TO_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("navigate to conversation");
            }
        },
        NAVIGATE{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("navigate");
            }
        },
        GET_HELP{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get help");
            }
        }
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
                String contentMessages = getContentFromMessages(messages);
                String  content= "Messages in the conversation <a href=\"/chat/"+ title+ "\">"+ title + "</a>";
                addAnswerMessageToStorage(contentMessages + content);
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
        GET_MESSAGES_BY_CREATION_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Message> messages = messageStore.getMessagesInTime(time);
                String contentMessages = getContentFromMessages(messages);
                String  content= "Messages within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(contentMessages + content);
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
        GET_CONVERSATIONS_BY_CREATION_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Conversation> conversations = conversationStore.getConversationsByTime(time);
                String titleConversations = getTitleFromConversations(conversations);
                String  content= "Conversations within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(titleConversations + content);
            }
        },
        GET_CONVERSATION_BY_AUTHOR{
            @Override
            public void doAction(Object ... argsObjects) {
                String username = (String) argsObjects[0];
                User user = userStore.getUser(username);
                UUID ownerId = user.getId();
                List<Conversation> conversations = conversationStore.getConversationsByAuthor(ownerId);
                String titleConversations = getTitleFromConversations(conversations);
                String  content= "Conversations done by " + username + " have been retrieved";
                addAnswerMessageToStorage(titleConversations + content);
            }
        },
        GET_ALL_CONVERSATIONS{
            @Override
            public void doAction(Object ... argsObjects) {        
                String titleConversations = getTitleFromConversations(conversationStore.getAllConversations());                
                String content= "All conversations have been retrieved";
                addAnswerMessageToStorage(titleConversations + content);
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
        },*/
        NAVIGATE_TO_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) throws IOException {
                String title = (String) argsObjects[0];
                HttpServletResponse response = (HttpServletResponse) argsObjects[1];
                Conversation conversation = conversationStore.getConversationWithTitle(title);
                if (conversation != null) {
                    response.sendRedirect("/chat/" + title);
                }
                String content = "You have been redirected to <a href\"/chat/"+title+"\">"+title+"</a>";
                addAnswerMessageToStorage(content);
            }
        },/*
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
        conversation = conversationStore.getBotConversation(user);
    }

	/**
	 * @param activityStore the activityStore to set
	 */
	public static void setActivityStore(ActivityStore aStore) {
		activityStore = aStore;
    }
    
    /**
     * @param conversationStore the conversationStore to set
     */
    public static void setConversationStore(ConversationStore cStore) {
        conversationStore = cStore;
    }

    /**
     * @param messageStore the messageStore to set
     */
    public static void setMessageStore(MessageStore mStore) {
        messageStore = mStore;
    }

    /**
     * @param userStore the userStore to set
     */
    public void setUserStore(UserStore uStore) {
        userStore = uStore;
    }

    private static void addAnswerMessageToStorage(String content) {
        User botUser = userStore.getUser(UserStore.BOT_USER_NAME);
        UUID botId = botUser.getId();
        Message message = new Message(UUID.randomUUID(), conversation.getId(), 
                                        botId,
                                        content,
                                        Instant.now());
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

    private static String getContentFromMessages(List<Message> messages) {
        String content = "";
        for (Message message : messages) {
            content += message.getContent() + "\n";
        }
        return content;
    }

    private static String getTitleFromConversations(List<Conversation> conversations) {
        String title = "";
        for (Conversation conversation : conversations) {
            title += conversation.getTitle() + "\n";
        }
        return title;
    }

}