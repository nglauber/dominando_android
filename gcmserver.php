<?php
$acao = $_POST['acao'];
$dispositivos = "dispositivos.txt";

if ($acao == "registrar") {
	$fp = fopen($dispositivos, "a+") or die("Erro ao abrir arquivo!");
	
	$registrationId = $_POST['regId'] ."\n";
	
	fwrite($fp, $registrationId) or die("Erro ao escrever no arquivo!");
	fclose($fp);

} else if ($acao == "enviar") {
	$mensagem = $_POST['mensagem'];

	if (file_exists($dispositivos)) {
		$linhas = file($dispositivos, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
	}

	$data = array(
		"registration_ids" => $linhas,
		"data" => array (
			"mensagem" => $mensagem
		)
	);

	$NL = "\r\n";
	$options = array(
		'http' => array(
			'method'  => 'POST',
			'content' => json_encode( $data ),
			'header'  =>  "Content-Type: application/json". $NL.
						    "Authorization: key=COLOQUE_SUA_SENDER_AUTH_TOKEN" . $NL
		  )
	);
 	$url = "https://android.googleapis.com/gcm/send";
	$context = stream_context_create($options);
	$result = file_get_contents($url, false, $context);

	var_dump($result);	
}
?>
