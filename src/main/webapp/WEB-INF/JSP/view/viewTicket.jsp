<html>
<head>
    <title>ticket #<c:out value="${ticketId}"/></title>
</head>
<body>
    <h2>ticket Post</h2>
    <h3>ticket #<c:out value="${ticketId}"/>: <c:out value="${ticket.title}"/></h3>
    <p>Date: <c:out value="${ticket.date}"/></p>
    <p><c:out value="${ticket.body}"/></p>
    <c:if test="${ticket.hasAttachment()}">
        <a href="<c:url value='/ticket' >
            <c:param name='action' value='download' />
            <c:param name='ticketID' value='${ticketId}' />
            <c:param name='attachment' value='${ticket.attachment.name}'/>
        </c:url>"><c:out value="${ticket.attachment.name}"/></a>
    </c:if>
    <br><a href="ticket">Return to ticket list</a>

</body>
</html>
