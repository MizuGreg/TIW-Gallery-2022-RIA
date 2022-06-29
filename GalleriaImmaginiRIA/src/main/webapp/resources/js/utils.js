// AJAX call

function makeCall(method, url, formData, success, fail) {
	var request = new XMLHttpRequest();
	request.onreadystatechange = () => {
		if (request.readyState == XMLHttpRequest.DONE) {
			const responseJson = JSON.parse(request.responseText);
			console.log("AJAX response: " + responseJson);
			if (request.status == 200) success(responseJson);
			else fail(responseJson);
		}
	};
	request.open(method, url);
	if (formData == null) request.send();
	else request.send(formData);
}
	