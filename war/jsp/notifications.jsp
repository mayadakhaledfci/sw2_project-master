<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Accept Requests Page</title>
</head>
<body>
<%@ page import= "com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.FCI.SWE.Models.*" %>
<%@ page import="java.util.*" %>

<%
DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
Query gaeQuery = new Query("requests");
PreparedQuery pq = datastore.prepare(gaeQuery);
%>

<form action="/social/acceptFriendRequest" method="post">

<select name="friendRequest">
<%
List<String> list=new ArrayList<String>();
for (Entity e : pq.asIterable()){
	String s=e.getProperty("status").toString();
	if(s.equals("0")){
		if(e.getProperty("currentUser").equals(session.getAttribute("name"))){
			String x= e.getProperty("toUser").toString()+" accepted your friend request";
			list.add(x);
		}
		continue;
	}
if(!e.getProperty("toUser").equals(session.getAttribute("name")))continue;
%>

<option value=<%=e.getProperty("currentUser").toString() %>><%=e.getProperty("currentUser")%> sent you a friend request</option>

<%
}
%>
</select>
<br>
<input type="submit" value="Accept">
</form>


</body>
</html>