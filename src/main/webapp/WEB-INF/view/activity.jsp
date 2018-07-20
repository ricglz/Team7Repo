<%@ page import="java.util.List" %>
<%@ page import="java.util.UUID" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Activity" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.store.basic.ActivityStore" %>

<% List<Activity> activities = (List<Activity>) request.getAttribute("activities"); %>
<!DOCTYPE html>
<html>

<head>
    <title>Activity</title>
    <%@ include file="/files.jsp" %>

    <style>
        body {
            margin: 0;
            font-family: sans-serif;
            line-height: 1.6;
            font-size: 18px;
            line-height: 1.6;
            color: #444;
            background-color:#eeeeee;
        }
        nav {
            background-color:blue;
        }
        nav a {
            color: white;
            display: inline-block;
            font-size: 24px;
            margin: 15px;
            text-decoration: none;
        }
        #navTitle {
            font-size: 36px;
        }
        #container {
            margin-left: auto;
            margin-right: auto;
            width: 800px;
        }
        h1 {
            color: #757575;
        }
        input {
            font-size: 18px;
        }
        button {
            font-size: 18px;
        }
        .stat-information {
            width:75%;
            margin-left:auto;
            margin-right:auto;
            margin-top: 50px;
        }
        #activity {
          background-color: white;
          height: 500px;
          overflow-y: scroll
        }

        #color{
            display:hidden;
        }
    </style>
</head>

<body>
    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
        <%@ include file="navbar.jsp" %>
        <main class="mdl-layout__content">
            <h1>Activity</h1>   
            <div id="container">
                <div id="activity">
                    <ul>
                        <% for (Activity activity : activities) { %>
                            <li> <%= activity.getDisplayString() %> </li>
                        <% } %>
                    </ul>
                </div>
            </div>
        </main>
    </div>
</body>