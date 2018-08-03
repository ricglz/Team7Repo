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
          style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">
          <h1>CodeU </h1>
          <h2>Welcome!</h2>
          <ul>
            <li><a href="/register">Register</a> to get started!</li>
            <li>Returning user? <a href="/login">Login</a> here.</li>
            <li>Go to the <a href="/conversations">conversations</a> page to
                create or join a conversation.</li>
            <li>View the <a href="/about.jsp">about</a> to learn more about bot functions.</li>
          </ul>
        </div>
      </div>
    </main>
  </div>
</body>
</html>
