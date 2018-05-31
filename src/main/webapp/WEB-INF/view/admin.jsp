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

<%
  UserStore userStore = UserStore.getInstance();
  ConversationStore conversationStore = ConversationStore.getInstance();
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
      <li>Total amount of conversations: <%=conversationStore.getConversationCount()%></li>
      <li>Total amount of user: <%=userStore.getUserCount()%></li>
    </ul>
  </div>
</body>
</html>