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
        #activity {
          background-color: white;
          height: 500px;
          overflow-y: scroll
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