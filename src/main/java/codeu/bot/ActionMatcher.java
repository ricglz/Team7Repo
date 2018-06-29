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

    public ActionMatcher() {
        distance = new LevenshteinDistance();
        // CHANGE THIS
        botActions = new BotActions("test");
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

    public void matchAction(String input) throws IOException {
        // ActionMatcher ac = new ActionMatcher(input);

        this.input = input;
        HashSet<String> conversationTitles = ConversationStore.getInstance().getAllConversationTitles();
        HashSet<String> userNames = UserStore.getInstance().getAllUserNames();


        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,depparse");
        properties.setProperty("sutime.markTimeRanges", "true");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        pipeline.addAnnotator(new TimeAnnotator("sutime", properties));


        CoreDocument document = new CoreDocument(input);
        // document.annotation().set(CoreAnnotations.DocDateAnnotation.class,"2018-06-28");
        document.annotation().set(CoreAnnotations.DocDateAnnotation.class,(new Date()).toString());

        pipeline.annotate(document);

        if (document.sentences().size() != 1) {
            System.out.println("Raise error, one command per message please.");
            return;
        }


        // tokens returns a CoreLabel ArrayList
        // posTags returns a String ArrayList
        CoreSentence command = document.sentences().get(0);
        ArrayList<CoreLabel> commandTokens = (ArrayList<CoreLabel>) command.tokens();
        ArrayList<String> commandTokensLemmas = new ArrayList<String>();
        SemanticGraph dependencyParse = command.dependencyParse();
        ArrayList<String> nerTags = (ArrayList<String>) command.nerTags();
        List<CoreMap> timexAnnotationsAll = document.annotation().get(TimeAnnotations.TimexAnnotations.class);

        for (CoreLabel label : commandTokens) {
            commandTokensLemmas.add(label.value().toLowerCase());
        }

        ArrayList<String> commandPOS = (ArrayList<String>) command.posTags();

        System.out.println(dependencyParse);
        System.out.println(commandTokens);
        System.out.println(commandPOS);
        System.out.println(nerTags);
        System.out.println(timexAnnotationsAll);

        ////////////////////////////////////////////////////////////////////////
        // SETTING/UPDATING/CHANGING DESCRIPTION/SETTINGS]
        int setOrUpdateOrChangeIndex = getKeywordIndex(new String[] {"set","update","change"}, commandTokensLemmas);
        if (setOrUpdateOrChangeIndex != -1 && 
            commandPOS.get(setOrUpdateOrChangeIndex).contains("VB")) {
            // Going to be setting/updating/changing description or setting

            // If setting/updating/changing the description, must use keyword description
            int descIndex = commandTokensLemmas.indexOf("description");
            String description = "";
            if (descIndex != -1 && commandPOS.get(descIndex).contains("NN")) {
                // Find set description parameter
                Pattern pattern = Pattern.compile("\"([^\"]*)\"");
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    description = matcher.group(1);
                }
                System.out.printf("Setting description to %s.",description);
                BotActions.Action.SET_DESCRIPTION.doAction(description);
                return;
            }

            // TODO: Settings implementation
        }


        ////////////////////////////////////////////////////////////////////////
        // CREATE_CONVERSATION, params: String conversationTitle
        int createOrMakeIndex = getKeywordIndex(new String[] {"create","make"}, commandTokensLemmas);
        if (createOrMakeIndex != -1 && 
            commandPOS.get(createOrMakeIndex).contains("VB")) {
            // Going to be creating something
            // Right now we can only create conversations so don't need to
            // look for conversation keyword

            String conversationTitle = "";
            Pattern pattern = Pattern.compile("\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                conversationTitle = matcher.group(1);
                System.out.println("got here");
            }
            System.out.printf("Creating conversation %s.",conversationTitle);
            BotActions.Action.CREATE_CONVERSATION.doAction(conversationTitle);
            return;
        }


        ////////////////////////////////////////////////////////////////////////
        // SEND_MESSAGE, params: String message, String conversationTitle
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

            // Deprecated, attempting fuzzy matching of conversation titles
            // Pattern conversationTitlePattern = Pattern.compile("<([^\"]*)>");
            // Matcher conversationTitleMatcher = conversationTitlePattern.matcher(input);
            // if (conversationTitleMatcher.find()) {
            //     conversationTitle = conversationTitleMatcher.group(1);
            // }

            String conversationTitle = findFuzzyMatch(conversationTitles, 5);
            System.out.printf("Sending message \"%s\" in conversation <%s>.",message,conversationTitle);
            BotActions.Action.SEND_MESSAGE.doAction(message,conversationTitle);
            return;
        }

        ////////////////////////////////////////////////////////////////////////
        // GETTING THINGS
        int getOrFindOrDisplayorShoworGiveIndex = getKeywordIndex(new String[] {"get","find","display","show","give"}, commandTokensLemmas);
        if (getOrFindOrDisplayorShoworGiveIndex != -1 && 
            commandPOS.get(getOrFindOrDisplayorShoworGiveIndex).contains("VB")) {
            // Going to be getting something
            // Have a lot of methods where we get something so
            // this block of code is going to branch a lot

            // Getting the lemma so we can ignore pluralizations

            IndexedWord verbOfInterest = new IndexedWord(commandTokens.get(getOrFindOrDisplayorShoworGiveIndex));
            // Set<IndexedWord> childrenWithReln = dependencyParse.getChildrenWithReln(verbOfInterest);
            // Find object we're getting
            Set<IndexedWord> directObjects = dependencyParse.getChildrenWithReln(verbOfInterest,UniversalEnglishGrammaticalRelations.DIRECT_OBJECT);
            String directObject = "";
            if (!directObjects.isEmpty() && directObjects.size() == 1) {
                // Is it even possible to have multiple direct objects
                // from a single verb?
                // Going to go with no...

                directObject = (new ArrayList<>(directObjects)).get(0).lemma();
            } else {
                // RAISE ERROR, SORRY I'M NOT SURE WHAT YOU'RE LOOKING FOR.
                return;
            }

            switch (directObject) {
                case "message": {
                    // Getting messages

                    // GET_MY_MESSAGES
                    if (commandTokensLemmas.contains("my") || commandTokensLemmas.contains("I")) {
                        System.out.printf("Showing your messages.");
                        // // BotActions.action.GET_MY_MESSAGES.doAction();
                        return;
                    }

                    // GET_MESSAGES_BY_CREATION_TIME
                    if (!timexAnnotationsAll.isEmpty() && timexAnnotationsAll.size() == 1) {
                        CoreMap timexAnnotation = timexAnnotationsAll.get(0);
                        Temporal temporal = timexAnnotation.get(TimeExpression.Annotation.class).getTemporal();
                        // // BotActions.action.GET_MESSAGES_BY_CREATION_TIME.doAction(temporal);
                        // Need to decide restrictions on time
                        System.out.printf("Getting messages based on time criteria: %s.",temporal.toString());
                        return;
                    }

                    // GET_MESSAGES_LIKE_KEYWORD
                    int containOrAboutOrMentionIndex = getKeywordIndex(new String[] {"contain","about","mention"}, commandTokensLemmas);
                    if (containOrAboutOrMentionIndex != -1) {
                        String keyword = "";
                        Pattern keywordPattern = Pattern.compile("<([^\"]*)>");
                        Matcher keywordMatcher = keywordPattern.matcher(input);
                        if (keywordMatcher.find()) {
                            keyword = keywordMatcher.group(1);
                        } else {
                            // RAISE SORRY IDK
                            return;
                        }
                        
                        if (!keyword.equals("")) {
                            // // BotActions.action.GET_MESSAGES_LIKE_KEYWORD.doAction(keyword);
                            System.out.printf("Getting messages like keyword \"%s\".",keyword);
                            return;
                        } else {
                            // RAISE SORRY IDK
                            return;
                        }
                    }

                    // GET_MESSAGES_FROM_CONVERSATION
                    int fromOrInIndex = getKeywordIndex(new String[] {"in","from"}, commandTokensLemmas);
                    if (fromOrInIndex != -1) {
                        String conversationTitle = findFuzzyMatch(conversationTitles, 3);
                        if (!conversationTitle.equals("")) {
                            // // BotActions.action.GET_MESSAGES_FROM_CONVERSATION.doAction(conversationTitle);
                            System.out.printf("Getting messsages from %s.",conversationTitle);
                            return;
                        } else {
                            // RAISE SORRY IDK
                            return;
                        }
                    }
                    
                }
                case "conversation": {
                    // Getting conversations

                    // GET_CONVERSATIONS_BY_CREATION_TIME
                    if (!timexAnnotationsAll.isEmpty() && timexAnnotationsAll.size() == 1) {
                        CoreMap timexAnnotation = timexAnnotationsAll.get(0);
                        Temporal temporal = timexAnnotation.get(TimeExpression.Annotation.class).getTemporal();
                        // BotActions.action.GET_MESSAGES_BY_CREATION_TIME.doAction(temporal);
                        // System.out.printf("Getting conversation based on time criteria: %s.",temporal.toString());
                        return;
                    }

                    // GET_CONVERSATIONS_BY_AUTHOR
                    int madeOrCreateOrOwnIndex = getKeywordIndex(new String[] {"made","create","own"}, commandTokensLemmas);
                    if (madeOrCreateOrOwnIndex != -1) {
                        String author = findFuzzyMatch(userNames, 3);

                        if (!author.equals("")) {
                            // // BotActions.action.GET_MESSAGES_BY_AUTHOR.doAction(author);
                            System.out.printf("Getting conversations made by %s.",author);
                            return;
                        } else {
                            // RAISE SORRY IDK
                            return;
                        }
                    }

                    // GET_CONVERSATIONS_WITH_UNREAD_MESSAGES
                    int unreadOrRespondIndex = getKeywordIndex(new String[] {"unread","respond"}, commandTokensLemmas);
                    if (unreadOrRespondIndex != -1) {
                        // // BotActions.action.GET_CONVERSATION_WITH_UNREAD_MESSAGES.doAction();
                        System.out.printf("Getting conversations with unread messages.");
                        return;
                    }


                    // GET_CONVESATIONS_ABOUT_KEYWORD (encompass the like and content methods)
                    int containOrAboutOrMentionIndex = getKeywordIndex(new String[] {"contain","about","mention"}, commandTokensLemmas);
                    if (containOrAboutOrMentionIndex != -1) {
                        String keyword = "";
                        Pattern keywordPattern = Pattern.compile("<([^\"]*)>");
                        Matcher keywordMatcher = keywordPattern.matcher(input);
                        if (keywordMatcher.find()) {
                            keyword = keywordMatcher.group(1);
                        } else {
                            // RAISE SORRY IDK
                            return;
                        }
                        
                        if (!keyword.equals("")) {
                            // // BotActions.action.GET_CONVERSATIONS_KEYWORD.doAction(keyword);
                            System.out.printf("Getting conversations about keyword \"%s\".",keyword);
                            return;
                        } else {
                            // RAISE SORRY IDK
                            return;
                        }
                    }

                    // GET_ALL_CONVERSATIONS
                    int allIndex = getKeywordIndex(new String[] {"all"}, commandTokensLemmas);
                    if (allIndex != -1) {
                        // BotActions.action.GET_ALL_CONVERSATIONS.doAction();
                        System.out.printf("Getting all conversations.");
                        return;
                    }
                }
            }
        }

        // GET_CONVERSATION_SUMMARY
        int summarizeOrSummariseOrOverviewOrTLDRIndex = getKeywordIndex(
            new String[] {"summarize","summarise","overview","TLDR"},
            commandTokensLemmas);
        if (summarizeOrSummariseOrOverviewOrTLDRIndex != -1) {
            System.out.println("here");
            String conversationTitle = findFuzzyMatch(conversationTitles,3);
            if (!conversationTitle.equals("")) {
                // // BotActions.action.GET_CONVERSATION_SUMMARY.doAction(conversationTitle);
                System.out.printf("Getting a summary of %s.",conversationTitle);
            } else {
                // RAISE SORRY IDK
                return;
            }
        }

        return;
    }

}