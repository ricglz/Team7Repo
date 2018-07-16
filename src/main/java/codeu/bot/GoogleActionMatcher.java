package codeu.bot;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;

public class GoogleActionMatcher {

    private static GoogleActionMatcher actionMatcherInstance;
    public static GoogleActionMatcher getInstance() {
        System.out.println("#getInstance 1");
        if (actionMatcherInstance == null) {
            System.out.println("#getInstance 2");
            actionMatcherInstance = new GoogleActionMatcher();
        }
        System.out.println("#getInstance 3");
        return actionMatcherInstance;
    }

    private GoogleActionMatcher() {

    }

    public String matchAction(String input, String username) {
      // Instantiates a client
      try (LanguageServiceClient language = LanguageServiceClient.create()) {
        // The text to analyze
        //String text = "Hello, world!";
        Document doc = Document.newBuilder()
            .setContent(input).setType(Type.PLAIN_TEXT).build();

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
