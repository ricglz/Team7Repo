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
<!DOCTYPE html>
<html>
<head>
  <title>Botler</title>
  <%@ include file="/files.jsp" %>  
</head>
<body>
  <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
    <%@ include file="/WEB-INF/view/navbar.jsp" %>
    <main class="mdl-layout__content">
      <div id="container">
        <div
          style="width:90%; margin-left:auto; margin-right:auto; margin-top: 50px;">

          <h1>About Botler</h1>
          <p>
            Welcome to our Chat bot "Botler". Botler is built with mindset of saving your precious time to make users life easier and simpler. The cool UI with fresh
            design is easy to navigate and user-friendly. Below, is the descriptions of various tasks that Botler can perform:</p>
          <ul>
            <li><strong>Changing the User Description: </strong> To change description: change description to" NewDescription". Along with updating description, it
            also gives link to profile page.</li>
            <li><strong>Creating new conversation: </strong>To create new conversation:  create conversation "newConversationName"</li>
            <li><strong>Send message:</strong> To send message to particular conversation: send message to "conversationTitle"</li>
            <li><strong>Retrieve message:</strong> To retrieve messages based on Conversation title: get messages from "conversationTitle"</li>
            <li><strong>Navigate to any page:</strong> To navigate to any page: navigate to pageName(About, Login,Register,Activity)</li>
            <li><strong>Get all messages: </strong>To get all message user have sent: get my messages</li>
            <li><strong>Retrieve conversation based on Time: </strong>To get conversation based on time: get conversation
              made 12:00 AM (or any other Time expressions)</li>
            <li><strong>Retrieve conversation: </strong> To retrieve conversation based on Author name: get conversation by "AuthorName"</li>
            <li><strong>Creating new conversation: </strong> Create conversation "newConversationName"</li>
            <li><strong>Retrieve all conversation: </strong> To get all conversation: Get all conversation</li>
            <li><strong>Search Keywords: </strong> To search keyword in message: get message about "keyword"</li>
            <li><strong>For Help: </strong> To get information about Bot functions:Get Help</li>
            <li><strong>Get conversation: </strong> To get Conversation that contains specific keyword: get conversation about "keyword"</li>
            <li><strong>Navigate to conversation</strong>: </strong> To navigate to conversation:Navigate to "conversationName"</li>
            <li><strong>Get all stats: </strong> To retrieve your message stats:get all stats. This provides information about
            Total number of sent messages,Conversations you have participated in and Last conversations that you were involved in. </li>
          </ul>

          <p>
            <strong>Members: </strong>
            <ul>
          <li>Kirielle Singarajah</li>
          <li>Manjil Gautam</li>
          <li>Ricardo González Castillo</li>
          <li>Tofe Salako (Project Advisor)</li>
        </ul>
          </p>
        </div>
      </div>
    </main>
  </div>
</body>
</html>
