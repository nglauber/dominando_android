<?php
if ($_FILES["arquivo"]["error"] > 0) {
	// Bad request
	http_response_code(400);
} else {
	// $_FILES["arquivo"]["name"]
	// $_FILES["arquivo"]["type"] 
	// $_FILES["arquivo"]["size"]
	$arquivo_destino = "upload/" . $_POST["titulo"] .".jpg";
	
	if (file_exists($arquivo_destino)) {
		// Arquivo já existe
		http_response_code(501);
    } else {
		move_uploaded_file(
			$_FILES["arquivo"]["tmp_name"],
			$arquivo_destino);
	    http_response_code(200);
    }  
}
?>