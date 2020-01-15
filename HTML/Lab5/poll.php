<!DOCTYPE>
<html>
	<head>
		<title>Poll</title>
		<link rel="stylesheet" type="text/css" href="style.css">
	</head>
	<body>
		<table>
			<tr>
				<td>
					<img src="images/banner.png" align="middle"></img>
				</td>
			</tr>
			<tr>
				<td>
					<form name="submit" id="submit" action="results.php" method="post">
						<h1>Which style should we study next?</h1>
						<h2>
						<input type="radio" name="Style" id="tiger" checked>Tiger<br>
						<input type="radio" name="Style" id="snake">Snake<br>
						<input type="radio" name="Style" id="mantis">Mantis<br>
						<input type="radio" name="Style" id="crane">Crane<br>
						<input type="radio" name="Style" id="monkey">Monkey<br>
						<input type="submit">
						</h2>
					</form>
				</td>
			</tr>
		</table>
	</body>
</html>