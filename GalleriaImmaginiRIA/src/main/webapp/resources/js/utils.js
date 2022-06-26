// AJAX call

function makeCall(method, url, formElement, callback, reset = false) {
	console.log("AJAX call made");
	var request = new XMLHttpRequest();
	request.onreadystatechange = () => callback(request);
	request.open(method, url);
	if (formElement == null) request.send();
	else {
		console.log("Sending request (makeCall)");
		console.log(formElement);
		console.log(new FormData(formElement));
		request.send(new FormData(formElement));
		if (reset) {
			formElement.reset();
		}
	}
}
	