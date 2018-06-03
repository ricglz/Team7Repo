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

package codeu.model.data;

import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

/**
 * Class representing a conversation, which can be thought of as a chat room. Conversations are
 * created by a User and contain Messages.
 */
public class Conversation {
  public final UUID id;
  public final UUID owner;
  public final Instant creation;
  public final String title;
  private long messageCount;

  /**
   * Constructs a new Conversation.
   *
   * @param id the ID of this Conversation
   * @param owner the ID of the User who created this Conversation
   * @param title the title of this Conversation
   * @param creation the creation time of this Conversation
   */
  public Conversation(UUID id, UUID owner, String title, Instant creation) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.messageCount = 0;
  }

  /**
   * Constructs a new Conversation.
   *
   * @param id the ID of this Conversation
   * @param owner the ID of the User who created this Conversation
   * @param title the title of this Conversation
   * @param creation the creation time of this Conversation
   * @param messageCount the message count of this Conversation
   */
  public Conversation(UUID id, UUID owner, String title, Instant creation, long messageCount) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.messageCount = messageCount;
  }

  /** Increases the count of messages fo this conversation by one*/
  public void increaseMessageCount() {
    messageCount++;
  }

  /** Returns the ID of this Conversation. */
  public UUID getId() {
    return id;
  }

  /** Returns the ID of the User who created this Conversation. */
  public UUID getOwnerId() {
    return owner;
  }

  /** Returns the title of this Conversation. */
  public String getTitle() {
    return title;
  }

  /** Returns the creation time of this Conversation. */
  public Instant getCreationTime() {
    return creation;
  }

  /** Returns the count of messages of this Conversation */
  public long getMessageCount() {
    return messageCount;
  }

  /** Function to compare the diferent Conversartions for sorting. 
   * The sorting compares the message count of each conversation
   * of an array. Leaving the conversation with less message count
   * at the start and the one with more at the end.
  */
  public static Comparator<Conversation> conversationComparator
                          = new Comparator<Conversation>() {

	    public int compare(Conversation conversation1, Conversation conversation2) {

        long messageCount1 = conversation1.getMessageCount();
        long messageCount2 = conversation2.getMessageCount();

	      //ascending order
	      return (int) (messageCount1-messageCount2);
	    }

	};
}
