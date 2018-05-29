// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Servlet class responsible for the chat page. */
public class ChatServlet extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Set up state for handling chat requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
  }

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method
   * for use by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * This function fires when a user navigates to the chat page. It gets the conversation title from
   * the URL, finds the corresponding Conversation, and fetches the messages in that Conversation.
   * It then forwards to chat.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String conversationTitle = requestUrl.substring("/chat/".length());

    Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);
    if (conversation == null) {
      // couldn't find conversation, redirect to conversation list
      System.out.println("Conversation was null: " + conversationTitle);
      response.sendRedirect("/conversations");
      return;
    }

    UUID conversationId = conversation.getId();

    List<Message> messages = messageStore.getMessagesInConversation(conversationId);

    request.setAttribute("conversation", conversation);
    request.setAttribute("messages", messages);
    request.getRequestDispatcher("/WEB-INF/view/chat.jsp").forward(request, response);
  }

  /**
   * This function fires when a user submits the form on the chat page. It gets the logged-in
   * username from the session, the conversation title from the URL, and the chat message from the
   * submitted form data. It creates a new Message from that data, adds it to the model, and then
   * redirects back to the chat page.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");
    if (username == null) {
      // user is not logged in, don't let them add a message
      response.sendRedirect("/login");
      return;
    }

    User user = userStore.getUser(username);
    if (user == null) {
      // user was not found, don't let them add a message
      response.sendRedirect("/login");
      return;
    }

    String requestUrl = request.getRequestURI();
    String conversationTitle = requestUrl.substring("/chat/".length());

    Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);
    if (conversation == null) {
      // couldn't find conversation, redirect to conversation list
      response.sendRedirect("/conversations");
      return;
    }

    String messageContent = request.getParameter("message");

    // this removes any HTML from the message content
    String cleanedMessageContent = Jsoup.clean(messageContent, Whitelist.none());
    // converts BBCode bold sytax to HTML
    cleanedMessageContent = processString(cleanedMessageContent, Styling.BOLD);
    // converts BBCode italics sytax to HTML
    cleanedMessageContent = processString(cleanedMessageContent, Styling.ITALICS);

    Message message =
        new Message(
            UUID.randomUUID(),
            conversation.getId(),
            user.getId(),
            cleanedMessageContent,
            Instant.now());

    messageStore.addMessage(message);

    // redirect to a GET request
    response.sendRedirect("/chat/" + conversationTitle);
  }

  // Defines supported styling of our BBCode processor.
  private enum Styling {
      BOLD ("b"),
      ITALICS ("i");

      private final String tag;

      Styling(String tag) {
          this.tag = tag;
      }

      public String getOpenTag() {
          return String.format("[%s]", tag);
      }

      public String getCloseTag() {
          return String.format("[/%s]", tag);
      }

      public String getOpenTagRegex() {
          return Pattern.quote(getOpenTag());
      }

      public String getCloseTagRegex() {
          return Pattern.quote(getCloseTag());
      }

      public String getConvertedOpenTag() {
          return String.format("<%s>", tag);
      }

      public String getConvertedCloseTag() {
          return String.format("</%s>", tag);
      }
  }

  /**
   * A recursive method for converting a string from BBO to HTML given the desired styling.
   * This method is recursive because we want to make sure we are processing pairs
   * of tags (so we dont process an open tag that has no close or vice versa).
   *
   * -- ex: bolding text --
   * input: "[b] I am writing [b] bold text [/b]"
   * output: "<b> I am writing [b] bold text </b>"
   */
  private String processString(String messageToProcess, Styling styling) {
      int openIndex = messageToProcess.indexOf(styling.getOpenTag());
      int closeIndex = messageToProcess.indexOf(styling.getCloseTag());

      // If we do not have both an opening and a closing tag we do not want to process the string
      // since we want to make sure we are replacing both the opening and closing tag with html.
      // This is also our base conditions for this recursive function. We know we are done
      // when we can no longer replace a tag pairing.
      if (openIndex == -1 || closeIndex == -1) {
          return messageToProcess;
      } else {
          messageToProcess = messageToProcess.replaceFirst(styling.getOpenTagRegex(), styling.getConvertedOpenTag());
          messageToProcess = messageToProcess.replaceFirst(styling.getCloseTagRegex(), styling.getConvertedCloseTag());
          return processString(messageToProcess, styling);
      }
  }
}
