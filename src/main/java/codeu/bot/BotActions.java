package codeu.bot;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
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
        GET_CONVERSATIONS_WITH_UNREAD_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get conversations with unread messages");
            }
        },
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
                activityStore.addActivity(conversation);
                String content = "<a href=\"/chat/"+ title+ "\">"+ title + "</a> has been created !";
                addAnswerMessageToStorage(content);
            }
        },
        SEND_MESSAGE{
            @Override
            public void doAction(Object ... argsObjects) {
                String content = (String) argsObjects[0];
                String title = (String) argsObjects[1];
                Conversation conversation = conversationStore.getConversationWithTitle(title);
                System.out.println(conversation);
                UUID id = conversation.getId();
                UUID author = user.getId();
                Message message = new Message(UUID.randomUUID(), id, author, content, Instant.now());
                messageStore.addMessage(message);
                activityStore.addActivity(message);
                String answerContent= content + " sent to <a href=\"/chat/"+ title+ "\">"+ title + "</a>";
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
        },
        SET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                return;   
            }
        },
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
        },
        GET_STAT{
            @Override
            public void doAction(Object ... argsObjects) {
                boolean getMessageCount = (Boolean) argsObjects[0];
                boolean getConversationCount = (Boolean) argsObjects[1];
                boolean getLongestMessage = (Boolean) argsObjects[2];
                List<Message> userMessages = messageStore.getMessagesByAuthor(user.getId());
                String content = "";
                if (getMessageCount) {
                    content += "Amount of messages: " + userMessages.size() + "\n";
                }
                if (getConversationCount) {
                    int conversationCount = getConversationCount(userMessages);
                    content += "Conversations you have participated: " + conversationCount + "\n";
                }
                if (getLongestMessage) {
                    userMessages.sort(Message.messageComparator);
                    Message longestMessage = userMessages.get(userMessages.size()-1);
                    long maxCharactersLength = longestMessage.getContent().length()-1;
                    content += "Longest message with " + maxCharactersLength + " characerts\n";
                }
                addAnswerMessageToStorage(content);
            }

            private int getConversationCount(List<Message> userMessages) {
                HashSet<UUID>conversationIds = new HashSet<UUID>();
                for (Message message : userMessages) {
                    conversationIds.add(message.getConversationId());
                }
				return conversationIds.size();
			}
        },
        GET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("get setting");
            }
        },
        GET_MESSAGES_BY_CREATION_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Message> messages = messageStore.getMessagesInTime(time);
                String contentMessages = getContentFromMessages(messages);
                String  content= "Messages within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(contentMessages + content);
            }
        },
        GET_HELP{
            @Override
            public void doAction(Object ... argsObjects) {
                String content = "This are the actions you can do\n";
                content += "Send message to a conversation, create a conversation, get messages you have send\n";
                content += "Go to a specific page, set your description, get all the conversations\n";
                content += "get conversations done by someone and in a specific time\n";
                addAnswerMessageToStorage(content);
            }
        },
        GET_MY_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                UUID author = user.getId();
                List<Message> messages = messageStore.getMessagesByAuthor(author);
                String contentMessages = getContentFromMessages(messages);
                String content = "Your messages have been retrieved";
                addAnswerMessageToStorage(contentMessages + content);
            }
        },
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
        GET_CONVERSATIONS_BY_AUTHOR{
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
        },
        GET_STATS{
            @Override
            public void doAction(Object ... argsObjects) {
                List<Message> userMessages = messageStore.getMessagesByAuthor(user.getId());
                int conversationCount = getConversationCount(userMessages);
                userMessages.sort(Message.messageComparator);
                Message longestMessage = userMessages.get(userMessages.size()-1);
                long maxCharactersLength = longestMessage.getContent().length()-1;
                String content = "This are your stats\n";
                content += "Amount of messages: " + userMessages.size() + "\n";
                content += "Conversations you have participated: " + conversationCount + "\n";
                content += "Longest message with " + maxCharactersLength + " characerts\n";
                addAnswerMessageToStorage(content);
            }

			private int getConversationCount(List<Message> userMessages) {
                HashSet<UUID>conversationIds = new HashSet<UUID>();
                for (Message message : userMessages) {
                    conversationIds.add(message.getConversationId());
                }
				return conversationIds.size();
			}
        },
        GET_SETTINGS{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("get settings");
            }
        },
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
        },
        GET_CONVERSATION_SUMMARY{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("getting conversation summary");
            }
        },
        GET_MESSAGES_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                String keyword = (String) argsObjects[0];
                List<Message> messages = getMessagesByKeyword(keyword);
                String contentMessages = getContentFromMessages(messages);
                String content = "These are the messages that contains the keyword";
                addAnswerMessageToStorage(contentMessages + content);
            }
        },
        GET_CONVERSATION_WITH_CONTENT_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                String keyword = (String) argsObjects[0];
                List<Message> messages = getMessagesByKeyword(keyword);
                HashSet<UUID> ids = conversationsIdsFromMessages(messages);
                List<Conversation> conversations = getConversationsById(ids);
                String conversationsTitles = getTitleFromConversations(conversations);
                String content = "These are the the conversations that the content contains the keyword";
                addAnswerMessageToStorage(conversationsTitles + content);
            }

            private List<Conversation> getConversationsById(HashSet<UUID> ids) {
                List<Conversation> conversations = new ArrayList<>();
                for (UUID id : ids) {
                    Conversation conversation = conversationStore.getConversationWithUUID(id);
                    conversations.add(conversation);
                }
				return conversations;
			}

			private HashSet<UUID> conversationsIdsFromMessages(List<Message> messages) {
                HashSet<UUID> conversationsIds = new HashSet<>();
                for (Message message : messages) {
                    conversationsIds.add(message.getConversationId());
                }
                return conversationsIds;
            }
        },
        GET_CONVERSATION_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                String keyword = (String) argsObjects[0];
                List<Conversation> conversations = conversationsByKeyword(keyword);
                String conversationsTitles = getTitleFromConversations(conversations);
                String content = "These are the the conversations that its title contains the keyword";
                addAnswerMessageToStorage(conversationsTitles + content);
            }

            private List<Conversation> conversationsByKeyword(String keyword) {
                List<Conversation> conversations = conversationStore.getAllConversations();
                List<Conversation> conversationsFiltered = new ArrayList<>();
                for (Conversation conversation : conversations) {
                    boolean titleHasKeyword = conversation.getTitle().contains(keyword);
                    if (titleHasKeyword) {
                        conversationsFiltered.add(conversation);
                    }
                }
                return conversationsFiltered;                
            }
        },
        GET_CONVERSATIONS_ABOUT_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("get conversation about keyword");
            }
        },
        GET_CONVERSATION_WITH_UNREAD_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                return;
            }
        },
        NOT_FOUND{
            @Override
            public void doAction(Object ... argsObjects) throws IOException {
                String content = "The action that you're trying to do can't be executed.";
                addAnswerMessageToStorage(content);
                Action.GET_HELP.doAction();
            }
        };
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

    private static List<Message> getMessagesByKeyword(String keyword) {
        List <Message> messages = messageStore.getMessages();
        List <Message> messagesByKeyWord = new ArrayList<>();
        UUID userId = user.getId();
        for (Message message : messages) {
            boolean hasKeyword = message.getContent().contains(keyword);
            boolean fromUser = message.getAuthorId().equals(userId);
            if (hasKeyword && fromUser) {
                messagesByKeyWord.add(message);
            }
        }
        return messagesByKeyWord; 
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