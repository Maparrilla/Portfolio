<!DOCTYPE html>
<html>
    <head>
        <script>
            function create(){
		if(document.getElementById("numberBox").value < 100){
                	var form = document.getElementById("numberForm");
			while(formElement.hasChildNodes()){
				formElement.removeChild(formElement.firstChild);
			}
			var i;
			i = 0;
			var formElement = document.createElement("form");
			formElement.setAttribute("method","post");
			formElement.setAttribute("action","output.php");
			var length = document.createElement();
			length.value = document.getElementById("numberBox").value;
			length.type = "number";
			length.name = "length";
			formElement.appendChild(length);
			var numBoxes[document.getElementById("numberBox").value];
                	while(i < document.getElementById("numberBox").value){
				numBoxes[i] = document.createElement("input");
				numBoxes[i].type = "number"
				numBoxes[i].name = ("numBox"+(i+1))
				form.appendChild(numBoxes[i]);
				formElement.appendChild(numBoxes[i]);
				i++;
			}
			var btnSubmit = document.createElement("input");
			btnSubmit.type = "submit";
			btnSubmit.value = "Submit";
			form.appendChild(btnSubmit);
			formElement.appendChild(btnSubmit);
		}else{
			document.getElementById("numberBox").innerHTML = "Too Large!";
		}
            }
        </script>
    </head>
    <body>
	Enter a Number:<br>
        <input id="numberBox" type="number">
        <button type="button" onclick="create()">Enter</button>
	<br>
	<p id="numberForm">
	</p>
	<?php
		$file = fopen("average.txt","r") or die("Average not completed yet!");
		$avg = fgets($file);
		echo "Previous Average: ".$avg;
		fclose($file);
	?>
    </body>
</html>