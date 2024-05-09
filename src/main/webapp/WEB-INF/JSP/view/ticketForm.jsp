<html>
<head>
    <title>Create a new ticket</title>
</head>
<body>
  <h2>Create a ticket Post</h2>
  <form method="POST" action="ticket" enctype="multipart/form-data">
    <input type="hidden" name="action" value="create">
    Title:<br>
    <input type="text" name="title"><br><br>
    Body:<br>
    <textarea name="body" rows="25" cols="100"></textarea><br><br>
    <b>Attachment</b><br>
    <input type="file" name="file1"><br><br>
    <input type="submit" value="Submit">
  </form>
</body>
</html>