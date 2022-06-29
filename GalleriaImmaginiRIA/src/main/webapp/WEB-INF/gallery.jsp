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
		<a href="Logout"> <!-- hashes are actually useless tho -->
			<button>Logout</button>
		</a>
	</div>

	<h1>Image Gallery</h1>

	<div id="userDiv">
		<h2>Your albums</h2>
		<table>
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
			<a href="#updateOrdering">
				<button>Save this album order</button>
			</a>
		</div>
		<br>
		<div id="createAlbumButton">
			<a href="#createAlbum">
				<button>Create a new album</button>
			</a>
		</div>
	</div>

	<br>

	<div id="othersDiv">
		<h2>Other users' albums</h2>
		<table>
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
	
	<div id="albumDiv">
		<br><hr><br>
		<h2 id ="albumNameHeader">Album view</h2>
		<div id="albumPlusButtons">
			<div id="precButton">
				<a href="#prec"> <!-- hashes are actually useless tho -->
					<button>Precedente</button>
				</a>
			</div>
			<table>
				<tbody id="albumView">
				</tbody>
			</table>
			<div id="succButton">
				<a href="#succ"> <!-- hashes are actually useless tho -->
					<button>Successivo</button>
				</a>
			</div>
		</div>
		<br>
		<div id="editAlbumButton">
			<a href="#editAlbum">
				<button>Edit this album</button>
			</a>
		</div>
	</div>
	
	<div id="editDiv">
		<br><hr><br>
		<h2>Edit album</h2>
		<form action="#albumEdit" id="albumEditForm">
			<div>
				<label>Album title: </label> 
				<input type="text" id="albumEditTitle" name="albumTitle" required/>
			</div>
			<ul id="albumEditList">
			</ul>			
		</form>
		<input type="button" id="albumEditButton" value="Edit album">
	</div>
	<br><br><br>
	<div id="modalWindow">
		<div id="modalContent">
			<span id="closeButton">&times;</span>
			<div id="imageView">
			</div>
			<br>
			<div id="commentsSection">
			</div>
			<br>
			<div>
				<textarea id="yourComment" placeholder="Your comment here..."></textarea>
				<div id="newCommentButton">
					<a href="#newComment">
						<button>Enter comment</button>
					</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
