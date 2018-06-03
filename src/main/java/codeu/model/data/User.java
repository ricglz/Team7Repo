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

import java.util.Comparator;
import java.time.Instant;
import java.util.UUID;

/** Class representing a registered user. */
public class User {
  private final UUID id;
  private final String name;
  private final String passwordHash;
  private final Instant creation;
  private long messageCount;
  private boolean isAdmin;
  private  String description;

  /**
   * Constructs a new User.
   *
   * @param id           the ID of this User
   * @param name         the username of this User
   * @param passwordHash the password hash of this User
   * @param creation     the creation time of this User
   * @param creation the creation time of this User
   * @param description of the username from profile section
   */
  public User(UUID id, String name, String passwordHash, Instant creation, boolean isAdmin) {
    this.id = id;
    this.name = name;
    this.passwordHash = passwordHash;
    this.creation = creation;
    this.messageCount = 0;
    this.isAdmin = isAdmin;
    this.description = "";
  }

  public User(UUID id, String name, String passwordHash, Instant creation, long messageCount, boolean isAdmin, String description) {
    this.id = id;
    this.name = name;
    this.passwordHash = passwordHash;
    this.creation = creation;
    this.messageCount = messageCount;
    this.isAdmin = isAdmin;
  }

  /** Increases the message count amount by one */
  public void increaseMessageCount(){
    messageCount++;
  }

  /** Returns the ID of this User. */
  public UUID getId() {
    return id;
  }

  /** Returns the username of this User. */
  public String getName() {
    return name;
  }

  /** Returns the password hash of this User. */
  public String getPasswordHash() {
    return passwordHash;
  }

  /** Returns the creation time of this User. */
  public Instant getCreationTime() {
    return creation;
  }


  /** Returns the description */
  public String getDescription(){
    return description;
  }

  /** Updates the description */
  public void setDescription(String Description){
    description = Description;
  }

  /** Returns the message count of this User. */
  public long getMessageCount() {
    return messageCount;
  }

  /** Allows to know if the user is an admin */
  public boolean isAdmin() {
    return isAdmin;
  }

  /** Makes this User an admin */
  public void makeAdmin() {
    isAdmin = true;
  }

  /** Function to compare the diferent Users for sorting
   * The sorting compares the message count of each user
   * of an array. Leaving the user with less message count
   * at the start and the one with more at the end.
  */
  public static Comparator<User> userComparator
                          = new Comparator<User>() {

	    public int compare(User User1, User User2) {

        long messageCount1 = User1.getMessageCount();
        long messageCount2 = User2.getMessageCount();

	      //ascending order
	      return (int) (messageCount1-messageCount2);
	    }

	};
}
