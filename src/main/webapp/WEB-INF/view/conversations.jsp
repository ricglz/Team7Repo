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

<!DOCTYPE html>
<html>
<head>
  <title>Conversations</title>
  <%@ include file="/files.jsp" %>
</head>
<body>
  <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <%@ include file="navbar.jsp" %>
    <main class="mdl-layout__content">
      <div id="container">

        <% if(request.getAttribute("error") != null){ %>
            <h2 style="color:red"><%= request.getAttribute("error") %></h2>
        <% } %>

        <% if(request.getSession().getAttribute("user") != null){ %>
          <h1>New Conversation</h1>
          <form action="/conversations" method="POST">
            <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
              <label class="mdl-textfield__label" for="conversationTitle">Conversation title: </label>
              <input class="mdl-textfield__input" type="text" name="conversationTitle" id="conversationTitle">
            </div>
            <button class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent"
                    type="submit">
                  Create
            </button>
          </form>
          <hr/>
        <% } %>

        <h1>Conversations</h1>

        <%
          List<Conversation> conversations =
            (List<Conversation>) request.getAttribute("conversations");
          if(conversations == null || conversations.isEmpty()){
        %>
        <p>Create a conversation to get started.</p>
        <%
          }
          else{
        %>
        <ul class="mdl-list">
        <% for(Conversation conversation : conversations){ %>
          <li class="mdl-list__item mdl-list__item--two-line">
            <span class="mdl-list__item-primary-content">
              <i class="material-icons mdl-list__item-avatar">group</i>
              <a href="/chat/<%= conversation.getTitle() %>">
                <%= conversation.getTitle() %></a>
              </a>
              <span class="mdl-list__item-sub-title">
                <%= conversation.getMessageCount() %> Messages
              </span>
            </span>
          </li>
        <%
          }
        %>
          </ul>
        <%
        }
        %>
        <hr/>
      </div>
    </main>
  </div>
</body>
</html>
