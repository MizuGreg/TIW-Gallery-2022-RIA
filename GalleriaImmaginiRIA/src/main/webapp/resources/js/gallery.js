(function() {

	var pageOrchestrator = new PageOrchestrator();
	var albumsList, albumView, imageView, albumEditView;
	
	window.addEventListener("load", () => {
		pageOrchestrator.start();
		pageOrchestrator.refresh(null, null, null);
	});

	function PageOrchestrator() {
		this.start = function () {
			albumsList = new AlbumsList(
				document.getElementById("userAlbums"),
				document.getElementById("othersAlbums"),
				document.getElementById("createAlbumButton"),
				document.getElementById("customOrderButton")
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
			imageView.registerEvents(this);

			albumEditView = new AlbumEditView(
				document.getElementById("albumEditView")
			);
			albumEditView.registerEvents(this);

			// Miiiiiight not work
			document.getElementById("logoutButton").addEventListener("click", () => {
				window.sessionStorage.removeItem("username"); // client-side logout
				window.location.href = "";
			});
		};

		this.refresh = (newAlbumId, newImageId, albumEditId) => {
			albumsList.reset();
			albumsList.show();

			if (newAlbumId != -1) {
				albumView.reset();
			}
			if (newImageId != -1) {
				imageView.reset();
			}
			if (albumEditId != -1) {
				albumEditView.reset();
			}

			if (newAlbumId != null) {
				albumsList.autoclick(newAlbumId);
			}
			if (newImageId != null) {
				imageView.show(newImageId); // should be converted to automouseover
			}
			if (albumEditId != null) {
				albumEditView.show(albumEditId);
			}
		};
	}
	
	function AlbumsList(userAlbums, othersAlbums, createAlbumButton, customOrderButton) {
		this.userAlbums = userAlbums;
		this.othersAlbums = othersAlbums;
		this.createAlbumButton = createAlbumButton;
		this.customOrderButton = customOrderButton;

		this.registerEvents = (pageOrchestrator) => {
			this.orchestrator = pageOrchestrator;
			this.createAlbumButton.onclick = () => {
				this.createAlbum();
			};
			this.customOrderButton.onclick = () => {
				this.pushNewOrder();
			};
		}

		this.reset = function () {
			document.getElementById("yourDiv").style.visibility = "hidden";
			document.getElementById("othersDiv").style.visibility = "hidden";
		};

		this.show = function (next) {
			var self = this; // ugh
			makeCall("GET", "GetAlbums", null, function(request) {
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
						this.orchestrator.refresh(element.id, null, -1);
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
						this.orchestrator.refresh(element.id, null, -1);
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
			var self = this;
			makeCall("GET", "CreateAlbum", null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					console.log(responseJson);
					if (request.status == 200) {
						// todo: get new album id
					} else {
						alert("There was an error while fetching the albums from the server. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			});
			//todo: fetch new album id
			var newlyCreatedId;
			this.orchestrator.refresh(-1, null, newlyCreatedId);
		};

		this.pushNewOrder = () => {
			//todo: get all IDs into an array and send it to server
		};
	}
	
	function AlbumView(albumView, precButton, succButton) {
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
			//todo: album edit button
		}

		this.reset = function () {
			document.getElementById("albumDiv").style.visibility = "hidden";
		};

		this.show = function (albumId, next) {
			var self = this;
			makeCall("GET", "Album?id=" + albumId, null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					console.log(responseJson.imagesList); // sì c: grazie <3 
					if (request.status == 200) {
						// fill the view with json content
						self.update(responseJson.imagesList);
						if (next) next(); // ??? delete this BS
					} else {
						alert("There was an error while fetching this album from the server. " +
						"Please try again later. Error: " + responseJson.errorMessage);
					}
				}
			});
		};

		this.update = function(imagesListInput) {
			const imagesList = imagesListInput; // boh qui firefox dice che il parametro formale è dichiarato 2 volte
			const imagesToDisplay = imagesList.slice(this.page*5, this.page*5+5);
			const imageCells = this.albumView.rows[0].cells.slice(1, 6); // skips prec/succ buttons
			const titleCells = this.albumView.rows[1].cells;
			for (var i = 0; i < imagesToDisplay.length; i++) {
				const img = document.createElement('img');
				img.src = imagesToDisplay[i].path;
				imageCells[i].appendChild(img);
				titleCells[i].appendChild(document.createTextNode(imagesToDisplay[i].title));
			}
			if (imagesToDisplay.length < 5 || imagesList.length/5 == this.page-1) {
				this.succButton.style.visibility = "hidden";
			} else {
				this.succButton.style.visibility = "visible";
			}
			if (this.page == 0) {
				this.precButton.style.visibility = "hidden";
			} else {
				this.precButton.style.visibility = "visible";
			}
			this.makeModalShowable(imagesToDisplay.length);
		}

		this.previousPage = () => {
			this.page--;
			this.update();
		};

		this.nextPage = () => {
			this.page++;
			this.update();
		};

		this.makeModalShowable = (numberOfCells) => {
			const imageCells = this.albumView.rows[0].cells.slice(1, 6); // skips prec/succ buttons
			for (var i = 0; i < numberOfCells; i++) {
				image.onmouseover = () => {
					this.orchestrator.refresh(-1, this.imagesList[this.page*5+i].id, -1);
				}
			}
		};

		this.editAlbum = () => {
			//TODO
		};
	}
	
	function ImageView(imageView) {
		this.imageView = imageView;
		//TODO
		this.registerEvents = () => {};

		this.reset = () => {};

		this.show = () => {};

		this.update = () => {};
	}

	function AlbumEditView(albumEditView) {
		this.albumEditView = albumEditView;
		//TODO
		this.registerEvents = () => {};

		this.reset = () => {};

		this.show = () => {};

		this.update = () => {};
	}

})();
