<%@ page import="codeu.model.store.basic.SettingStore" %>
<!--
    General navigation bar setup (3 main "modes")
    - Visitor (not logged in)
        - Have access to: Register, Login, About
    - User (logged in)
        - Have access to: Activity Feed, Profile, Conversations, About
        - Display "Hello, <user>!"
    - Admin (logged in + admin)
        - Have access to: Activity Feed, Admin Page, Profile, Conversations, About
        - Display "Hello, Admin <user>!"
-->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<!--retrives the "color" and sets it to HTML
//Also sets the back-ground color of nav to "color"
//which gets update in all pages-->
<script>
    $(document).ready(function () {
        var color = $("#color").html();
        $("html").css("background-color", color);
        $("body").css("background-color", color);
    })
</script>
<nav>
    <div id="color"><%String color = SettingStore.getInstance().getColor();%>
    </div>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <% if(request.getSession().getAttribute("user") == null) { %>
        <a href="/register">Register</a>
        <a href="/login">Login</a>
        <a href="/about.jsp">About</a>
    <% }
    else { %>
        <% if(request.getSession().getAttribute("admin") != null) { %>
            <a>Hello, Admin <%= request.getSession().getAttribute("user") %>!</a>
        <% } else { %>
            <a>Hello, <%= request.getSession().getAttribute("user") %>!</a>
        <% } %>
        <a href="/activity">Activity</a>
        <% if(request.getSession().getAttribute("admin") != null) { %>
            <a href="/admin">Admin Page</a>
        <% } %>  
        <a href="/profile">Profile</a>
        <a href="/bot">Bot Conversation</a>
        <a href="/conversations">Conversations</a>  
        <a href="/about.jsp">About</a>
    <% } %>
</nav>