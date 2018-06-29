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
    <title>Bot Conversation Page</title>
    <link rel="stylesheet" href="/css/main.css" type="text/css">

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

<nav>
    <%@ include file="navbar.jsp" %>
</nav>

<div id="container">

    <h1><a href="" style="float: right">&#8635;</a>Bot Conversation</h1>

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
        <input type="text" name="botmessage">
        <br/>
        <button type="submit">Send</button>
    </form>
    <% } else { %>
    <p><a href="/login">Login</a> to send a message.</p>
    <% } %>

    <hr/>

</div>

</body>
</html>