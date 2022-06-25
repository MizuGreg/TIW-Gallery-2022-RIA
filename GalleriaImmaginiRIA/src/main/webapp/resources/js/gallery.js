(function() {
	var pageOrchestrator = new PageOrchestrator();
	var albumsList, albumView, imageView;
	
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "login_page.jsp"; // client-side LoggedFilter
		} else {
			pageOrchestrator.start();
			pageOrchestrator.refresh();
		}
	})
	
	class PageOrchestrator {
		constructor() {
			this.start = function () {
				albumsList = new AlbumsList(
					document.getElementById("userAlbums"),
					document.getElementById("othersAlbums")
					// vari parametri: document.getElementById("yourAlbums") ad es.
					// in realtà si può passare un wrapper obj1: val1, obj2:val2 ecc.
				);
				albumsList.registerEvents(this);

				albumView = new AlbumView(
					document.getElementById("albumView")
				);
				albumView.registerEvents(this);

				imageView = new ImageView(
					// cosa mettere qui?
				);
				imageView.registerEvents(this); // non credo questa riga sia necessaria

				document.getElementsByClassName("logoutButton").addEventListener("click", () => {
					window.sessionStorage.removeItem("username"); // client-side logout
					window.location.href = "login_page.html";
				});
			};

			this.refresh = function (currentAlbumId, currentImageId) {
				albumsList.reset();
				albumView.reset();
				imageView.reset();
				if (currentAlbumId == null) {
					albumsList.refresh();
				} else {
					albumView.refresh();
					if (currentImageId !== null) {
						albumView.autoclick(currentImageId);
					}
				}
			};
		}
	}
	
	class AlbumsList {
		constructor(userAlbums, othersAlbums) {
			this.userAlbums = userAlbums;
			this.othersAlbums = othersAlbums;

			this.registerEvents = (pageOrchestrator) => {
				this.orchestrator = pageOrchestrator;
			}

			this.reset = function () {
				this.userAlbums.style.visibility = "hidden";
				this.othersAlbums.style.visibility = "hidden";
				// or i guess i could also do the following:
				this.userAlbums.innerHTML = "";
				this.othersAlbums.innerHTML = "";
			};

			this.show = function (next) {
				var self = this; // ugh
				makeCall("GET", "Galleria", null, function(request) {
					if (request.readyState == XMLHttpRequest.DONE) {
						const responseJson = JSON.parse(request.responseText);
						console.log(responseJson);
						if (request.status == 200) {
							// fill yourAlbums and othersAlbums with json content
							self.updateUser(responseJson.userAlbums);
							self.updateOthers(responseJson.othersAlbums);
							if (next) next(); // ???
						} else {
							alert("There was an error while fetching the albums from the server. " +
							"Please try again later. Error: " + responseJson.errorJson);
						}
					}
				});
			};

			this.updateUser = function(AlbumsArray) {
				this.userAlbums.innerHTML = "";
				if (AlbumsArray.length == 0) {
					const row = this.userAlbums.insertRow();
					const cell = row.insertCell();
					cell.setAttribute("colspan", "3");
					cell.appendChild(document.createTextNode("You have no albums yet! " +
					"Create an album with the 'Create album' button."));
				} else {
					AlbumsArray.forEach(element => {
						const row = this.userAlbums.insertRow();
						const titleCell = row.insertCell();
						titleCell.appendChild(document.createTextNode(element.title));
						titleCell.onclick = () => {
							albumView.show(element.id);
						}
						const idCell = row.insertCell();
						idCell.appendChild(document.createTextNode(element.id));
						const dateCell = row.insertCell();
						dateCell.appendChild(document.createTextNode(element.date));
					});
				};
				this.makeOrderable();
				this.userAlbums.style.visibility = "visible";
			};

			this.makeOrderable = () => {
				var draggingRow;
				for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
					row.ondragstart = function startDrag() {
						draggingRow = event.target;
					};
					row.ondragover = function dragOver() {
						e.preventDefault();
						var t = event.target;
						const rows = Array.from(t.parentNode.parentNode.children);

						if (rows.indexOf(t.parentNode) > rows.indexOf(draggingRow))
							t.parentNode.after(draggingRow);
						else
							t.parentNode.before(draggingRow);
					}
				}
			}

			this.updateOthers = function(AlbumsArray) {
				this.othersAlbums.innerHTML = "";
				if (AlbumsArray.length == 0) {
					const row = this.othersAlbums.insertRow();
					const cell = row.insertCell();
					cell.setAttribute("colspan", "4");
					cell.appendChild(document.createTextNode("There are no albums here! "));
				} else {
					AlbumsArray.forEach(element => {
						const row = this.othersAlbums.insertRow();
						const creatorCell = row.insertCell();
						creatorCell.appendChild(document.createTextNode(element.creator_username));
						const titleCell = row.insertCell();
						titleCell.appendChild(document.createTextNode(element.title));
						titleCell.onclick = () => {
							albumView.show(element.id);
						};
						const idCell = row.insertCell();
						idCell.appendChild(document.createTextNode(element.id));
						const dateCell = row.insertCell();
						dateCell.appendChild(document.createTextNode(element.date));
					});
				};
				this.othersAlbums.style.visibility = "visible";
			};

			this.autoclick = (id) => {
				var e = new Event("click");
				for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
					if (row.cells[1].innerHTML === id) {
						row.dispatchEvent(e);
						return;
					}
				}
				for (var i = 0, row; row = this.othersAlbums.rows[i]; i++) {
					if (row.cells[1].innerHTML === id) {
						row.dispatchEvent(e);
						return;
					}
				}
				alert("autoclick failed!"); // this should never happen
			}
		}
	}
	
	class AlbumView {
		constructor(albumView) {
			this.albumView = albumView;
			this.imagesList;
			this.page = 0;

			this.registerEvents = (pageOrchestrator) => {
				this.orchestrator = pageOrchestrator;
			}

			this.reset = function () {
				this.userAlbums.style.visibility = "hidden";
				this.othersAlbums.style.visibility = "hidden";
				// or i guess i could also do the following:
				this.userAlbums.innerHTML = "";
				this.othersAlbums.innerHTML = "";
			};

			this.show = function (albumId, next) {
				var self = this;
				makeCall("GET", "Album?id=" + albumId, null, function(request) {
					if (request.readyState == XMLHttpRequest.DONE) {
						const responseJson = JSON.parse(request.responseText);
						console.log(responseJson);
						if (request.status == 200) {
							// fill the view with json content
							self.update(responseJson);
							if (next) next(); // ???
						} else {
							alert("There was an error while fetching this album from the server. " +
							"Please try again later. Error: " + responseJson.errorJson);
						}
					}
				});
			};

			this.update = function(imagesList) {
				this.imagesToDisplay = imagesList.slice(this.page*5-1, this.page*5+4);
				const imageCells = this.albumView.rows[0].cells;
				const titleCells = this.albumView.rows[1].cells;
				for (var i = 0; i < 5; i++) {
					const img = document.createElement('img');
					img.src = imagesList[i].path;
					imageCells[i].appendChild(img);
					titleCells[i].appendChild(document.createTextNode(imagesList[i].title));
				}
			}
		}
	}	
})();
