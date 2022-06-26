(function() {

	console.log("hello");
	loginFormPath = "LoginCheck";
	signupFormPath = "SignupCheck";
	
	var loginForm = new LoginForm(document.getElementById("loginForm"));
	var signupForm = new SignupForm(document.getElementById("signupForm"));
	
	window.addEventListener("load", () => {
		console.log("add event listener load");
		loginForm.registerClick();
		signupForm.registerClick();
	})
	
	function LoginForm(formHtmlElement) {
		this.elements = formHtmlElement.elements;
		
		this.registerClick = function() {
			this.elements["loginButton"].addEventListener("click", (e) => {
				console.log("clicked on login button");
				var form = e.target.closest("form"); // looks for the form near where the event is

				if (form.checkValidity()) {
					makeCall("POST", loginFormPath, form, 
					function(request) {
						if (request.readyState == XMLHttpRequest.DONE) {
							const responseJson = JSON.parse(request.responseText);
							console.log(responseJson);
							if (request.status == 200) {
								sessionStorage.setItem("username", responseJson.usernameJson);
								window.location.href = "gallery.jsp";
							} else {
								alert(responseJson.errorMessage);
							}
						}
					});
				} else {
					form.reportValidity();
				}
			});
		}
	}
	
	function SignupForm(formHtmlElement) {
		this.elements = formHtmlElement.elements;
		
		this.registerClick = function() {
			this.elements["signupButton"].addEventListener("click", (e) => {
				var form = e.target.closest("form"); // looks for the form near where the event is
				
				if (form.checkValidity()) {
					if (this.elements["signupPassword"].value == this.elements["repeatPassword"].value) {
						makeCall("POST", loginFormPath, form, 
						function(request) {
							if (request.readyState == XMLHttpRequest.DONE) {
								const responseJson = JSON.parse(request.responseText);
								console.log(responseJson);
								if (request.status == 200) {
									sessionStorage.setItem("username", responseJson.usernameJson);
									window.location.href = "gallery.js";
								} else {
									alert(responseJson.errorMessage);
								}
							}
						})
					} else {
						alert("The passwords don't match, please check your input.")
					}
				} else {
					form.reportValidity();
				}
			});
		}
	}
})();
