(function() {
	console.log("hello");
	loginFormPath = "CheckLogin";
	signupFormPath = "CheckSignup";
	
	var loginForm = new LoginForm(document.getElementById("loginForm"));
	var signupForm = new SignupForm(document.getElementById("signupForm"));
	
	window.addEventListener("load", () => {
		console.log("add event listener load");
		loginForm.registerClick();
		signupForm.registerClick();
	})
	
	function makeCall(method, url, formElement, callback, reset = false) {
		console.log("call made");
		var request = new XMLHttpRequest();
		request.onreadystatechange = () => callback(request);
		request.open(method, url);
		if (formElement == null) request.send();
		else {
			console.log("Sending request from form");
			request.send(new FormData(formElement));
			if (reset) {
				formElement.reset();
			}
		}
	}
	
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
							// if we're here it means there was NO redirect, thus login failed. ajax handles redirects silently
							alert(request.responseText);
						}
					})
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
								// if we're here it means there was NO redirect, thus login failed. ajax handles redirects silently
								alert(request.responseText);
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
