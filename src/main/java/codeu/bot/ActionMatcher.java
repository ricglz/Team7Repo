package codeu.bot;

import codeu.bot.BotActions;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.simple.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// SET_DESCRIPTION, String descrption
// CREATE_CONVERSATION, String conversationTitle
// SEND_MESSAGE, String message, String conversationTitle
// GET_MESSAGES_FROM_CONVERSATION, String conversationTitle
// SET_SETTING,
// NAVIGATE,
// GET_STAT,
// GET_SETTING,
// GET_MESSAGES_AT_TIME,
// GET_TUTORIAL,
// GET_MY_MESSAGES,
// GET_CONVERSATION_BY_CREATION_TIME,
// GET_CONVERSATION_BY_CREATOR,
// GET_CONVERSATIONS,
// GET_STATS,
// GET_SETTINGS,
// NAVIGATE_TO_CONVERSATION,
// GET_CONVERSATION_SUMMARY,
// GET_MESSAGES_LIKE_KEYWORD,
// GET_CONVESATION_WITH_CONTENT_LIKE_KEYWORD,
// GET_CONVERSATION_LIKE_KEYWORD,
// GET_CONVERSATION_WITH_UNREAD_MESSAGES
public class ActionMatcher {
    public static String input;

    public ActionMatcher(String input) {
        this.input = input;
    }

    public void getActionType(String input) throws IOException {
        // Options for how this interacts with the action executor:
        // 1. Return an ActivityType implementing object
        // which has been instantiated with the appropriate parameters
        // 2. Access the enum type and call the doAction method
        // with Object parameter? Q: Is this an array? 

        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        CoreDocument document = new CoreDocument(input);

        pipeline.annotate(document);

        if (document.sentences().size() == 1) {
            return;
            // Raise error, one command per message please.
        }


        // tokens returns a CoreLabel ArrayList
        // posTags returns a String ArrayList

        CoreSentence command = document.sentences().get(0);
        ArrayList<CoreLabel> commandTokens = (ArrayList<CoreLabel>) command.tokens();
        ArrayList<String> commandTokensString = new ArrayList<String>();
        
        for (CoreLabel label : commandTokens) {
            commandTokensString.add(label.value().toLowerCase());
        }

        ArrayList<String> commandPOS = (ArrayList<String>) command.posTags();


        // Currently going with a couple formatting rules for commands:
        // - desc/messages denoted with "content"
        // - conversations denoted with <conversation title>
        // - settings with $
        //  - if settings are 2-3 words long, with a list/array of setting names
        //  can use edit distance to overcome this (set theme color to black => 
        //  can extract the setting operating over as ThemeColor or something similar)
        // - same thing for conversation titles, if we want to do this we can have
        //  a list of conversations and allow for fuzzy matching of conversation names
        //  a "Did you mean <conversationTitle>?"" might be a good idea in this case.
        //  This would let us get rid of the need for <>


        // SET_DESCRIPTION, params: String description
        // Need to extent to synonyms sets
        int setIndex = commandTokensString.indexOf("set");
        if (setIndex != -1 && 
            commandPOS.get(setIndex).contains("VB")) {
            // Going to be setting something

            int descIndex = commandTokensString.indexOf("description");
            String description = "";
            if (descIndex != -1 && commandPOS.get(descIndex).contains("NN")) {
                // Find set description parameter
                Pattern pattern = Pattern.compile("\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    description = matcher.group(1);
                }
                System.out.printf("Setting description to %s.",description);
                BotActions.action.SET_DESCRIPTION.doAction(description);
                return;
            }
        }

        // CREATE_CONVERSATION, params: String conversationTitle
        int createIndex = commandTokensString.indexOf("create");
        if (createIndex != -1 && 
            commandPOS.get(createIndex).contains("VB")) {
            // Going to be creating something
            // Right now we can only create conversations so don't need to look for conversation
            String conversationTitle = "";
            Pattern pattern = Pattern.compile("<([^\"]*)>");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                conversationTitle = matcher.group(1);
                System.out.println("got here");
            }
            System.out.printf("Creating conversation %s.",conversationTitle);
            BotActions.action.CREATE_CONVERSATION.doAction(conversationTitle);
            return;
        }


