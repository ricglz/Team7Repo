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

    // GENERAL COMMENT: Put prompt before list of things (like like of messages)
    public enum Action {
        MISSING_PARAMETER{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("missing parameters");
            }
        },
        // NOT DONE
        GET_CONVERSATIONS_WITH_UNREAD_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.printf("get conversations with unread messages");
            }
        },
        // DONE (set description to "animate my gifs")
        // Maybe link to profile (or some sort of callback)
        SET_DESCRIPTION{
            @Override
            public void doAction(Object ... argsObjects) {
                String description = (String) argsObjects[0];
                user.setDescription(description);
                userStore.updateUser(user);
                String content = "Description set, <a href=\"/profile\"> View your profile </a>";
                addAnswerMessageToStorage(content);
            }
        },
        // DONE (create conversation "team7bottest")
        // Doesnt do error checking on existing conversation
        CREATE_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) {
                String title = (String) argsObjects[0];
                String content;
                if (conversationStore.isTitleTaken(title)) {
                    content = "<a href=\"/chat/"+ title+ "\">"+ title + "</a> conversation was already created";
                }
                else {
                    UUID owner = user.getId();
                    Conversation conversation = new Conversation(UUID.randomUUID(), owner, title, Instant.now());
                    conversationStore.addConversation(conversation);
                    activityStore.addActivity(conversation);
                    content = "<a href=\"/chat/"+ title+ "\">"+ title + "</a> conversation has been created !";
                }
                addAnswerMessageToStorage(content);
            }
        },
        // DONE (send message "all I do is win" to team7bottest)
        // conversation counter doesnt work
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
                //Updating user
                user.increaseMessageCount();
                userStore.updateUser(user);
                //Updating conversation
                conversation.increaseMessageCount();
                conversationStore.updateConversation(conversation);
                String answerContent= content + " sent to <a href=\"/chat/"+ title+ "\">"+ title + "</a>";
                addAnswerMessageToStorage(answerContent);
            }
        },
        // DONE (get messages from team7bottest)
        // Fuzzy matching updates needed, print response prompt before messages
        GET_MESSAGES_FROM_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) {
                String title = (String) argsObjects[0];
                Conversation conversation = conversationStore.getConversationWithTitle(title);
                UUID id = conversation.getId();
                List<Message> messages = messageStore.getMyMessagesInConversation(id, user.getId());
                String contentMessages = getContentFromMessages(messages);
                String  content= "Messages in the conversation <a href=\"/chat/"+ title+ "\">"+ title + "</a> <br/>";
                addAnswerMessageToStorage(content + contentMessages);
            }
        },
        // NOT DONE
        SET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                return;
            }
        },
        // navigate to profile
        NAVIGATE{
            @Override
            public void doAction(Object ... argsObjects) throws IOException {
                String location = (String) argsObjects[0];
                response = (HttpServletResponse) argsObjects[1];
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
        // NOT DONE Mistmatch problems currently
        // get conversation count
        // get [stat]
        GET_STAT{
            @Override
            public void doAction(Object ... argsObjects) {
                String statistic = (String) argsObjects[0];
                List<Message> userMessages = messageStore.getMessagesByAuthor(user.getId());
                String content;
                if (statistic.equals("message count")) {
                    content = "Amount of messages: " + userMessages.size() + "<br />";
                }
                else if (statistic.equals("conversation count")) {
                    int conversationCount = getConversationCount(userMessages);
                    content = "Conversations you have participated: " + (conversationCount - 1) + "<br />";
                }
                else if (statistic.equals("longest message")) {
                    userMessages.sort(Message.messageComparator);
                    Message longestMessage = userMessages.get(userMessages.size()-1);
                    long maxCharactersLength = longestMessage.getContent().length()-1;
                    content = "Longest message with " + maxCharactersLength + " characerts<br />";
                }
                else {
                    String title = conversationStore.getMostActiveConversationTitle();
                    long messageCount = conversationStore.getMaxMessageCount();
                    content = "<a href=\"/chat/"+ title+ "\">"+ title + "</a> is the most active conversation with " 
                              + messageCount +" messages";
                }
                addAnswerMessageToStorage(content);
            }
        },
        // NOT DONE
        GET_SETTING{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("get setting");
            }
        },
        // get messages from [time expression]
        // includes bot messages. aka is not specific to user.
        // "get messages from last friday" doesnt work
        // Currently also gives ACTION_NOT_FOUND message
        GET_MESSAGES_BY_CREATION_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Message> messages = messageStore.getMyMessagesInTime(time, user.getId());
                String contentMessages = getContentFromMessages(messages);
                String  content= "Messages within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(content + contentMessages);
            }
        },
        // DONE (help, tutorial, guide)
        // Updae ABOUT page to have more details on using bot
        // clean up help formatting/add more detail to this result as well
        GET_HELP{
            @Override
            public void doAction(Object ... argsObjects) {
                String content = "This are the actions you can do<br />";
                content += "Send message to a conversation, create a conversation, get messages you have send";
                content += "Go to a specific page, set your description, get all the conversations";
                content += "get conversations done by someone and in a specific time";
                addAnswerMessageToStorage(content);
            }
        },
        // DONE (get my messages)
        GET_MY_MESSAGES{
            @Override
            public void doAction(Object ... argsObjects) {
                UUID author = user.getId();
                List<Message> messages = messageStore.getMessagesByAuthor(author);
                String contentMessages = getContentFromMessages(messages);
                String content = "Your messages have been retrieved";
                addAnswerMessageToStorage(content + contentMessages);
            }
        },
        // DONE (get conversations made [time expression])
        // Currently also gives ACTION_NOT_FOUND message
        GET_CONVERSATIONS_BY_CREATION_TIME{
            @Override
            public void doAction(Object ... argsObjects) {
                Instant time = (Instant) argsObjects[0];
                List<Conversation> conversations = conversationStore.getConversationsByTime(time);
                String titleConversations = getTitleFromConversations(conversations);
                String  content= "Conversations within the time of " + time.toString() + " have been retrieved";
                addAnswerMessageToStorage(content + titleConversations);
            }
        },
        // get conversation by [author]
        // Currently includes bot conversation
        GET_CONVERSATIONS_BY_AUTHOR{
            @Override
            public void doAction(Object ... argsObjects) {
                String username = (String) argsObjects[0];
                User user = userStore.getUser(username);
                UUID ownerId = user.getId();
                List<Conversation> conversations = conversationStore.getConversationsByAuthor(ownerId);
                String titleConversations = getTitleFromConversations(conversations);
                String  content= "Conversations done by " + username + " have been retrieved";
                addAnswerMessageToStorage(content + titleConversations);
            }
        },
        // get all conversations
        // Currently includes bot conversations
        GET_ALL_CONVERSATIONS{
            @Override
            public void doAction(Object ... argsObjects) {
                String titleConversations = getTitleFromConversations(conversationStore.getUserConversations());
                String content= "All conversations have been retrieved";
                addAnswerMessageToStorage(content + titleConversations);
            }
        },
        // DONE (get all stats, get statistics)
        GET_STATS{
            @Override
            public void doAction(Object ... argsObjects) {
                List<Message> userMessages = messageStore.getMessagesByAuthor(user.getId());
                int conversationCount = getConversationCount(userMessages);
                userMessages.sort(Message.messageComparator);
                Message longestMessage = userMessages.get(userMessages.size()-1);
                long maxCharactersLength = longestMessage.getContent().length()-1;
                String title = conversationStore.getMostActiveConversationTitle();
                long messageCount = conversationStore.getMaxMessageCount();
                String content = "This are your stats<br />";
                content += "Amount of messages: " + userMessages.size() + "<br />";
                content += "Conversations you have participated: " + conversationCount + "<br />";
                content += "Longest message with " + maxCharactersLength + " characerts<br />";
                content += "<a href=\"/chat/"+ title+ "\">"+ title + "</a> is the most active conversation with " 
                          + messageCount +" messages<br />";
                addAnswerMessageToStorage(content);
            }
        },
        // NOT DONE
        GET_SETTINGS{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("get settings");
            }
        },
        // navigate to team7bottest
        // take me to team7bottest
        // Currently broken
        NAVIGATE_TO_CONVERSATION{
            @Override
            public void doAction(Object ... argsObjects) throws IOException {
                String title = (String) argsObjects[0];
                response = (HttpServletResponse) argsObjects[1];
                Conversation conversation = conversationStore.getConversationWithTitle(title);
                if (conversation != null) {
                    response.sendRedirect("/chat/" + title);
                }
                String content = "You have been redirected to <a href\"/chat/"+title+"\">"+title+"</a>";
                addAnswerMessageToStorage(content);
            }
        },
        // NOT DONE
        GET_CONVERSATION_SUMMARY{
            @Override
            public void doAction(Object ... argsObjects) {
                System.out.println("getting conversation summary");
            }
        },
        // DONE (get messages about "stats")
        GET_MESSAGES_LIKE_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                String keyword = (String) argsObjects[0];
                List<Message> messages = getMyMessagesByKeyword(keyword);
                String contentMessages = getContentFromMessages(messages);
                String content = "These are the messages that contains the keyword";
                addAnswerMessageToStorage(content + contentMessages);
            }
        },
        // Maybe it works
        // get conversation about "test"
        GET_CONVERSATIONS_ABOUT_KEYWORD{
            @Override
            public void doAction(Object ... argsObjects) {
                String keyword = (String) argsObjects[0];
                List<Message> messages = getMyMessagesByKeyword(keyword);
                HashSet<UUID> ids = conversationsIdsFromMessages(messages);
                ids = conversationsByKeyword(keyword, ids);
                List<Conversation> conversations = getConversationsById(ids);
                String conversationsTitles = getTitleFromConversations(conversations);
                String content = "These are the the conversations that its content contains the keyword";
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

            private HashSet<UUID> conversationsByKeyword(String keyword, HashSet<UUID> ids) {
                List<Conversation> conversations = conversationStore.getAllConversations();
                for (Conversation conversation : conversations) {
                    boolean titleHasKeyword = conversation.getTitle().contains(keyword);
                    if (titleHasKeyword) {
                        ids.add(conversation.getId());
                    }
                }
                return ids;
            }
        },
        // NOT DONE
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

    /**
     * Checks the content of every message to check if it contains the keyword and
     * then add it in a vector
     */
    private static List<Message> getMyMessagesByKeyword(String keyword) {
        List <Message> messages = messageStore.getMessages();
        List <Message> messagesByKeyword = new ArrayList<>();
        UUID userId = user.getId();
        for (Message message : messages) {
            boolean hasKeyword = message.getContent().contains(keyword);
            boolean fromUser = message.getAuthorId().equals(userId);
            if (hasKeyword && fromUser) {
                messagesByKeyword.add(message);
            }
        }
        return messagesByKeyword;
    }

    /**
     * Recieves content that will be send as a message from the bot
     */
    private static void addAnswerMessageToStorage(String content) {
        User botUser = userStore.getUser(UserStore.BOT_USER_NAME);
        UUID botId = botUser.getId();
        Message message = new Message(UUID.randomUUID(), conversation.getId(),
                botId,
                content,
                Instant.now());
        messageStore.addMessage(message);
    }

    /**
     * Checks if it is a valid conversation
     */
    private static boolean validLocation(String location) {
        List<String> valid_locations = Lists.newArrayList("/register", "/login", "/about.jsp", "/activity",
                "/admin", "/profile", "/conversations");
        return valid_locations.contains(location);
    }

    /**
     * Redirects to a location
     */
    private static void goTo(String location) throws IOException {
        response.sendRedirect(location);
    }

    /**
     * Gets the content from the messages of a list to pass them as a string
     */
    private static String getContentFromMessages(List<Message> messages) {
        String content = "<ul>";
        for (Message message : messages) {
            content += "<li>" + message.getContent() + "</li>";
        }
        content += "</ul>";
        return content;
    }

    /**
     * Gets the title from the conversations of a list to pass them as a string
     */
    private static String getTitleFromConversations(List<Conversation> conversations) {
        String title = "<ul>";
        for (Conversation conversation : conversations) {
            title += "<li>" + conversation.getTitle() + "</li>";
        }
        title += "</ul>";
        return title;
    }

    /**
     * Gets the amount of unique conversation in a list of messages,
     * adding the UUID of the conversation of the message in a set.
     */
    private static int getConversationCount(List<Message> userMessages) {
        HashSet<UUID>conversationIds = new HashSet<UUID>();
        for (Message message : userMessages) {
            conversationIds.add(message.getConversationId());
        }
        return conversationIds.size();
    }

}
