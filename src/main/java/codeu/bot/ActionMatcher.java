package codeu.bot;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.naturalli.ClauseSplitterSearchProblem.Action;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.simple.*;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

import codeu.bot.BotActions;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class ActionMatcher {

    public static String input;
    public static LevenshteinDistance distance;

    public BotActions botActions;
    public Properties properties;
    public StanfordCoreNLP pipeline;
    public CoreDocument document;
    public CoreSentence command;
    public ArrayList<CoreLabel> commandTokens;
    public ArrayList<String> commandTokensLemmas;
    public ArrayList<String> commandPOS;
    public SemanticGraph dependencyParse;
    public ArrayList<String> nerTags;
    public List<CoreMap> timexAnnotationsAll;

    public HashSet<String> conversationTitles;
    public HashSet<String> userNames;

    public static final String[] SET_KEYWORDS = new String[] {"set","update","change"};
    public static final String[] CREATE_KEYWORDS = new String[] {"create","make"};
    public static final String[] GET_KEYWORDS = new String[] {"get","find","display","show","give"}; 
    public static final String[] SUMMARIZE_KEYWORDS = new String[] {"summarize","summarise","overview","TLDR"};
    public static final String[] UNREAD_KEYWORDS = new String[] {"unread","respond"};

    public static final Pattern doubleQuotesPattern = Pattern.compile("\"([^\"]*)\"");
    public Matcher doubleQuotesMatcher;


    public ActionMatcher() {
        distance = new LevenshteinDistance();

        properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse");
        properties.setProperty("sutime.markTimeRanges", "true");

        pipeline = new StanfordCoreNLP(properties);
        pipeline.addAnnotator(new TimeAnnotator("sutime", properties));
    }

    public String findFuzzyMatch(HashSet<String> set, int distanceThreshold) {
        String ret = "";
        ArrayList<Integer> spaceIndices = new ArrayList<Integer>();

        int findIndex = input.indexOf(" ", 0);
        while (findIndex >= 0) {
            spaceIndices.add(findIndex);
            findIndex = input.indexOf(" ", findIndex+1);
        }

        outerloop:
        for (String element : set) {
            for (Integer spaceIndex : spaceIndices) {
                if (spaceIndex.intValue() + element.length() >= input.length()) {
                    break;
                } else {
                    if (distance.apply(input.substring(spaceIndex, spaceIndex + element.length()), element).intValue() < distanceThreshold) {
                        ret = element;
                        break outerloop;
                    }
                }
            }
        }

        return ret;
    }

    public int getKeywordIndex(String[] keywords, ArrayList<String> lemmas) {
        int index = -1;
        int keywordsIndex = 0;

        while (index < 0 && keywordsIndex < keywords.length) {
            System.out.println("loop");
            index = lemmas.indexOf(keywords[keywordsIndex]);
            keywordsIndex++;
        }

        return index;
    }

    private void matchSet() throws IOException {
        int setOrUpdateOrChangeIndex = getKeywordIndex(SET_KEYWORDS, commandTokensLemmas);
        if (setOrUpdateOrChangeIndex != -1 && 
            commandPOS.get(setOrUpdateOrChangeIndex).contains("VB")) {

            int descIndex = commandTokensLemmas.indexOf("description");
            String description = "";
            if (descIndex != -1 && commandPOS.get(descIndex).contains("NN")) {
                // Find set description parameter
                Matcher matcher = doubleQuotesPattern.matcher(input);
                if (matcher.find()) {
                    description = matcher.group(1);
                }
                System.out.printf("Setting description to %s.",description);
                BotActions.Action.SET_DESCRIPTION.doAction(description);
                return;
            }

            // TODO: Settings implementation
        }
    }

    private void matchCreateConversation() throws IOException {
        int createOrMakeIndex = getKeywordIndex(CREATE_KEYWORDS, commandTokensLemmas);
        if (createOrMakeIndex != -1 && 
            commandPOS.get(createOrMakeIndex).contains("VB")) {
            // Going to be creating something
            // Right now we can only create conversations so don't need to
            // look for conversation keyword

            String conversationTitle = "";
            Matcher matcher = doubleQuotesPattern.matcher(input);
            if (matcher.find()) {
                conversationTitle = matcher.group(1);
                System.out.println("got here");
            }
            System.out.printf("Creating conversation %s.",conversationTitle);
            BotActions.Action.CREATE_CONVERSATION.doAction(conversationTitle);
            return;
        }
    }

    private void matchSendMessage() throws IOException {
        int sendIndex = commandTokensLemmas.indexOf("send");
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

            String conversationTitle = findFuzzyMatch(conversationTitles, 5);
            System.out.printf("Sending message \"%s\" in conversation <%s>.",message,conversationTitle);
            BotActions.Action.SEND_MESSAGE.doAction(message,conversationTitle);
            return;
        }
    }

    public void matchGet() throws IOException {
        int getOrFindOrDisplayorShoworGiveIndex = getKeywordIndex(GET_KEYWORDS, commandTokensLemmas);
        if (getOrFindOrDisplayorShoworGiveIndex != -1 && 
            commandPOS.get(getOrFindOrDisplayorShoworGiveIndex).contains("VB")) {

            // Getting the lemma so we can ignore pluralizations
            IndexedWord verbOfInterest = new IndexedWord(commandTokens.get(getOrFindOrDisplayorShoworGiveIndex));
        
            // Find object we're getting
            Set<IndexedWord> directObjects = dependencyParse.getChildrenWithReln(verbOfInterest,UniversalEnglishGrammaticalRelations.DIRECT_OBJECT);
            String directObject = "";
            if (!directObjects.isEmpty() && directObjects.size() == 1) {
                directObject = (new ArrayList<>(directObjects)).get(0).lemma();
            } else {
                // Make a dummy call to SET_DESCRIPTION
                BotActions.Action.SET_DESCRIPTION.doAction();
                return;
            }

            switch (directObject) {
                case "message": {
                    matchGetMessages();
                }
                case "conversation": {
                    matchGetConversations();
                }
            }
        }
    }

    public void matchGetMessages() throws IOException {
        // GET_MY_MESSAGES
        if (commandTokensLemmas.contains("my") || commandTokensLemmas.contains("I")) {
            System.out.printf("Showing your messages.");
            // BotActions.Action.GET_MY_MESSAGES.doAction();
            return;
        }

        // GET_MESSAGES_BY_CREATION_TIME
        if (!timexAnnotationsAll.isEmpty() && timexAnnotationsAll.size() == 1) {
            CoreMap timexAnnotation = timexAnnotationsAll.get(0);
            Temporal temporal = timexAnnotation.get(TimeExpression.Annotation.class).getTemporal();
            // BotActions.Action.GET_MESSAGES_BY_CREATION_TIME.doAction(temporal);
            // Need to decide restrictions on time
            System.out.printf("Getting messages based on time criteria: %s.",temporal.toString());
            return;
        }

        // GET_MESSAGES_LIKE_KEYWORD
        int containOrAboutOrMentionIndex = getKeywordIndex(new String[] {"contain","about","mention"}, commandTokensLemmas);
        if (containOrAboutOrMentionIndex != -1) {
            String keyword = "";
            Matcher keywordMatcher = doubleQuotesPattern.matcher(input);
            if (keywordMatcher.find()) {
                keyword = keywordMatcher.group(1);
            }
            // else {
            //     // RAISE SORRY IDK
            //     return;
            // }
            
            // if (!keyword.equals("")) {
            // BotActions.Action.GET_MESSAGES_LIKE_KEYWORD.doAction(keyword);
            System.out.printf("Getting messages like keyword \"%s\".",keyword);
            return;
            // }else {
            //     // RAISE SORRY IDK
            //     return;
            // }
        }

        // GET_MESSAGES_FROM_CONVERSATION
        int fromOrInIndex = getKeywordIndex(new String[] {"in","from"}, commandTokensLemmas);
        if (fromOrInIndex != -1) {
            String conversationTitle = findFuzzyMatch(conversationTitles, 3);
            // if (!conversationTitle.equals("")) {
            // BotActions.Action.GET_MESSAGES_FROM_CONVERSATION.doAction(conversationTitle);
            System.out.printf("Getting messsages from %s.",conversationTitle);
            return;
            // } else {
            //     // RAISE SORRY IDK
            //     return;
            // }
        }
    }

    public void matchGetConversations() throws IOException {

        // GET_CONVERSATIONS_BY_CREATION_TIME
        if (!timexAnnotationsAll.isEmpty() && timexAnnotationsAll.size() == 1) {
            CoreMap timexAnnotation = timexAnnotationsAll.get(0);
            Temporal temporal = timexAnnotation.get(TimeExpression.Annotation.class).getTemporal();
            BotActions.Action.GET_MESSAGES_BY_CREATION_TIME.doAction(temporal);
            System.out.printf("Getting conversation based on time criteria: %s.",temporal.toString());
            return;
        }

        // GET_CONVERSATIONS_BY_AUTHOR
        int madeOrCreateOrOwnIndex = getKeywordIndex(CREATE_KEYWORDS, commandTokensLemmas);
        if (madeOrCreateOrOwnIndex != -1) {
            String author = findFuzzyMatch(userNames, 3);

            // if (!author.equals("")) {
                // BotActions.Action.GET_MESSAGES_BY_AUTHOR.doAction(author);
            System.out.printf("Getting conversations made by %s.",author);
            return;
            // } else {
            //     // RAISE SORRY IDK
            //     return;
            // }
        }

        // GET_CONVERSATIONS_WITH_UNREAD_MESSAGES
        int unreadOrRespondIndex = getKeywordIndex(UNREAD_KEYWORDS, commandTokensLemmas);
        if (unreadOrRespondIndex != -1) {
            // BotActions.Action.GET_CONVERSATION_WITH_UNREAD_MESSAGES.doAction();
            System.out.printf("Getting conversations with unread messages.");
            return;
        }


        // GET_CONVESATIONS_ABOUT_KEYWORD (encompass the like and content methods)
        int containOrAboutOrMentionIndex = getKeywordIndex(new String[] {"contain","about","mention"}, commandTokensLemmas);
        if (containOrAboutOrMentionIndex != -1) {
            String keyword = "";
            Matcher keywordMatcher = doubleQuotesPattern.matcher(input);
            if (keywordMatcher.find()) {
                keyword = keywordMatcher.group(1);
            }
            // BotActions.Action.GET_CONVERSATIONS_KEYWORD.doAction(keyword);
            System.out.printf("Getting conversations about keyword \"%s\".",keyword);
            return;
        }

        // GET_ALL_CONVERSATIONS
        int allIndex = getKeywordIndex(new String[] {"all"}, commandTokensLemmas);
        if (allIndex != -1) {
            // BotActions.Action.GET_ALL_CONVERSATIONS.doAction();
            System.out.printf("Getting all conversations.");
            return;
        }
    }

    public void matchSummarize() throws IOException {
        int summarizeOrSummariseOrOverviewOrTLDRIndex = getKeywordIndex(
            SUMMARIZE_KEYWORDS,
            commandTokensLemmas);
        if (summarizeOrSummariseOrOverviewOrTLDRIndex != -1) {
            String conversationTitle = findFuzzyMatch(conversationTitles,3);
            System.out.printf("Getting a summary of %s.",conversationTitle);
            // BotActions.Action.GET_CONVERSATION_SUMMARY.doAction(conversationTitle);
        }
    }

    public void matchAction(String input) throws IOException {
        ConversationStore.getInstance().getAllConversationTitles();
        UserStore.getInstance().getAllUserNames();

        this.input = input;

        document = new CoreDocument(input);
        document.annotation().set(CoreAnnotations.DocDateAnnotation.class,(new Date()).toString());

        pipeline.annotate(document);

        if (document.sentences().size() != 1) {
            System.out.println("Raise error, one command per message please.");
            return;
        }

        command = document.sentences().get(0);
        commandTokens = (ArrayList<CoreLabel>) command.tokens();
        commandTokensLemmas = new ArrayList<String>();
        dependencyParse = command.dependencyParse();
        nerTags = (ArrayList<String>) command.nerTags();
        timexAnnotationsAll = document.annotation().get(TimeAnnotations.TimexAnnotations.class);

        for (CoreLabel label : commandTokens) {
            commandTokensLemmas.add(label.value().toLowerCase());
        }

        commandPOS = (ArrayList<String>) command.posTags();

        System.out.println(dependencyParse);
        System.out.println(commandTokens);
        System.out.println(commandPOS);
        System.out.println(nerTags);
        System.out.println(timexAnnotationsAll);

        matchSet();
        matchSendMessage();
        matchCreateConversation();
        matchGet();
        matchSummarize();
        return;
    }

}