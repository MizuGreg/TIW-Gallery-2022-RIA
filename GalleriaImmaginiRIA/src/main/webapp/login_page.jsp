<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1">
<title>PF15 - Log in/sign up</title>
<link rel="stylesheet" type="text/css" media="all" href="css/login_page.css" />
<script type="text/javascript" src="js/utils.js" charset="utf-8" defer></script>
<script type="text/javascript" src="js/login_page.js" charset="utf-8" defer></script> <!-- remember: defer means the script is fetched
in parallel to parsing but evaluated after parsing has finished -->

</head>

<body>
	<h1>
		Welcome to the PoliMi image gallery.<br>Sign up or sign in.
	</h1>

	<fieldset>
  		<legend>Login:</legend>
			<form id="loginForm" action="#" method="POST">
				<table>
					<tr>
						<td>Username:</td>
						<td><input type="text" name="username" required></td>
					</tr>
					<tr>
						<td>Password:</td>
						<td><input type="password" name="loginPassword" required></td>
					</tr>
					<tr>
						<td></td>
						<td><input type="button" name="loginButton" value="Log in"></td>
					</tr>
				</table>
			</form>
	</fieldset>

	<fieldset>
  		<legend>Signup:</legend>
			<form id="signupForm" action="#" method="POST">
				<table>
					<tr>
						<td>Username:</td>
						<td><input type="text" name="username" required></td>
					</tr>
					<tr>
						<td>Email:</td>
						<td><input type="email" name="email" required></td>
					</tr>
					<tr>
						<td>Password:</td>
						<td><input type="password" name="signupPassword" required></td>
					</tr>
					<tr>
						<td>Repeat password:</td>
						<td><input type="password" name="repeatPassword" required>
						</td>
					</tr>
					<tr>
						<td></td>
						<td><input type="button" name="signupButton" value="Sign up"></td>
					</tr>
				</table>
			</form>
	</fieldset>
	
</body>
</html>
