+<%--
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
    <title>Profile</title>
    <link rel="stylesheet" href="/css/main.css">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"
          integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

</head>
<body>

<nav>
    <%@ include file="navbar.jsp" %>
</nav>

<div id="container">
    <h1><%= request.getSession().getAttribute("user") %>'s Profile page </h1>
    <!--About Section -->
    <h3>Summary</h3>
    <!-- info from descriptions -->
    <%=request.getSession().getAttribute("description")%>
    <br>
    <br>
    <br>
    <h3> Edit Summery</h3>

    <form action="/profile" method="POST">
        <label for="description">Summary</label>
        <br/>
        <!-- Populating description field so that user can edit it, if they want it -->
        <textarea class="form-control" rows="4" cols="100" name="description"
                  id="description"><%=request.getSession().getAttribute("description")%></textarea>
        <br/>
        <button class="btn btn-primary" button type=submit>Submit</button>
    </form>
    <hr>

</div>
</body>
</html>