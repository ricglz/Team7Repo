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
import codeu.model.store.persistence.PersistentDataStoreException;

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
    // Hard coded for now? Not sure if a store is going to be implemented for this
    public HashSet<String> settingNames = new HashSet<>(Arrays.asList(new String[] {"background color"}));


    public static final String[] SET_KEYWORDS = new String[] {"set","update","change"};
    public static final String[] CREATE_KEYWORDS = new String[] {"create","make"};
    public static final String[] GET_KEYWORDS = new String[] {"get","find","display","show","give"}; 
    public static final String[] SUMMARIZE_KEYWORDS = new String[] {"summarize","summarise","overview","TLDR"};
    public static final String[] UNREAD_KEYWORDS = new String[] {"unread","respond"};
    public static final String[] NAVIGATE_KEYWORDS = new String[] {"navigate","take"};
    public static final String[] HELP_KEYWORDS = new String[] {"help","tutorial","guide"};

    public static final String[] BACKGROUND_COLORS = new String[] {"white","black","grey","gray","red","orange","yellow","green","blue","indigo","violet","purple"};
    public static final HashSet<String> STATS = new HashSet<>(Arrays.asList(new String[] {"conversation count","user count","message count","most active user", "most active conversation"}));
    public static final String[] PAGES = new String[] {"activity","profile","conversation","setting"};

    public static final Pattern doubleQuotesPattern = Pattern.compile("\"([^\"]*)\"");
    public Matcher doubleQuotesMatcher;

    public boolean actionMatched;


    private static ActionMatcher actionMatcherInstance;
    public static ActionMatcher getInstance() {
        System.out.println("#getInstance 1");
        if (actionMatcherInstance == null) {
            System.out.println("#getInstance 2");
            actionMatcherInstance = new ActionMatcher();
        }
        System.out.println("#getInstance 3");
        return actionMatcherInstance;
    }

    private ActionMatcher() {
        System.out.println("#ActionMatcher 1");
        distance = new LevenshteinDistance();
        System.out.println("#ActionMatcher 2");
        properties = new Properties();
        System.out.println("#ActionMatcher 3");
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse");
        System.out.println("#ActionMatcher 4");
        properties.setProperty("sutime.markTimeRanges", "true");
        System.out.println("#ActionMatcher 5");
        //properties.setProperty("ner.useSUTime", "false");

        pipeline = new StanfordCoreNLP(properties);
        System.out.println("#ActionMatcher 6");
        pipeline.addAnnotator(new TimeAnnotator("sutime", properties));
        System.out.println("#ActionMatcher 7");
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
            index = lemmas.indexOf(keywords[keywordsIndex]);
            keywordsIndex++;
        }

        return index;
    }

    private void matchSet() throws IOException, PersistentDataStoreException {
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
                    System.out.printf("Setting description to %s.",description);
                    BotActions.Action.SET_DESCRIPTION.doAction(description);
                    actionMatched = true;
                    return;
                } else {
                    BotActions.Action.MISSING_PARAMETER.doAction();
                    actionMatched = true;
                    return;
                }
            }

            // // TODO: Settings implementation
            // // Set background_color to color
            // // From a set of colors
            // int backgroundOrColorOrColourIndex = getKeywordIndex(new String[] {"background","color","colour"}, commandTokensLemmas);
            // if (backgroundOrColorOrColourIndex != -1) {
            //     String color = "";
            //     int colorIndex = getKeywordIndex(BACKGROUND_COLORS, commandTokensLemmas);
            //     if (colorIndex != -1) {
            //         color = commandTokensLemmas.get(colorIndex);
            //         System.out.printf("Setting background color to %s.",color);
            //         BotActions.Action.SET_SETTING.doAction(color);
            //     }
            // }

            String setting = findFuzzyMatch(settingNames, 3);
            if (!setting.isEmpty()) {
                if (setting.contains("color") || setting.contains("colour")) {
                    // Going to be setting a color

                    String color = "";
                    int colorIndex = getKeywordIndex(BACKGROUND_COLORS, commandTokensLemmas);
                    if (colorIndex != -1) {
                        color = commandTokensLemmas.get(colorIndex);
                        System.out.printf("Setting background color to %s.",color);
                        BotActions.Action.SET_SETTING.doAction(setting, color);
                        actionMatched = true;
                        return;
                    } else {
                        BotActions.Action.MISSING_PARAMETER.doAction();
                        actionMatched = true;
                        return;
                    }
                }
            } else {

            }
            return;
        }
        return;
    }

    private void matchCreateConversation() throws IOException, PersistentDataStoreException {
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
                System.out.printf("Creating conversation %s.",conversationTitle);
                BotActions.Action.CREATE_CONVERSATION.doAction(conversationTitle);
                actionMatched = true;
                return;
            } else {
                BotActions.Action.MISSING_PARAMETER.doAction();
                actionMatched = true;
                return;
            }
        }
        return;
    }

    private void matchSendMessage() throws IOException, PersistentDataStoreException {
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
                String conversationTitle = findFuzzyMatch(conversationTitles, 5);
                System.out.printf("Sending message \"%s\" in conversation <%s>.",message,conversationTitle);
                BotActions.Action.SEND_MESSAGE.doAction(message,conversationTitle);
                actionMatched = true;
                return;
            } else {
                BotActions.Action.MISSING_PARAMETER.doAction();
                actionMatched = true;
                return;
            }
        }
        return;
    }

