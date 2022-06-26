<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">

<head>
<meta charset="ISO-8859-1">
<title>PF15 - Gallery</title>
<link rel="stylesheet" type="text/css" media="all" href="/css/home_page.css" />
<script type="text/javascript" src="js/utils.js" charset="utf-8" defer></script>
<script type="text/javascript" src="js/login_page.js" charset="utf-8" defer></script>
</head>
<body>
	<div id="logoutButton">
		<a href="#logout"> <!-- hashes are actually useless tho -->
			<button>Logout</button>
		</a>
	</div>

	<h1>Image Gallery</h1>

	<div id="yourDiv">
		<h2>Your albums</h2>
		<table border="1">
			<thead>
				<tr>
					<td>Title</td>
					<td>ID</td>
					<td>Date</td>
				</tr>
			</thead>
			<tbody id="yourAlbums">
			</tbody>
		</table>
		<br>
		<div id="createAlbumButton">
			<a href="#createAlbum"> <!-- hashes are actually useless tho -->
				<button>Create a new album</button>
			</a>
		</div>
		<div id="customOrderButton">
			<a href="#customAlbumOrder"> <!-- hashes are actually useless tho -->
				<button>Save this album order</button>
			</a>
		</div>
	</div>
	<br>
	<div id="othersDiv">
		<h2>Other users' albums</h2>
		<table border="1">
			<thead>
				<tr>
					<td>Creator</td>
					<td>Title</td>
					<td>ID</td>
					<td>Date</td>
				</tr>
			</thead>
			<tbody id="othersAlbums">
			</tbody>
		</table>
	</div>
	<br><hr><br>
	<div id="albumDiv">
		<h2>Album view</h3>
		<table border="1">
			<tbody id="albumView">
				<tr>
					<td id="precButtonCell" rowspan="2" ></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td id="succButtonCell" rowspan="2"></td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
		</table>
		<br>
		<div id="editAlbumButton">
			<a href="#editAlbum"> <!-- hashes are actually useless tho -->
				<button>Edit this album</button>
			</a>
		</div>
	</div>
	<br><hr><br>
	<div id="editDiv">
		<h2>Edit album</h2>
		<!-- TODO id to use: "AlbumEditView" -->
	</div>
	<br><hr><br>
	<div id="modalWindow" class="modal">
		<div class="modal-content">
			<span id="closeButton">&times;</span>
			<div id="imageView">
				<p>Some text in the modal window.</p> <!-- TODO -->
			</div>
			
		</div>
	</div>
</body>
</html>
