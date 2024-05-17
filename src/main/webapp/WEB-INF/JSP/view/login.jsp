<html>
<head>
    <title>Ticket Login</title>
</head>
<body>
  <h2>Login</h2>
  You must log in to access the ticket website.<br><br>
  <c:if test="${loginFailed == true}">
    <b><c:out value="The username or password you enteres are not correct, Please try again."></c:out></b><br>
  </c:if>
  <form method="POST" action="<c:url value='/login'/>">
    Username: <input type="text" name="username"><br><br>
    Password: <input type="password" name="password"><br><br>
    <input type="submit" value="Log In">
  </form>
</body>
</html>
