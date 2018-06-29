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

package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import codeu.model.data.User;
import codeu.model.data.Conversation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class MessageStore {

  /** Singleton instance of MessageStore. */
  private static MessageStore instance;

  /**
   * Returns the singleton instance of MessageStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static MessageStore getInstance() {
    if (instance == null) {
      instance = new MessageStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static MessageStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new MessageStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Messages from and saving Messages to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Messages. */
  private List<Message> messages;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private MessageStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    messages = new ArrayList<>();
  }

  /** Add a new message to the current set of messages known to the application. */
  public void addMessage(Message message) {
    messages.add(message);
    persistentStorageAgent.writeThrough(message);
  }

  /** Find and return the Message with the given UUID */
  public Message getMessage(UUID id) {
    for (Message message : messages) {
      if (message.getId().equals(id)) {
        return message;
      }
    }
    return null;
  }

  /** Access the current set of Messages within the given Conversation. */
  public List<Message> getMessagesInConversation(UUID conversationId) {

    List<Message> messagesInConversation = new ArrayList<>();

    for (Message message : messages) {
      if (message.getConversationId().equals(conversationId)) {
        messagesInConversation.add(message);
      }
    }

    return messagesInConversation;
  }

  /** Compares the date of 2 Instant times, by truncating the Intants to days. */
  private boolean sameDate(Instant time1, Instant time2) {
    time1 = time1.truncatedTo(ChronoUnit.DAYS);
    time2 = time2.truncatedTo(ChronoUnit.DAYS);
    return time1.equals(time2);
  }

  public List<Message> getMessagesInTime(Instant time) {
    List<Message> messagesInTime = new ArrayList<>();

    for (Message message : messages) {
      if (sameDate(time, message.getCreationTime())) {
        messagesInTime.add(message);
      }
    }

    return messagesInTime;
  }

  public List<Message> getMessagesByAuthor(UUID author) {
    List<Message> messagesByAuthor = new ArrayList<>();

    for (Message message : messages) {
      if (message.getAuthorId().equals(author)) {
        messagesByAuthor.add(message);
      }
    }

    return messagesByAuthor;
  }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  /** Returns the count of total messages stored */
  public int getMessagesCount() {
    return messages.size();
  }

  /** Returns the longest message */
  public Message getLongestMessage() {
    sort();
    return messages.get(messages.size()-1);
  }

  /** Method to sort the messages contained in the list */
  public void sort() {
    messages.sort(Message.messageComparator);
  }
}
