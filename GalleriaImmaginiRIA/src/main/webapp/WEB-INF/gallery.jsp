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

	<div>
		<h2>Your albums</h2>
		<table border="1">
			<thead>
				<tr>
					<td>Title</td>
					<td>Date</td>
				</tr>
			</thead>
			<tbody id="yourAlbums">
			</tbody>
		</table>
		<form action="/CreateAlbum">
			<input type="submit" value="Create a new album"/>
		</form>
	</div>
	<br>
	<div>
		<h2>Other users' albums</h2>
		<table border="1">
			<thead>
				<tr>
					<td>Creator</td>
					<td>Title</td>
					<td>Date</td>
				</tr>
			</thead>
			<tbody id="othersAlbums">
			</tbody>
		</table>
	</div>
</body>
</html>
