package codeu.bot;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;
import codeu.bot.BotActions;

public class GoogleActionMatcher {
    private static boolean matchSet(
        LanguageServiceClient language,
        Document doc,
        String input) {
      System.out.println("#matchSet");
      return false;
    }

    private static boolean matchCreate(
        LanguageServiceClient language,
        Document doc,
        String input) {
      System.out.println("#matchCreate");
      return false;
    }

    private static boolean matchGet(
        LanguageServiceClient language,
        Document doc,
        String input) {
      System.out.println("#matchGet");
      return false;
    }

    private static boolean matchSummarize(
        LanguageServiceClient language,
        Document doc,
        String input) {
      System.out.println("#matchSummarize");
      return false;
    }

    private static boolean matchNavigate(
        LanguageServiceClient language,
        Document doc,
        String input) {
      System.out.println("#matchNavigate");
      return false;
    }

    private static boolean matchHelp(
        LanguageServiceClient language,
        Document doc,
        String input) {
      System.out.println("#matchHelp");
      return false;
    }

    // THE RETURN VALUE HERE IS JUST FOR DEBUGGING. THIS SHOULD BE A VOID METHOD.
    public static String matchAction(String input, String username) {
      new BotActions(username);
      boolean foundMatch = false;

      // Instantiates a client
      try (LanguageServiceClient language = LanguageServiceClient.create()) {
        Document doc = Document.newBuilder()
            .setContent(input).setType(Type.PLAIN_TEXT).build();

        if (matchSet(language, doc, username)) {
          return "Matched on SET";
        } else if (matchCreate(language, doc, username)) {
          return "Matched on CREATE";
        } else if (matchGet(language, doc, username)) {
          return "Matched on GET";
        } else if (matchSummarize(language, doc, username)) {
          return "Matched on SUMMARIZE";
        } else if (matchNavigate(language, doc, username)) {
          return "Matched on NAVIGATE";
        } else if (matchHelp(language, doc, username)) {
          return "Matched on HELP";
        }

        BotActions.Action.NOT_FOUND.doAction();
        //return "NOT MATCHED";

        // Detects the sentiment of the text
        Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

        System.out.printf("Text: %s%n", input);
        System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
        return String.format("Sentiment: %s, %s%n", sentiment.getScore(), sentiment.getMagnitude());
      } catch (IOException e) {
        System.out.println(" ");
        System.out.println(e.getMessage());
        System.out.println(" ");
      }
      return "";
    }
}
