<%@ page import="java.util.List" %>
<%@ page import="java.util.UUID" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.model.data.Activity" %>
<%@ page import="codeu.model.data.ActivityType" %>
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.store.basic.ActivityStore" %>

<% List<Activity> activities = (List<Activity>) request.getAttribute("activities"); %>
<!DOCTYPE html>
<html>

<head>
    <title>Activity</title>
    <link rel="stylesheet" href="/css/main.css">

    <style>
        #activity {
          background-color: white;
          height: 500px;
          overflow-y: scroll
        }
    </style>
</head>

<body>
    <%@ include file="navbar.jsp" %>
    <h1>Activity</h1>   

    <div id="container">

        <div id="activity">
            <ul>
                <%
                String username;
                UUID ownerId;
                String ownerUsername;
                String conversationTitle;
                UUID authorId;
                String authorUsername;
                UUID conversationId;
                String messageContent;
                for (Activity activity : activities) {
                    switch (activity.getActivityType()) {
                        case UserRegistered: {
                            username = UserStore.getInstance().getUser(activity.getId()).getName(); %>
                            <li><strong><%= activity.getCreationTime() %>:</strong> <%= username %> joined!</li>
                            <% break; 
                        }    
                        case ConversationCreated: {
                            ownerId = ConversationStore.getInstance().getConversationWithUUID(activity.getId()).getOwnerId();
                            ownerUsername = UserStore.getInstance().getUser(ownerId).getName();
                            conversationTitle = ConversationStore.getInstance().getConversationWithUUID(activity.getId()).getTitle(); %>
                            <li><strong><%= activity.getCreationTime() %>:</strong> 
                                <%= ownerUsername %> created a new conversation: 
                                <a href="/chat/<%= conversationTitle %>"><%= conversationTitle %></a></li>  
                            <% break;
                        }                    
                        case MessageSent: {
                            authorId = MessageStore.getInstance().getMessage(activity.getId()).getAuthorId();
                            authorUsername = UserStore.getInstance().getUser(authorId).getName();
                            conversationId = MessageStore.getInstance().getMessage(activity.getId()).getConversationId();
                            conversationTitle = ConversationStore.getInstance().getConversationWithUUID(conversationId).getTitle();
                            messageContent = MessageStore.getInstance().getMessage(activity.getId()).getContent(); %>
                            <li><strong><%= activity.getCreationTime() %>:</strong> <%= authorUsername %> sent a message in 
                            <a href="/chat/<%= conversationTitle %>"><%= conversationTitle %></a>: 
                            "<%= messageContent %>"</li>    
                            <% break; 
                        }                                                    
                    }
                } %>
            </ul>
        </div>
    </div>
</body>