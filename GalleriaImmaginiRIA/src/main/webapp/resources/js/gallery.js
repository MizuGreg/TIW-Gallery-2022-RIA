(function() {
	var pageOrchestrator = new PageOrchestrator();
	var albumsList, albumView, imageView;
	
	
	
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "login_page.jsp"; // client-side LoggedFilter
		} else {
			pageOrchestrator.start();
			pageOrchestrator.refresh(null, null);
		}
	})
	
	class PageOrchestrator {
		constructor() {
			this.currentAlbumId = null;

			this.start = function () {
				albumsList = new AlbumsList(
					document.getElementById("userAlbums"),
					document.getElementById("othersAlbums")
				);
				albumsList.registerEvents(this);

				albumView = new AlbumView(
					document.getElementById("albumView"),
					document.getElementById("precButton"),
					document.getElementById("succButton")
				);
				albumView.registerEvents(this);

				imageView = new ImageView(
					document.getElementById("imageView")
				);
				imageView.registerEvents(this); // non credo questa riga sia necessaria

				document.getElementById("logoutButton").addEventListener("click", () => {
					window.sessionStorage.removeItem("username"); // client-side logout
					window.location.href = "login_page.html";
				});
			};

			this.refresh = (newAlbumId, newImageId) => {
				albumsList.reset();
				albumView.reset();
				imageView.reset();
				albumsList.show();

				if (newAlbumId != null) {
					albumsList.autoclick(newAlbumId);
				}
				if (newImageId != null) {
					imageView.show(newImageId); // should be converted to automouseover
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
				document.getElementById("yourDiv").style.visibility = "hidden";
				document.getElementById("othersDiv").style.visibility = "hidden";
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
							"Please try again later. Error: " + responseJson.errorMessage);
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
							this.orchestrator.refresh(element.id, null);
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
							this.orchestrator.refresh(element.id, null);
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
						row.cells[0].dispatchEvent(e);
						return;
					}
				}
				for (var i = 0, row; row = this.othersAlbums.rows[i]; i++) {
					if (row.cells[2].innerHTML === id) {
						row.cells[1].dispatchEvent(e);
						return;
					}
				}
				alert("autoclick failed!"); // this should never happen
			};

			this.createAlbum = () => {
				//TODO
			};
		}
	}
	
	class AlbumView {
		constructor(albumView, precButton, succButton) {
			this.albumView = albumView;
			this.imagesList;
			this.page = 0;
			this.precButton = precButton;
			this.succButton = succButton;

			this.registerEvents = (pageOrchestrator) => {
				this.orchestrator = pageOrchestrator;
				this.precButton.onclick = () => {
					this.previousPage();
				};
				this.succButton.onclick = () => {
					this.nextPage();
				};
				const imageCells = this.albumView.rows[0].cells.slice(1, 6); // skips prec/succ buttons
			}

			this.reset = function () {
				document.getElementById("albumDiv").style.visibility = "hidden";
			};

			this.show = function (albumId, next) {
				var self = this;
				makeCall("GET", "Album?id=" + albumId, null, function(request) {
					if (request.readyState == XMLHttpRequest.DONE) {
						const responseJson = JSON.parse(request.responseText);
						console.log(responseJson); // forse è responseJson.imagesList ?
						if (request.status == 200) {
							// fill the view with json content
							self.update(responseJson);
							if (next) next(); // ??? delete this BS
						} else {
							alert("There was an error while fetching this album from the server. " +
							"Please try again later. Error: " + responseJson.errorJson);
						}
					}
				});
			};

			this.update = function(imagesList) {
				const imagesList = imagesList;
				const imagesToDisplay = imagesList.slice(this.page*5, this.page*5+5);
				const imageCells = this.albumView.rows[0].cells.slice(1, 6); // skips prec/succ buttons
				const titleCells = this.albumView.rows[1].cells;
				for (var i = 0; i < imagesToDisplay.length; i++) {
					const img = document.createElement('img');
					img.src = imagesToDisplay[i].path;
					imageCells[i].appendChild(img);
					titleCells[i].appendChild(document.createTextNode(imagesToDisplay[i].title));
				}
				if (imagesToDisplay.length < 5) {
					this.succButton.style.visibility = "hidden";
				} else {
					this.succButton.style.visibility = "visible";
				}
				if (this.page == 0) {
					this.precButton.style.visibility = "hidden";
				} else {
					this.precButton.style.visibility = "visible";
				}
				this.makeModalShowable();
			}

			this.previousPage = () => {
				this.page--;
				this.update();
			};

			this.nextPage = () => {
				this.page++;
				this.update();
			};

			this.makeModalShowable = () => {
				const imageCells = this.albumView.rows[0].cells.slice(1, 6); // skips prec/succ buttons
				for (var i = 0; i < 5; i++) {
					image.onmouseover = () => {
						this.orchestrator.refresh(null, this.imagesList[this.page*5+i].id);
					}
				}
			};

			this.editAlbum = () => {
				//TODO UGHHHHHHHHHHHH
			};
		}
	}
	
	class ImageView {
		constructor(imageView) {
			this.imageView = imageView;
			//TODO
			this.registerEvents = () => {};

			this.reset = () => {};

			this.show = () => {};

			this.update = () => {};
		}
	}
})();
