<!DOCTYPE html>
<html xmlns:th="https://www.thymeleaf.org">

<head>
<meta charset="ISO-8859-1">
<title>PF15 - Gallery</title>
<link rel="stylesheet" type="text/css" media="all" href="css/gallery.css" />
<script type="text/javascript" src="js/utils.js" charset="utf-8" defer></script>
<script type="text/javascript" src="js/gallery.js" charset="utf-8" defer></script>
</head>
<body>
	<div id="logoutButton">
		<a href="#logout"> <!-- hashes are actually useless tho -->
			<button>Logout</button>
		</a>
	</div>

	<h1>Image Gallery</h1>

	<div id="userDiv">
		<h2>Your albums</h2>
		<table border="1">
			<thead>
				<tr>
					<td>Title</td>
					<td>ID</td>
					<td>Date</td>
				</tr>
			</thead>
			<tbody id="userAlbums">
			</tbody>
		</table>
		<br>
		<div id="customOrderButton">
			<a href="#customAlbumOrder">
				<button>Save this album order</button>
			</a>
		</div>
		<div id="createAlbumButton">
			<a href="#createAlbum">
				<button>Create a new album</button>
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
		<h2>Album view</h2>
		<table>
			<tbody>
				<tr>
					<td>
						<div id="precButton">
							<a href="#prec"> <!-- hashes are actually useless tho -->
								<button>Precedente</button>
							</a>
						</div>
					</td>
					<td>
						<table border="1">
							<tbody id="albumView">
								<tr>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
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
					</td>
					<td>
						<div id="succButton">
							<a href="#succ"> <!-- hashes are actually useless tho -->
								<button>Successivo</button>
							</a>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
		<br>
		<div id="editAlbumButton">
			<a href="#editAlbum">
				<button>Edit this album</button>
			</a>
		</div>
	</div>
	<br><hr><br>
	<div id="editDiv">
		<h2>Edit album</h2>
		<!-- TODO id to use for this part: "AlbumEditView" -->
	</div>
	<br><hr><br>
	<div id="modalWindow">
		<div id="modalContent">
			<span id="closeButton">&times;</span>
			<div id="imageView">
				<p>Some text in the modal window.</p> <!-- TODO -->
			</div>
			
		</div>
	</div>
</body>
</html>
