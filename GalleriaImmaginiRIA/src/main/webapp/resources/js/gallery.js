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
				document.getElementById("albumEditForm"),
				document.getElementById("albumEditTitle"),
				document.getElementById("albumEditList")
			);
			albumEditView.registerEvents(this);

			document.getElementById("logoutButton").addEventListener("click", () => {
				window.sessionStorage.removeItem("username"); // client-side logout
				window.location.href = ""; // superseded by HTML-side redirect
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

		this.show = () => {
			makeCall("GET", "GetAlbums", null,
			(response) => {
				this.updateUser(response.userAlbums);
				this.updateOthers(response.othersAlbums);
			}, (response) => {
				alert("There was an error while fetching the albums from the server. " +
				"Please try again later. Error: " + response.errorMessage);
			});
			document.getElementById("userDiv").style.display = "block";
			this.customOrderButton.style.display = "block";
			document.getElementById("othersDiv").style.display = "block";
		};

		this.updateUser = (albumsArray) => {
			this.userAlbums.innerHTML = "";
			if (albumsArray.length == 0) {
				const row = this.userAlbums.insertRow();
				const cell = row.insertCell();
				cell.setAttribute("colspan", "3");
				cell.appendChild(document.createTextNode("You have no albums yet! " +
				"Create an album with the 'Create a new album' button."));
				this.customOrderButton.style.display = "none";
			} else {
				if (albumsArray.length == 1) this.customOrderButton.style.display = "none";
				albumsArray.forEach(element => {
					const row = this.userAlbums.insertRow();
					row.draggable = true;
					const titleCell = row.insertCell();
					titleCell.appendChild(document.createTextNode(element.title));
					
					titleCell.addEventListener("click", () => {
						this.orchestrator.refresh(element.id, null, null);
						albumView.showEditButton(false);
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
			var draggingRow = null;
			for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
				row.addEventListener("dragstart", (event) => {
					draggingRow = event.target;
				});
				row.addEventListener("dragover", (event) => {
					event.preventDefault();
					var t = event.target;
					const rows = Array.from(t.parentNode.parentNode.children);

					if (rows.indexOf(t.parentNode) > rows.indexOf(draggingRow))
						t.parentNode.after(draggingRow);
					else
						t.parentNode.before(draggingRow);
				});
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
						albumView.showEditButton(true);
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
			var setTitle = () => {
				albumEditView.setTitle("newAlbum");
			};
			makeCall("POST", "CreateAlbum", null,
			(response) => {
				this.orchestrator.refresh(null, null, response.albumId);
				setTitle();
			}, (response) => {
				alert("There was an error while fetching the albums from the server. " +
				"Error: " + response.errorMessage);
			});
		};

		this.pushNewOrder = () => {
			var orderedIDs = [];
			for (var i = 0, row; row = this.userAlbums.rows[i]; i++) {
				orderedIDs.push(row.cells[1].textContent);
			}
			var formData = new FormData();
			formData.append("albumIds", orderedIDs);
			makeCall("POST", "UpdateOrdering", formData,
			(response) => {
				alert("New album order saved.");
			}, (response) => {
				alert("There was an error while saving the custom album order. " +
				"Error: " + response.errorMessage);
				this.orchestrator.update(-1, -1, -1);
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

		this.show = (albumId) => {
			this.albumId = albumId;
			makeCall("GET", "Album?id=" + albumId, null,
			(response) => {
				this.update(response.albumTitle, response.imagesList);
			}, (response) => {
				alert("There was an error while fetching this album from the server. " +
				"Please try again later. Error: " + response.errorMessage);
			});
			document.getElementById("albumDiv").style.display = "block";
		};

		this.update = (title, imagesList) => {
			document.getElementById("albumNameHeader").textContent = title;
			if (imagesList != null) {
				this.imagesList = imagesList;
				if (imagesList.length == 0) { // album is empty
					const row = this.albumView.insertRow();
					row.insertCell().appendChild(document.createTextNode("This album is empty."));
					this.precButton.style.display = "none";
					this.succButton.style.display = "none";
					return;
				}
			}
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

			if (imagesToDisplay.length < 5 || this.imagesList.length/5 == this.page+1) {
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
			this.albumView.innerHTML = "";
			this.update();
		};

		this.nextPage = () => {
			this.page++;
			this.albumView.innerHTML = "";
			this.update();
		};

		this.makeModalShowable = (numberOfCells) => {
			const imageCells = this.albumView.rows[0].cells;
			for (var i = 0; i < numberOfCells; i++) {
				const imageId = this.imagesList[this.page*5+i].id;
				imageCells[i].childNodes[0].addEventListener("mouseover", () => {
					this.orchestrator.refresh(-1, imageId, -1);
				});
			}
		};

		this.showEditButton = (showBool) => {
			if (showBool) this.editButton.style.visibility = "hidden";
			else this.editButton.style.visibility = "visible";
		}

		this.editAlbum = () => {
			var editAlbumId = this.albumId;
			this.orchestrator.refresh(null, null, editAlbumId);
			const title = document.getElementById("albumNameHeader").textContent;
			albumEditView.setTitle(title);
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
			window.addEventListener("click", (event) => {
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
			makeCall("GET", "Image?id=" + imageId, null,
			(response) => {
				this.updateImage(response.image);
				this.updateComments(response.comments);
			}, (response) => {
				alert("There was an error while fetching image info. " +
				"Error: " + response.errorMessage);
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
			imageTable.insertRow().insertCell().appendChild(document.createTextNode("Uploader: " + image.uploader_username));
		};

		this.updateComments = (comments) => {
			var createCommentTable = (user, text) => {
				const commentTable = document.createElement("table");
				const commentText = document.createElement("pre"); //preformatted text => for newlines
				commentText.innerHTML = text;
				commentTable.createTBody().insertRow().insertCell().appendChild(commentText);
				commentTable.createTHead().insertRow().insertCell().appendChild(document.createTextNode(user + " said:"));
				return commentTable;
			}
			comments.forEach(comment => {
				this.commentsSection.appendChild(document.createElement("br"));
				this.commentsSection.appendChild(createCommentTable(comment.user, comment.text));
			});
		};

		this.addComment = () => {
			if (this.yourComment.value == "") return;

			var formData = new FormData();
			formData.append("username", window.sessionStorage.getItem("username"));
			formData.append("imageId", this.imageId);
			formData.append("commentText", this.yourComment.value);
			makeCall("POST", "CreateComment", formData,
			(response) => {
				this.orchestrator.refresh(-1, this.imageId, -1);
			}, (response) => {
				alert("There was an error while posting your comment. " +
				"Error: " + response.errorMessage);
			});
		};
	}

	function AlbumEditView(albumEditForm, albumEditTitle, albumEditList) {
		this.albumEditForm = albumEditForm;
		this.albumEditTitle = albumEditTitle;
		this.albumEditList = albumEditList;
		this.albumId = -1;

		this.registerEvents = (pageOrchestrator) => {
			this.orchestrator = pageOrchestrator;
			document.getElementById("albumEditButton").addEventListener("click", () => {
				this.sendAlbumEdit();
			});
		};

		this.reset = () => {
			document.getElementById("editDiv").style.display = "none";
			this.albumEditList.innerHTML = "";
			this.albumEditTitle.value = "";
			this.albumId = -1;
		};

		this.show = (albumEditId) => {
			this.albumId = albumEditId;
			makeCall("GET", "GetYourImages?id=" + albumEditId, null,
			(response) => {
				this.update(response.imagesList, response.isPresentList);
			}, (response) => {
				alert("There was an error while fetching the images from the server. " +
				"Error: " + response.errorMessage);
			});
			document.getElementById("editDiv").style.display = "block";
		};

		this.update = (imagesList, isPresentList) => {
			if (imagesList.length == 0) {
				const liNode = document.createElement("li");
				this.albumEditList.appendChild(liNode);
				liNode.appendChild(document.createTextNode("You have no images to add to this album."));
			} else {
				for (var i = 0; i < imagesList.length; i++) {
					const image = imagesList[i], isPresent = isPresentList[i];
					const liNode = document.createElement("li");
					this.albumEditList.appendChild(liNode);

					const checkbox = document.createElement("input");
					checkbox.type = "checkbox";
					checkbox.name = "checkedImages";
					checkbox.value = image.id;
					checkbox.checked = isPresent;
					liNode.appendChild(checkbox);

					const imgNode = document.createElement("img");
					imgNode.src = image.path;
					imgNode.classList.add("thumbnail");
					liNode.appendChild(imgNode);
				}
			}
		};

		this.setTitle = (oldTitle) => {
			this.albumEditTitle.value = oldTitle;
		};

		this.sendAlbumEdit = () => {
			if (!this.albumEditForm.checkValidity()) { // check title not null
				this.albumEditForm.reportValidity();
				return;
			}
			var formData = new FormData(this.albumEditForm);
			formData.append("id", this.albumId);
			makeCall("POST", "EditAlbum", formData,
			(response) => {
				this.orchestrator.refresh(this.albumId, null, null);
			}, (response) => {
				alert("There was an error while sending the edits to the server. " +
				"Error: " + response.errorMessage);
			});
		}
	}

})();
