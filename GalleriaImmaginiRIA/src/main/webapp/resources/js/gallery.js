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
			var self = this; // ugh
			makeCall("GET", "GetAlbums", null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						// fill yourAlbums and othersAlbums with json content
						self.updateUser(responseJson.userAlbums);
						self.updateOthers(responseJson.othersAlbums);
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
			var newlyCreatedAlbumId = null;
			var self = this;
			var setTitle = () => {
				albumEditView.setTitle("Unnamed album");
			}
			makeCall("POST", "CreateAlbum", null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						newlyCreatedAlbumId = responseJson.albumId;
						self.orchestrator.refresh(null, null, newlyCreatedAlbumId);
						setTitle();
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
				orderedIDs.push(row.cells[1].textContent);
			}
			console.log(orderedIDs);
			var formData = new FormData();
			formData.append("albumIds", orderedIDs);
			makeCall("POST", "UpdateOrdering", formData, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						alert("New album order saved.");
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

		this.show = (albumId) => {
			this.albumId = albumId;
			var self = this;
			makeCall("GET", "Album?id=" + albumId, null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					if (request.status == 200) {
						// fill the view with json content
						self.update(responseJson.title, responseJson.imagesList);
					} else {
						alert("There was an error while fetching this album from the server. " +
						"Please try again later. Error: " + responseJson.errorMessage);
					}
				}
			});
			document.getElementById("albumDiv").style.display = "block";
		};

		this.update = (title, imagesListInput) => {
			// document.getElementById("albumNameHeader").value = title;
			if (imagesListInput != null)
				this.imagesList = imagesListInput;
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
				if (imageCells[i].innerHTML != "") { // useless check now
					const imageId = this.imagesList[this.page*5+i].id;
					console.log(imageCells[i], imageId);
					imageCells[i].childNodes[0].addEventListener("mouseover", () => {
						console.log(imageId + "mouseover");
						this.orchestrator.refresh(-1, imageId, -1);
					});
				} else console.log("did the impossible!");
			}
		};

		this.showEditButton = (showBool) => {
			console.log("showeditbutton " + showBool);
			if (showBool) this.editButton.style.visibility = "hidden";
			else this.editButton.style.visibility = "visible";
		}

		this.editAlbum = () => {
			var editAlbumId = this.albumId;
			this.orchestrator.refresh(null, null, editAlbumId);
			const title = document.getElementById("albumNameHeader").value;
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
			makeCall("GET", "Image?id=" + imageId, null, function(request) {
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
			this.albumId = -1;
		};

		this.show = (albumEditId) => {
			this.albumId = albumEditId;
			var self = this;
			makeCall("GET", "GetYourImages?id=" + albumEditId, null, function(request) {
				if (request.readyState == XMLHttpRequest.DONE) {
					const responseJson = JSON.parse(request.responseText);
					console.log(responseJson);
					if (request.status == 200) {
						self.update(responseJson.imagesMap);
					} else {
						alert("There was an error while fetching the images from the server. " +
						"Error: " + responseJson.errorMessage);
					}
				}
			})
			document.getElementById("editDiv").style.display = "block";
		};

		this.update = (imagesMap) => { // how to iterate through this???
			for (const [image, isPresent] of imagesMap) {
				const liNode = document.createElement("li");
				this.albumEditList.appendChild(liNode);

				const checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				checkbox.value = image.id;
				checkbox.checked = isPresent;
				liNode.appendChild(checkbox);

				const imgNode = document.createElement("img");
				imgNode.src = image.path;
				liNode.appendChild(imgNode);
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