<<<<<<< HEAD
    public void matchGet() throws IOException {
        int getOrFindOrDisplayOrShoworGiveIndex = getKeywordIndex(GET_KEYWORDS, commandTokensLemmas);
        if (getOrFindOrDisplayOrShoworGiveIndex != -1 && 
            commandPOS.get(getOrFindOrDisplayOrShoworGiveIndex).contains("VB")) {
=======
    public void matchGet() throws IOException, PersistentDataStoreException {
        int getOrFindOrDisplayorShoworGiveIndex = getKeywordIndex(GET_KEYWORDS, commandTokensLemmas);
        if (getOrFindOrDisplayorShoworGiveIndex != -1 && 
            commandPOS.get(getOrFindOrDisplayorShoworGiveIndex).contains("VB")) {
>>>>>>> fe9b51c2b4583183298c35970812562df96c0d30

            // Getting the lemma so we can ignore pluralizations
            IndexedWord verbOfInterest = new IndexedWord(commandTokens.get(getOrFindOrDisplayOrShoworGiveIndex));
        
            // Find object we're getting
            Set<IndexedWord> directObjects = dependencyParse.getChildrenWithReln(verbOfInterest,UniversalEnglishGrammaticalRelations.DIRECT_OBJECT);
            String directObject = "";
            if (!directObjects.isEmpty() && directObjects.size() == 1) {
                directObject = (new ArrayList<>(directObjects)).get(0).lemma();
            } else {
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

            if (actionMatched) {
                return;
            } else {
                String setting = findFuzzyMatch(settingNames, 3);
                if (!setting.isEmpty()) {
                    System.out.printf("Getting %s.",setting);
                    BotActions.Action.GET_SETTING.doAction(setting);
                    actionMatched = true;
                    return;
                }

                if (commandTokensLemmas.contains("all") && commandTokensLemmas.contains("setting")) {
                    System.out.println("Getting all settings.");
                    BotActions.Action.GET_SETTINGS.doAction();
                    actionMatched = true;
                    return;
                }

                // STATISTICS
                String statistic = findFuzzyMatch(STATS, 10);
                if (!statistic.isEmpty()) {
                    System.out.printf("Getting %s.",statistic);
                    BotActions.Action.GET_STAT.doAction(statistic);
                    actionMatched = true;
                    return;
                }

                if (commandTokensLemmas.contains("all") && 
                (commandTokensLemmas.contains("statistic") || commandTokensLemmas.contains("stat"))) {
                    System.out.println("Getting all settings.");
                    BotActions.Action.GET_STATS.doAction();
                    actionMatched = true;
                    return;
                }

            }
        }
        return;
    }

    public void matchGetMessages() throws IOException {
        // GET_MY_MESSAGES
        if (commandTokensLemmas.contains("my") || commandTokensLemmas.contains("I")) {
            System.out.printf("Showing your messages.");
            BotActions.Action.GET_MY_MESSAGES.doAction();
            actionMatched = true;
            return;
        }

        // GET_MESSAGES_BY_CREATION_TIME
        if (!timexAnnotationsAll.isEmpty() && timexAnnotationsAll.size() == 1) {
            CoreMap timexAnnotation = timexAnnotationsAll.get(0);
            Temporal temporal = timexAnnotation.get(TimeExpression.Annotation.class).getTemporal();
            System.out.printf("Getting messages based on time criteria: %s.",temporal.toString());
            BotActions.Action.GET_MESSAGES_BY_CREATION_TIME.doAction(temporal);
            actionMatched = true;
            return;
        }

        // GET_MESSAGES_LIKE_KEYWORD
        int containOrAboutOrMentionIndex = getKeywordIndex(new String[] {"contain","about","mention"}, commandTokensLemmas);
        if (containOrAboutOrMentionIndex != -1) {
            String keyword = "";
            Matcher keywordMatcher = doubleQuotesPattern.matcher(input);
            if (keywordMatcher.find()) {
                keyword = keywordMatcher.group(1);
                System.out.printf("Getting messages like keyword \"%s\".",keyword);
                BotActions.Action.GET_MESSAGES_LIKE_KEYWORD.doAction(keyword);
                actionMatched = true;
                return;
            } else {
                BotActions.Action.MISSING_PARAMETER.doAction();
                actionMatched = true;
                return;
            }
        }

        // GET_MESSAGES_FROM_CONVERSATION
        int fromOrInIndex = getKeywordIndex(new String[] {"in","from"}, commandTokensLemmas);
        if (fromOrInIndex != -1) {
            String conversationTitle = findFuzzyMatch(conversationTitles, 3);
            if (!conversationTitle.isEmpty()) {
                System.out.printf("Getting messsages from %s.",conversationTitle);
                BotActions.Action.GET_MESSAGES_FROM_CONVERSATION.doAction(conversationTitle);
                actionMatched = true;
                return;
            }
        }
        return;
    }

    public void matchGetConversations() throws IOException, PersistentDataStoreException {

        // GET_CONVERSATIONS_BY_CREATION_TIME
        if (!timexAnnotationsAll.isEmpty() && timexAnnotationsAll.size() == 1) {
            CoreMap timexAnnotation = timexAnnotationsAll.get(0);
            Temporal temporal = timexAnnotation.get(TimeExpression.Annotation.class).getTemporal();
            System.out.printf("Getting conversation based on time criteria: %s.",temporal.toString());
            BotActions.Action.GET_MESSAGES_BY_CREATION_TIME.doAction(temporal);
            actionMatched = true;
            return;
        }

        // GET_CONVERSATIONS_BY_AUTHOR
        int madeOrCreateOrOwnIndex = getKeywordIndex(CREATE_KEYWORDS, commandTokensLemmas);
        if (madeOrCreateOrOwnIndex != -1) {
            String author = findFuzzyMatch(userNames, 3);
            if (!author.isEmpty()) {
                System.out.printf("Getting conversations made by %s.",author);
                BotActions.Action.GET_MESSAGES_BY_AUTHOR.doAction(author);
                actionMatched = true;
                return;
            }
        }

        // GET_CONVERSATIONS_WITH_UNREAD_MESSAGES
        int unreadOrRespondIndex = getKeywordIndex(UNREAD_KEYWORDS, commandTokensLemmas);
        if (unreadOrRespondIndex != -1) {
            System.out.printf("Getting conversations with unread messages.");
            BotActions.Action.GET_CONVERSATIONS_WITH_UNREAD_MESSAGES.doAction();
            actionMatched = true;
            return;
        }


        // GET_CONVERSATIONS_ABOUT_KEYWORD (encompass the like and content methods)
        int containOrAboutOrMentionIndex = getKeywordIndex(new String[] {"contain","about","mention"}, commandTokensLemmas);
        if (containOrAboutOrMentionIndex != -1) {
            String keyword = "";
            Matcher keywordMatcher = doubleQuotesPattern.matcher(input);
            if (keywordMatcher.find()) {
                keyword = keywordMatcher.group(1);
                System.out.printf("Getting conversations about keyword \"%s\".",keyword);
                BotActions.Action.GET_CONVERSATIONS_ABOUT_KEYWORD.doAction(keyword);
                actionMatched = true;
                return;
            } else {
                BotActions.Action.MISSING_PARAMETER.doAction();
                actionMatched = true;
                return;
            }
        }

        // GET_ALL_CONVERSATIONS
        int allIndex = getKeywordIndex(new String[] {"all"}, commandTokensLemmas);
        if (allIndex != -1) {
            System.out.printf("Getting all conversations.");
            BotActions.Action.GET_ALL_CONVERSATIONS.doAction();
            actionMatched = true;
            return;
        }
    }

    public void matchSummarize() throws IOException {
        int summarizeOrSummariseOrOverviewOrTLDRIndex = getKeywordIndex(
            SUMMARIZE_KEYWORDS,
            commandTokensLemmas);
        if (summarizeOrSummariseOrOverviewOrTLDRIndex != -1) {
            String conversationTitle = findFuzzyMatch(conversationTitles,3);
            if (!conversationTitle.isEmpty()) {
                System.out.printf("Getting a summary of %s.",conversationTitle);
                BotActions.Action.GET_CONVERSATION_SUMMARY.doAction(conversationTitle);
                actionMatched = true;
                return;
            } else {
                BotActions.Action.MISSING_PARAMETER.doAction();
                actionMatched = true;
                return;
            }
        }
        return;
    }

<<<<<<< HEAD
    public void matchNavigate() throws IOException {
        int navigateOrTakeIndex = getKeywordIndex(NAVIGATE_KEYWORDS, commandTokensLemmas);
        if (navigateOrTakeIndex != -1) {
            
            String conversation = findFuzzyMatch(conversationTitles, 3);
            if (!conversation.isEmpty()) {
                System.out.printf("Taking you to %s.",conversation);
                BotActions.Action.NAVIGATE_TO_CONVERSATION.doAction(conversation);
                actionMatched = true;
                return;
            }

            int pageIndex = getKeywordIndex(PAGES, commandTokensLemmas);
            if (pageIndex != -1) {
                String page = commandTokensLemmas.get(pageIndex);
                System.out.printf("Taking you to %s page.",page);
                BotActions.Action.NAVIGATE.doAction(page);
                actionMatched = true;
                return;
            }
        }
        return;
    }

    public void matchHelp() throws IOException {
        if (getKeywordIndex(HELP_KEYWORDS,commandTokensLemmas != -1) {
            BotActions.Actions.GET_HELP.doAction();
            actionMatched = true;
            return;
        }
        return;
    }

<<<<<<< HEAD
    public void matchAction(String input) throws IOException {
        conversationTitles = ConversationStore.getInstance().getAllConversationTitles();
        userNames = UserStore.getInstance().getAllUserNames();
=======
    public void matchAction(String input, String username) throws IOException {
=======
    public void matchAction(String input, String username) throws IOException, PersistentDataStoreException {
>>>>>>> fe9b51c2b4583183298c35970812562df96c0d30
        BotActions botActions = new BotActions(username);

        ConversationStore.getInstance().getAllConversationTitles();
        UserStore.getInstance().getAllUserNames();
>>>>>>> 34d573aa4a11454e70f897ff323f8bf019bdf038

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
        if (!actionMatched) {
            matchSendMessage();
            if (!actionMatched) {
                matchCreateConversation();
                if (!actionMatched) {
                    matchGet();
                    if (!actionMatched) {
                        matchSummarize();
                        if (!actionMatched) {
                            matchNavigate();
                            if (!actionMatched) {
                                matchHelp();
                            }
                        }
                    }
                }
            }
        }

        if (!actionMatched) {
            BotActions.Action.NO_IDEA_PLACEHOLDER.doAction();
        } else {
            return;
        }
    }

}