<%--
  Copyright 2017 Google Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.data.Message" %>

<%
  UserStore userStore = UserStore.getInstance();
  ConversationStore conversationStore = ConversationStore.getInstance();
  MessageStore messageStore = MessageStore.getInstance();
  Message longestMessage;
%>

<!DOCTYPE html>
<html>
<head>
  <title>Admin</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <%@ include file="navbar.jsp" %>
  </nav>

  <div class="stat-information" id="container">
    <h1>Statistics</h1>
    <ul>
      <li>Conversations: <%=conversationStore.getConversationCount()%></li>
      <li>Users: <%=userStore.getUserCount()%></li>
      <li>Messages: <%=messageStore.getMessagesCount()%></li>
      <%if(userStore.getUserCount() != 0) {%>
        <li>Most active user: <%=userStore.getMostActiveUser()%> with <%=userStore.getMaxMessageCount()%> messages</li>
      <%}%>
      <%if(conversationStore.getConversationCount() != 0){%>
        <li>Most active conversation: <%=conversationStore.getMostActiveConversation()%> with <%=conversationStore.getMaxMessageCount()%> messages
      <%}%>
      <%if(messageStore.getMessagesCount() != 0) {
        longestMessage = messageStore.getLongestMessage();
      %>
        <li>Longest message: <%=longestMessage.getContent().length()-1%> letters
      <%}%>
    </ul>
    <hr>
    <% if(request.getAttribute("error") != null){ %>
        <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

    <% if(request.getAttribute("message") != null){ %>
        <h2 style="color:blue"><%= request.getAttribute("message") %></h2>
    <% } %>
    <h1>Add admin</h1>
    <form action="/admin" method="POST">
      <label for="username">Username: </label>
      <br/>
      <input type="text" name="username" id="username">
      <br/><br/>
      <button type="submit">Login</button>
    </form>
  </div>
</body>
</html>
