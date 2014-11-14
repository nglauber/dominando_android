<?php
$metodoHttp = $_SERVER['REQUEST_METHOD'];
$banco = "banco.txt";
$autoInc = "ids.txt";
$delimitador = "#";

if ($metodoHttp == 'POST') {
	$json = json_decode(file_get_contents('php://input'));
	$nome     = $json->{'nome'};
	$endereco = $json->{'endereco'};
	$estrelas = $json->{'estrelas'};

	$id = 0;
	if (file_exists($autoInc)) {
		$id = file_get_contents($autoInc, "a");
	}
	file_put_contents($autoInc, ++$id);

	$linha = $id .$delimitador. $nome .$delimitador. $endereco .$delimitador. $estrelas ."\n";
	$fp = fopen($banco, "a") or die("Erro ao abrir arquivo!");
	fwrite($fp, $linha) or die("Erro ao escrever no arquivo!");
	fclose($fp);
	$jsonRetorno = array("id"=>$id);
	echo json_encode($jsonRetorno);

} else if ($metodoHttp == 'GET') {
	$jsonArray = array();
	if (file_exists($banco)) {
		$linhas = file($banco, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
		foreach ($linhas as $linhaNum => $linha) {
			$valoresLinha = explode($delimitador, $linha);
			$jsonLinha = array(
				"id"       => $valoresLinha[0],
				"nome"     => $valoresLinha[1],
				"endereco" => $valoresLinha[2],
				"estrelas" => (float)$valoresLinha[3]);

			$jsonArray[] = $jsonLinha;
		}
	}
	echo json_encode($jsonArray);

} else if ($metodoHttp == 'PUT') {
	$json  = json_decode(file_get_contents('php://input'));
	$id       = $json->{'id'};
	$nome     = $json->{'nome'};
	$endereco = $json->{'endereco'};
	$estrelas = $json->{'estrelas'};

	$linhaAtualizada = $id .$delimitador .$nome .$delimitador .$endereco .$delimitador .$estrelas;

	$linhas = file($banco, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

	$conteudo = "";
	foreach ($linhas as $linhaNum => $linha) {
		$valoresLinha = explode($delimitador, $linha);
		if ($valoresLinha[0] == $id) {
			$linha = $linhaAtualizada;
		}
		$conteudo .= $linha ."\n";
	}
	file_put_contents($banco, $conteudo);
	$jsonRetorno = array("id"=>$id);
	echo json_encode($jsonRetorno);

} else if ($metodoHttp == 'DELETE') {
	$segments = explode("/", $_SERVER["REQUEST_URI"]);
	$id = $segments[count($segments)-1];

	$linhas = file($banco, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

	$conteudo = "";
	foreach ($linhas as $linhaNum => $linha) {
		$valoresLinha = explode($delimitador, $linha);
		if ($valoresLinha[0] == $id) {
			continue;
		}
		$conteudo .= $linha ."\n";
	}
	file_put_contents($banco, $conteudo);
	$jsonRetorno = array("id"=>$id);
	echo json_encode($jsonRetorno);
}
?>
