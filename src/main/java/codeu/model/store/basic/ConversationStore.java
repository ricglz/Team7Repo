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

import codeu.controller.BotServlet;
import codeu.model.data.Conversation;
import codeu.model.store.persistence.PersistentStorageAgent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class ConversationStore {

  /** Singleton instance of ConversationStore. */
  private static ConversationStore instance;

  /**
   * Returns the singleton instance of ConversationStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static ConversationStore getInstance() {
    if (instance == null) {
      instance = new ConversationStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static ConversationStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new ConversationStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Conversations from and saving Conversations
   * to Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Conversations. */
  private List<Conversation> conversations;

  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private ConversationStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    conversations = new ArrayList<>();
  }

  /** Access the current set of conversations known to the application. */
  public List<Conversation> getAllConversations() {
    return new ArrayList<Conversation>(conversations);
  }

  public HashSet<String> getAllConversationTitles() {
    HashSet<String> titles = getAllConversations().stream().map(
      x -> x.getTitle()).collect(Collectors.toCollection(HashSet::new));

    return titles;
  }

  /** Compares the date of 2 Instant times, by truncating the Intants to days. */
  private boolean sameDate(Instant time1, Instant time2) {
    time1 = time1.truncatedTo(ChronoUnit.DAYS);
    time2 = time2.truncatedTo(ChronoUnit.DAYS);
    return time1.equals(time2);
  }

  public List<Conversation> getConversationsByTime(Instant time) {
    List<Conversation> conversationsInTime = new ArrayList<>();
    for (Conversation conversation : conversations) {
      if (sameDate(time, conversation.getCreationTime())) {
        conversationsInTime.add(conversation);
      }
    }
    return conversationsInTime;
  }

  public List<Conversation> getConversationsByAuthor(UUID ownerId) {
    List<Conversation> conversationsInTime = new ArrayList<>();
    for (Conversation conversation : conversations) {
      if (conversation.getOwnerId().equals(ownerId)) {
        conversationsInTime.add(conversation);
      }
    }
    return conversationsInTime;
  }

  /** Add a new conversation to the current set of conversations known to the application. */
  public void addConversation(Conversation conversation) {
    conversations.add(conversation);
    persistentStorageAgent.writeThrough(conversation);
  }

  public void updateConversation(Conversation conversation) {
    persistentStorageAgent.writeThrough(conversation);
  }

  /** Check whether a Conversation title is already known to the application. */
  public boolean isTitleTaken(String title) {
    // This approach will be pretty slow if we have many Conversations.
    for (Conversation conversation : conversations) {
      if (conversation.getTitle().equals(title)) {
        return true;
      }
    }
    return false;
  }

  /** Find and return the Conversation with the given UUID. */
  public Conversation getConversationWithUUID(UUID id) {
    for (Conversation conversation : conversations) {
      if (conversation.getId().equals(id)) {
        return conversation;
      }
    }
    return null;
  }

  /** Find and return the Conversation with the given title. */
  public Conversation getConversationWithTitle(String title) {
    for (Conversation conversation : conversations) {
      if (conversation.getTitle().equals(title)) {
        return conversation;
      }
    }
    return null;
  }
  /** Find and return the Conversation with the given title. */
  public Conversation getBotConversation(UUID userId) {

    for (Conversation conversation : conversations) {
      if (conversation.getId().equals(conversation.getOwnerId()) && conversation.getOwnerId().equals(userId)) {
        return conversation;
      }
    }
    return null;
  }

  /** Sets the List of Conversations stored by this ConversationStore. */
  public void setConversations(List<Conversation> conversations) {
    this.conversations = conversations;
  }

  /** Returns the count of total conversations stored */
  public int getConversationCount() {
    return conversations.size();
  }
  /** Returns the title of the most active conversation */
  public String getMostActiveConversationTitle() {
    sort();
    return getMostActiveConversation().getTitle();
  }

  /** Returns the amount of messages of the most active conversation */
  public long getMaxMessageCount() {
    return getMostActiveConversation().getMessageCount();
  }

  /** Returns the last Conversation of the conversations list */
  public Conversation getMostActiveConversation() {
    return conversations.get(conversations.size()-1);
  }

  /** Method to sort the conversations contained in the list */
  public void sort() {
    conversations.sort(Conversation.conversationComparator);
  }
}
