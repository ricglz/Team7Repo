// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.store.basic.UserStore;
import codeu.model.data.User;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AdminServletTest {

  private AdminServlet adminServlet;
  private HttpServletRequest mockRequest;
  private HttpSession mockSession;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;
  private UserStore mockUserStore;

  @Before
  public void setup() {
    adminServlet = new AdminServlet();

    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/admin.jsp"))
        .thenReturn(mockRequestDispatcher);
    
    mockUserStore = Mockito.mock(UserStore.class);
    adminServlet.setUserStore(mockUserStore);
  }
  
  @Test
  public void testdoGet() throws IOException, ServletException {
	adminServlet.doGet(mockRequest, mockResponse);
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testdoPost_emptyUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("");
    
    adminServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("error", "Introduce a username please.");
  }

  @Test
  public void testdoPost_nonexistenttUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(null);

    adminServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("error", "That username was not found.");
  }

  @Test
  public void testdoPost_alreadyAnAdmin() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("Ricardo");
    User user =
      new User(
        UUID.randomUUID(),
        "Ricardo",
        "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
        Instant.now());
    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("Ricardo")).thenReturn(true);
    Mockito.when(mockUserStore.getUser("Ricardo")).thenReturn(user);
    adminServlet.setUserStore(mockUserStore);

    adminServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("message", "The user is already an admin.");
  }

  @Test
  public void testdoPost_notAnAdmin() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    User user =
      new User(
        UUID.randomUUID(),
        "test_username",
        "$2a$10$eDhncK/4cNH2KE.Y51AWpeL8/5znNBQLuAFlyJpSYNODR/SJQ/Fg6",
        Instant.now());
    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("test_username")).thenReturn(true);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(user);
    adminServlet.setUserStore(mockUserStore);

    adminServlet.doPost(mockRequest, mockResponse);
    
    Assert.assertEquals(true, user.isAdmin());
    Mockito.verify(mockRequest).setAttribute("message", "The user has been made admin.");
  }
  
}
