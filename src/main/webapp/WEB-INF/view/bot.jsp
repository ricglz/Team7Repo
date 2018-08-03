<%--
  Created by IntelliJ IDEA.
  User: Manjil
  Date: 6/9/2018
  Time: 10:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%
    List<Message> messages = (List<Message>) request.getAttribute("botmessage");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Botler</title>
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
            <h1><a href="" style="float: right; font-size: 56px;" class="">&#8635;</a>Botler, at your service.</h1>
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
            <form action="/bot" method="POST">
                <div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
                    <input class="mdl-textfield__input" type="text" name="botmessage" id="botmessage">
                    <label class="mdl-textfield__label" for="botmessage">Message</label>
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