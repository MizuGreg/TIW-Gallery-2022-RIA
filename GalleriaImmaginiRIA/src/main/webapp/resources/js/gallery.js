(function() {
	var pageOrchestrator = new PageOrchestrator();
	var albumsList, albumView, imageView;
	
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "login_page.html"; // client-side LoggedFilter
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
					// bla bla bla
				);
				albumView.registerEvents(this);

				imageView = new ImageView(
					// bla bla bla
				);
				imageView.registerEvents(this); // non credo sia necessario

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
							self.updateUser(responseJson); // actually only want user albums out of the json
							self.updateOthers(responseJson);
						} else {
							// only fill one table with the error message?
						}
					}
				});
			};

			this.updateUser = function(AlbumsArray) {
				var row;
				this.userAlbums.innerHTML = "";
				var self = this;
				AlbumsArray.forEach(element => {
					
					row = document.createElement("tr");
				});
			};

			this.updateOthers = function(AlbumsArray) {
				this.othersAlbums.innerHTML = "";
			};
		}
	}
	
	function AlbumView() {
		// TODO
	}
	
})();
