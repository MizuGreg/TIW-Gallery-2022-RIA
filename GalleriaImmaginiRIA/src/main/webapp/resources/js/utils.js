// AJAX call

function makeCall(method, url, formElement, callback, reset = false) {
	console.log("AJAX call made");
	var request = new XMLHttpRequest();
	request.onreadystatechange = () => callback(request);
	request.open(method, url);
	if (formElement == null) request.send();
	else {
		var formData = new FormData(formElement);
		request.send(formData);
		if (reset) {
			formElement.reset();
		}
	}
}
	