<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">

<head>
<meta charset="ISO-8859-1">
<title>PF15 - Gallery</title>
<link rel="stylesheet" type="text/css" media="all" th:href="@{/css/home_page.css}" />
<script type="text/javascript" src="js/utils.js" charset="utf-8" defer></script>
<script type="text/javascript" src="js/login_page.js" charset="utf-8" defer></script>
</head>
<!-- attributi per thymeleaf: userAlbums e othersAlbums che sono List<Album> -->
<body>
	<div class="logoutButton">
		<a href="/Logout">
			<button>Logout</button>
		</a>
	</div>

	<h1>Image Gallery</h1>

	<div id=yourAlbums>
	<!-- ALL OF THIS IS NOT PRESENT WHEN THE PAGE IS LOADED, BUT IS GENERATED BY JAVASCRIPT
		<h2>Your albums</h2>
		<table th:if="${userAlbums.size()}" border="1">
			<thead>
				<tr>
					<td>Title</td>
					<td>Date</td>
				</tr>
			</thead>
			<tbody>
				<tr th:each="album : ${userAlbums}">
					<td><a th:text="${album.getTitle()}" th:href="@{/Album(id=${album.getId()})}"></a></td>
					<td th:text="${album.getDate()}">date1</td>
				</tr>
			</tbody>
		</table>
		<h6 th:if="${!userAlbums.size()}">You have no albums! Create one with the button below.</h6>
		<br>
		<form action="/CreateAlbum">
			<input type="submit" value="Create a new album"/>
		</form>
	-->
	</div>
	<br>
	<div id="othersAlbums">
	<!-- ��all of this won't be displayed but will be created by javascript
	<h2>Other users' albums</h2>
	<table th:if="${othersAlbums.size()}" border="1">
		<thead>
			<tr>
				<td>Creator</td>
				<td>Title</td>
				<td>Date</td>
			</tr>
		</thead>
		<tbody>
			<tr th:each="album : ${othersAlbums}">
				<td th:text="${album.getCreator_username()}">creator2</td>
				<td><a th:text="${album.getTitle()}" th:href="@{/Album(id=${album.getId()})}"></a></td>
				<td th:text="${album.getDate()}">date2</td>
			</tr>
		</tbody>
	</table>
	<h6 th:if="${!othersAlbums.size()}">There are no albums here!</h6>
	 -->
	</div>

</body>
</html>
