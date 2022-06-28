(function() {

	var pageOrchestrator = new PageOrchestrator();
	var albumsList, albumView, imageView, albumEditView;
	
	window.addEventListener("load", () => {
		pageOrchestrator.start();
		pageOrchestrator.refresh(null, null, null);
	});

	function PageOrchestrator() {
		this.start = () => {
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
				document.getElementById("succButton"),
				document.getElementById("editAlbumButton")
			);
			albumView.registerEvents(this);

			imageView = new ImageView(
				document.getElementById("modalWindow"),
				document.getElementById("imageView"),
				document.getElementById("commentsSection"),
				document.getElementById("yourComment"),
				document.getElementById("newCommentButton")
			);
			imageView.registerEvents(this);

			albumEditView = new AlbumEditView(
				document.getElementById("albumEditView")
			);
			albumEditView.registerEvents(this);

			document.getElementById("logoutButton").addEventListener("click", () => {
				window.sessionStorage.removeItem("username"); // client-side logout
				window.location.href = ""; // useless now
			});
		};

		this.refresh = (newAlbumId, newImageId, albumEditId) => {
			albumsList.reset();
			albumsList.show();

			if (newAlbumId != -1) {
				albumView.reset();
				if (newAlbumId != null) {
					albumView.show(newAlbumId);
				}
			}
			if (newImageId != -1) {
				imageView.reset();
				if (newImageId != null) {
					imageView.show(newImageId);
				}
			}
			if (albumEditId != -1) {
				albumEditView.reset();
				if (albumEditId != null) {
					albumEditView.show(albumEditId);
				}
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
			this.createAlbumButton.addEventListener("click", () => {
				this.createAlbum();
			})
			this.customOrderButton.addEventListener("click", () => {
				this.pushNewOrder();
			});
		}

		this.reset = () => {
			document.getElementById("userDiv").style.display = "none";
			document.getElementById("othersDiv").style.display = "none";
		};

		this.show = (next) => {
			var self = this; // ugh
			makeCall("GET", "GetAlbums", null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
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
			document.getElementById("userDiv").style.display = "block";
			document.getElementById("othersDiv").style.display = "block";
		};

		this.updateUser = (albumsArray) => {
			this.userAlbums.innerHTML = "";
			if (albumsArray.length == 0) {
				const row = this.userAlbums.insertRow();
				const cell = row.insertCell();
				cell.setAttribute("colspan", "3");
				cell.appendChild(document.createTextNode("You have no albums yet! " +
				"Create an album with the 'Create album' button."));
			} else {
				albumsArray.forEach(element => {
					const row = this.userAlbums.insertRow();
					const titleCell = row.insertCell();
					titleCell.appendChild(document.createTextNode(element.title));
					
					titleCell.addEventListener("click", () => {
						this.orchestrator.refresh(element.id, null, null);
					});
					const idCell = row.insertCell();
					idCell.appendChild(document.createTextNode(element.id));
					const dateCell = row.insertCell();
					dateCell.appendChild(document.createTextNode(element.date));
				});
			};
			this.makeOrderable();
		};

		this.makeOrderable = () => {
			for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
				//FIXME cause it does not work
				var draggingRow = null;
				row.ondragstart = () => {
					draggingRow = event.target;
				};
				row.ondragover = () => {
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

		this.updateOthers = (albumsArray) => {
			this.othersAlbums.innerHTML = "";
			if (albumsArray.length == 0) {
				const row = this.othersAlbums.insertRow();
				const cell = row.insertCell();
				cell.setAttribute("colspan", "4");
				cell.appendChild(document.createTextNode("There are no albums here! "));
			} else {
				albumsArray.forEach(element => {
					const row = this.othersAlbums.insertRow();
					const creatorCell = row.insertCell();
					creatorCell.appendChild(document.createTextNode(element.creator_username));
					const titleCell = row.insertCell();
					titleCell.appendChild(document.createTextNode(element.title));
					titleCell.addEventListener("click", () => {
						this.orchestrator.refresh(element.id, null, null);
					});
					const idCell = row.insertCell();
					idCell.appendChild(document.createTextNode(element.id));
					const dateCell = row.insertCell();
					dateCell.appendChild(document.createTextNode(element.date));
				});
			};
		};

		this.autoclick = (id) => {
			var e = new Event("click");
			for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
				if (row.cells[1].innerHTML == id) {
					row.cells[0].dispatchEvent(e);
					return;
				}
			}
			for (var i = 0, row; row = this.othersAlbums.rows[i]; i++) {
				if (row.cells[2].innerHTML == id) {
					row.cells[1].dispatchEvent(e);
					return;
				}
			}
			alert("autoclick failed!"); // this should never happen
		};

		this.createAlbum = () => {
			var newlyCreatedAlbumId = null;
			var self = this;
			makeCall("POST", "CreateAlbum", null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						newlyCreatedAlbumId = request.albumId;
						self.orchestrator.refresh(null, null, self.newlyCreatedAlbumId);
					} else {
						alert("There was an error while fetching the albums from the server. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			});
		};

		this.pushNewOrder = () => {
			var orderedIDs = [];
			for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
				orderedIDs.push(row.cells[1].value);
			}
			var formData = new FormData();
			formData.append("albumIds", orderedIDs);
			makeCall("POST", "UpdateOrdering", formData, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						// do nothing
					} else {
						alert("There was an error while saving the custom album order. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			});
		};
	}
	
	function AlbumView(albumView, precButton, succButton, editButton) {
		this.albumView = albumView;
		this.albumId = -1;
		this.imagesList;
		this.page = 0;
		this.precButton = precButton;
		this.succButton = succButton;
		this.editButton = editButton;

		this.registerEvents = (pageOrchestrator) => {
			this.orchestrator = pageOrchestrator;
			this.precButton.addEventListener("click", () => {
				this.previousPage();
			});
			this.succButton.addEventListener("click", () => {
				this.nextPage();
			})
			this.editButton.addEventListener("click", () => {
				this.editAlbum();
			})
		}

		this.reset = () => {
			document.getElementById("albumDiv").style.display = "none";
			this.albumView.innerHTML = "";
			this.albumId = -1;
			this.page = 0;
		};

		this.show = (albumId, next) => {
			this.albumId = albumId;
			var self = this;
			makeCall("GET", "Album?id=" + albumId, null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
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
			document.getElementById("albumDiv").style.display = "block";
		};

		this.update = (imagesListInput) => {
			this.imagesList = imagesListInput; // TODO fix slice
			const imagesToDisplay = this.imagesList.slice(this.page*5, this.page*5+5);
			const imageRow = this.albumView.insertRow();
			const titleRow = this.albumView.insertRow();
			for (var i = 0; i < imagesToDisplay.length; i++) {
				const img = document.createElement("img");
				img.src = imagesToDisplay[i].path;
				img.classList.add("thumbnail");
				imageRow.insertCell().appendChild(img);
				const title = document.createTextNode(imagesToDisplay[i].title);
				titleRow.insertCell().appendChild(title);
			}

			if (imagesToDisplay.length < 5 || this.imagesList.length/5 == this.page-1) {
				this.succButton.style.display = "none";
			} else {
				this.succButton.style.display = "block";
			}
			if (this.page == 0) {
				this.precButton.style.display = "none";
			} else {
				this.precButton.style.display = "block";
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
			const imageCells = this.albumView.rows[0].cells;
			for (var i = 0; i < numberOfCells; i++) {
				if (imageCells[i].innerHTML != "") { // useless check now
					const imageId = this.imagesList[this.page*5+i].id;
					console.log(imageCells[i], imageId);
					imageCells[i].addEventListener("mouseover", () => {
						console.log(imageId + "mouseover");
						this.orchestrator.refresh(-1, imageId, -1);
					});
				}
			}
		};

		this.editAlbum = () => {
			var editAlbumId = this.albumId;
			this.orchestrator.refresh(null, editAlbumId, null);
		};
	}
	
	function ImageView(modalWindow, imageView, commentsSection, yourComment, newCommentButton) {
		this.modalWindow = modalWindow;
		this.imageView = imageView;
		this.imageId = -1;
		this.commentsSection = commentsSection;
		this.yourComment = yourComment;
		this.newCommentButton = newCommentButton;
		
		this.registerEvents = (pageOrchestrator) => {
			this.orchestrator = pageOrchestrator;
			window.addEventListener("click", () => {
				if (event.target == document.getElementById("modalWindow"))
					this.reset();
			});
			document.getElementById("closeButton").addEventListener("click", () => {
				this.reset();
			});
			this.newCommentButton.addEventListener("click", () => {
				this.addComment();
			});
		};

		this.reset = () => {
			this.modalWindow.style.display = "none";
			this.imageView.innerHTML = "";
			this.commentsSection.innerHTML = "";
			this.yourComment.value = "";
			this.imageId = -1;
		};

		this.show = (imageId) => {
			this.imageId = imageId;
			var self = this;
			makeCall("POST", "Image?id=" + imageId, null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						self.updateImage(responseJson.image);
						self.updateComments(responseJson.comments);
					} else {
						alert("There was an error while fetching image info. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			});
			this.modalWindow.style.display = "block";
		};

		this.updateImage = (image) => {
			const imageTable = document.createElement("table");
			this.imageView.appendChild(imageTable);

			const imageRow = imageTable.insertRow();
			const img = document.createElement("img");
			img.src = image.path;
			img.classList.add("fullImage");
			imageRow.insertCell().appendChild(img);
			
			imageTable.insertRow().insertCell().appendChild(document.createTextNode("Title: " + image.title));
			imageTable.insertRow().insertCell().appendChild(document.createTextNode("Date: " + image.date));
			imageTable.insertRow().insertCell().appendChild(document.createTextNode("Description: " + image.description));
			imageTable.insertRow().insertCell().appendChild(document.createTextNode("Author: " + image.uploader_username));
		};

		this.updateComments = (comments) => {
			var createCommentTable = (user, text) => {
				const commentTable = document.createElement("table");
				commentTable.createTBody().insertRow().insertCell().appendChild(document.createTextNode(text));
				commentTable.createTHead().insertRow().insertCell().appendChild(document.createTextNode(user + " said:"));
				return commentTable;
			}
			comments.forEach(comment => {
				this.commentsSection.appendChild(createCommentTable(comment.user, comment.text));
			});
		};

		this.addComment = () => {
			if (this.yourComment.value == "") return;

			var formData = new FormData();
			formData.append("username", window.sessionStorage.getItem("username"));
			formData.append("imageId", this.imageId);
			formData.append("commentText", this.yourComment.value);
			var self = this;
			makeCall("POST", "CreateComment", formData, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						self.orchestrator.refresh(-1, self.imageId, -1);
					} else {
						alert("There was an error while posting your comment. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			});
		};
	}

	function AlbumEditView(albumEditForm, albumEditList) {
		this.albumEditForm = albumEditForm;
		this.albumEditList = albumEditList;
		this.albumId = -1;

		this.registerEvents = () => {
			document.getElementById("albumEditButton").addEventListener("click", () => {
				this.sendAlbumEdit();
			});
		};

		this.reset = () => {
			document.getElementById("editDiv").style.display = "none";
			this.albumId = -1;
		};

		this.show = (albumEditId) => {
			this.albumId = albumEditId;
			var self = this;
			makeCall("GET", "GetYourImages", null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					console.log(responseJson);
					if (request.status == 200) {
						self.update(responseJson.imagesList);
					} else {
						alert("There was an error while fetching the images from the server. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			})
			document.getElementById("editDiv").style.display = "block";
		};

		this.update = (imagesList) => {
			imagesList.forEach(element => {
				const li = document.createElement("li");
				this.albumEditList.appendChild(li);

				const checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				checkbox.value = element.key.id;
				checkbox.checked = element.value;
				li.appendChild(checkbox);

				const img = document.createElement("img");
				img.src = element.key.path;
				li.appendChild(img);
			})
		};

		this.sendAlbumEdit = () => {
			var formData = new FormData(this.albumEditForm);
			formData.append("id", this.albumId);
			var self = this;

			makeCall("POST", "EditAlbum", formData, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					console.log(responseJson);
					if (request.status == 200) {
						self.orchestrator.refresh(self.albumId, null, null);
					} else {
						alert("There was an error while sending the edits to the server. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			})
		}
	}

})();
