<%@ page import="codeu.model.store.basic.SettingStore" %>
<!--
    General navigation bar setup (3 main "modes")
    - Visitor (not logged in)
        - Have access to: Register, Login, About
    - User (logged in)
        - Have access to: Activity Feed, Profile, Conversations, About
        - Display "Hello, <user>!"
    - Admin (logged in  admin)
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
<header class="mdl-layout__header">
    <div class="mdl-layout__header-row">
      <!-- Title -->
      <a class="mdl-navigation__link" id="navTitle" href="/">CodeU Chat App</a>
      <!-- Add spacer, to align navigation to the right -->
      <div class="mdl-layout-spacer"></div>
      <!-- Navigation. We hide it in small screens. -->
      <nav class="mdl-navigation mdl-layout--large-screen-only">
        <% if(request.getSession().getAttribute("user") == null) { %>
            <a class="mdl-navigation__link" href="/register">Register</a>
            <a class="mdl-navigation__link" href="/login">Login</a>
        <% } else { %>
            <a class="mdl-navigation__link" href="/bot">Botler</a>
            <a class="mdl-navigation__link" href="/conversations">Conversations</a>  
        <% } %>
      </nav>
    </div>
</header>
<div class="mdl-layout__drawer">
    <span class="mdl-layout-title">
        <% if(request.getSession().getAttribute("user") != null) { %>
            <% if(request.getSession().getAttribute("admin") != null) { %>
                Hello, Admin <%= request.getSession().getAttribute("user") %>!
            <% } else { %>
                Hello, <%= request.getSession().getAttribute("user") %>!
            <% } %>
        <% } %>
    </span>
    <nav class="mdl-navigation">
        <% if(request.getSession().getAttribute("user") != null) { %>
            <a class="mdl-navigation__link" href="/activity">Activity</a>
            <% if(request.getSession().getAttribute("admin") != null) { %>
                <a class="mdl-navigation__link" href="/admin">Admin Page</a>
            <% } %>  
            <a class="mdl-navigation__link" href="/profile">Profile</a>
            <a class="mdl-navigation__link" href="/bot">Bot Conversation</a>
            <a class="mdl-navigation__link" href="/conversations">Conversations</a>  
        <% } %>
        <a class="mdl-navigation__link" href="/about.jsp">About</a>
    </nav>
</div>