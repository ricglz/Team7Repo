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
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%
Conversation conversation = (Conversation) request.getAttribute("conversation");
List<Message> messages = (List<Message>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html>
<head>
  <title><%= conversation.getTitle() %></title>
  <%@ include file="/files.jsp" %>

  <style>
    #chat {
      background-color: white;
      height: 500px;
      overflow-y: scroll
    }
  </style>

  <script>
    // scroll the chat div to the bottom
    function scrollChat() {
      var chatDiv = document.getElementById('chat');
      chatDiv.scrollTop = chatDiv.scrollHeight;
    };
  </script>
</head>
<body onload="scrollChat()">
  <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <%@ include file="navbar.jsp" %>
    <main class="mdl-layout__content">
      <div id="container">
        <h1><%= conversation.getTitle() %>
          <a href="" style="float: right">&#8635;</a></h1>
        <hr/>
        <div id="chat">
          <ul>
        <%
          for (Message message : messages) {
            String author = UserStore.getInstance()
              .getUser(message.getAuthorId()).getName();
        %>
          <li><strong><%= author %>:</strong> <%= message.getContent() %></li>
        <%
          }
        %>
          </ul>
        </div>
        <hr/>
        <% if (request.getSession().getAttribute("user") != null) { %>
        <form action="/chat/<%= conversation.getTitle() %>" method="POST">
            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="text" name="message" id="message">
                    <label class="mdl-textfield__label" for="message">Message</label>
                </div>
                <button class="mdl-button mdl-js-button mdl-button--fab mdl-js-ripple-effect mdl-button--colored">
                  <i class="material-icons">send</i>
                </button>
        </form>
        <% } else { %>
          <p><a href="/login">Login</a> to send a message.</p>
        <% } %>
        <hr/>
      </div>
    </main>
  </div>
</body>
</html>