        // SEND_MESSAGE, params: String message, String conversationTitle
        int sendIndex = commandTokensString.indexOf("send");
        if (sendIndex != -1 && 
            commandPOS.get(sendIndex).contains("VB")) {
            // Going to be sending something
            // Same logic for create, know we're looking for a message and conversation

            String message = "";
            Pattern messagePattern = Pattern.compile("\"([^\"]*)\"");
            Matcher messageMatcher = messagePattern.matcher(input);
            if (messageMatcher.find()) {
                message = messageMatcher.group(1);
            }

            String conversationTitle = "";
            Pattern conversationTitlePattern = Pattern.compile("<([^\"]*)>");
            Matcher conversationTitleMatcher = conversationTitlePattern.matcher(input);
            if (conversationTitleMatcher.find()) {
                conversationTitle = conversationTitleMatcher.group(1);
            }
            System.out.printf("Sending message \"%s\" in conversation <%s>.",message,conversationTitle);
            BotActions.action.SEND_MESSAGE.doAction(message,conversationTitle);
            return;
        }

        // GET...
        int getIndex = commandTokensString.indexOf("get");
        if (getIndex != -1 && 
            commandPOS.get(getIndex).contains("VB")) {
            // Going to be getting something
            // Have a lot of methods where we get something so this block of code is going to branch a lot

            if (getIndex < commandTokensString.size()-1) {
                // Getting the lemma so we can ignore pluralizations
                String object = commandTokens.get(getIndex+1).lemma();

                switch (object) {
                    case "message": {
                        // Getting messages

                        int prepositionIndex = commandPOS.indexOf("IN");
                        if (prepositionIndex != -1) {
                            String conversationTitle = "";
                            Pattern conversationTitlePattern = Pattern.compile("<([^\"]*)>");
                            Matcher conversationTitleMatcher = conversationTitlePattern.matcher(input);
                            if (conversationTitleMatcher.find()) {
                                conversationTitle = conversationTitleMatcher.group(1);
                            }

                            if (!conversationTitle.equals("")) {
                                System.out.printf("Getting messages in conversation <%s>.",conversationTitle);
                                BotActions.action.GET_MESSAGES_FROM_CONVERSATION.doAction(conversationTitle);
                                return;
                            }
                        }
                        
                    }
                    case "conversation": {
                        // Getting conversations
                    }
                }
            }
        }
        return;
    }

    public static void main(String[] args) {
        // Options for how this interacts with the action executor:
        // 1. Return an ActivityType implementing object
        // which has been instantiated with the appropriate parameters
        // 2. Access the enum type and call the doAction method
        // with Object parameter? Q: Is this an array? 

        String input = "Please set the description to \"Hello, world!\"";

        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        CoreDocument document = new CoreDocument(input);

        pipeline.annotate(document);

        if (document.sentences().size() != 1) {
            return;
            // Raise error, one command per message please.
        }


        // tokens returns a CoreLabel ArrayList
        // posTags returns a String ArrayList

        CoreSentence command = document.sentences().get(0);
        ArrayList<CoreLabel> commandTokens = (ArrayList<CoreLabel>) command.tokens();
        ArrayList<String> commandTokensString = new ArrayList<String>();
        
        for (CoreLabel label : commandTokens) {
            commandTokensString.add(label.value().toLowerCase());
        }

        ArrayList<String> commandPOS = (ArrayList<String>) command.posTags();


        // Currently going with a couple formatting rules for commands:
        // - desc/messages denoted with "content"
        // - conversations denoted with <conversation title>
        // - settings with $
        //  - if settings are 2-3 words long, with a list/array of setting names
        //  can use edit distance to overcome this (set theme color to black => 
        //  can extract the setting operating over as ThemeColor or something similar)
        // - same thing for conversation titles, if we want to do this we can have
        //  a list of conversations and allow for fuzzy matching of conversation names
        //  a "Did you mean <conversationTitle>?"" might be a good idea in this case.
        //  This would let us get rid of the need for <>


        // SET_DESCRIPTION, params: String description
        // Need to extent to synonyms sets
        int setIndex = commandTokensString.indexOf("set");
        if (setIndex != -1 && 
            commandPOS.get(setIndex).contains("VB")) {
            // Going to be setting something

            int descIndex = commandTokensString.indexOf("description");
            String description = "";
            if (descIndex != -1 && commandPOS.get(descIndex).contains("NN")) {
                // Find set description parameter
                Pattern pattern = Pattern.compile("\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    description = matcher.group(1);
                }
                System.out.printf("Setting description to %s.",description);
                // BotActions.action.SET_DESCRIPTION.doAction(description);
                return;
            }
        }

        // CREATE_CONVERSATION, params: String conversationTitle
        int createIndex = commandTokensString.indexOf("create");
        if (createIndex != -1 && 
            commandPOS.get(createIndex).contains("VB")) {
            // Going to be creating something
            // Right now we can only create conversations so don't need to look for conversation
            String conversationTitle = "";
            Pattern pattern = Pattern.compile("<([^\"]*)>");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                conversationTitle = matcher.group(1);
                System.out.println("got here");
            }
            System.out.printf("Creating conversation %s.",conversationTitle);
            // BotActions.action.CREATE_CONVERSATION.doAction(conversationTitle);
            return;
        }


        // SEND_MESSAGE, params: String message, String conversationTitle
        int sendIndex = commandTokensString.indexOf("send");
        if (sendIndex != -1 && 
            commandPOS.get(sendIndex).contains("VB")) {
            // Going to be sending something
            // Same logic for create, know we're looking for a message and conversation

            String message = "";
            Pattern messagePattern = Pattern.compile("\"([^\"]*)\"");
            Matcher messageMatcher = messagePattern.matcher(input);
            if (messageMatcher.find()) {
                message = messageMatcher.group(1);
            }

            String conversationTitle = "";
            Pattern conversationTitlePattern = Pattern.compile("<([^\"]*)>");
            Matcher conversationTitleMatcher = conversationTitlePattern.matcher(input);
            if (conversationTitleMatcher.find()) {
                conversationTitle = conversationTitleMatcher.group(1);
            }
            System.out.printf("Sending message \"%s\" in conversation <%s>.",message,conversationTitle);
            // BotActions.action.SEND_MESSAGE.doAction(message,conversationTitle);
            return;
        }

        // GET...
        int getIndex = commandTokensString.indexOf("get");
        if (getIndex != -1 && 
            commandPOS.get(getIndex).contains("VB")) {
            // Going to be getting something
            // Have a lot of methods where we get something so this block of code is going to branch a lot

            if (getIndex < commandTokensString.size()-1) {
                // Getting the lemma so we can ignore pluralizations
                String object = commandTokens.get(getIndex+1).lemma();

                switch (object) {
                    case "message": {
                        // Getting messages

                        int prepositionIndex = commandPOS.indexOf("IN");
                        if (prepositionIndex != -1) {
                            String conversationTitle = "";
                            Pattern conversationTitlePattern = Pattern.compile("<([^\"]*)>");
                            Matcher conversationTitleMatcher = conversationTitlePattern.matcher(input);
                            if (conversationTitleMatcher.find()) {
                                conversationTitle = conversationTitleMatcher.group(1);
                            }

                            if (!conversationTitle.equals("")) {
                                System.out.printf("Getting messages in conversation <%s>.",conversationTitle);
                                // BotActions.action.GET_MESSAGES_FROM_CONVERSATION.doAction(conversationTitle);
                                return;
                            }
                        }
                        
                    }
                    case "conversation": {
                        // Getting conversations
                    }
                }
            }
        }
        return;
    }
}