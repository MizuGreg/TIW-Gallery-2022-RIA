(function() {
	
	var loginForm = new LoginForm(document.getElementById("loginForm"));
	var signupForm = new SignupForm(document.getElementById("signupForm"));
	
	window.addEventListener("load", () => {
		loginForm.registerClick();
		signupForm.registerClick();
	})
	
	function LoginForm(formHtmlElement) {
		this.elements = formHtmlElement.elements;
		
		this.registerClick = function() {
			this.elements["loginButton"].addEventListener("click", (e) => {
				var form = e.target.closest("form"); // looks for the form near where the event is

				if (form.checkValidity()) {
					var formData = new FormData(form);
					makeCall("POST", "LoginCheck", formData, (response) => {
						sessionStorage.setItem("username", response.username);
						window.location.href = "Galleria";
					}, (response) => {
						alert(response.errorMessage);
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
						var formData = new FormData(form);
						makeCall("POST", "SignupCheck", formData, (response) => {
							sessionStorage.setItem("username", response.username);
							window.location.href = "Galleria";
						}, (response) => {
							alert(response.errorMessage);
						});
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
