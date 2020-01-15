<!DOCTYPE html>
<html>
	<head>
	</head>
	<body>
		<?php
			$i = 0;
			$total = 0;
			while($i < $_POST["length"]){
				$total = $total + $_POST[("numBox"+(i+1))];
			}
			$avg = $total/$_POST["length"];
			echo "Average: ".$avg;
			$file = fopen("average.txt","w");
			fwrite($avg);
			fclose($file);
		?>
	</body>
</html>