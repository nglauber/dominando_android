package dominando.android.ex41_wear;

public interface Constantes {
    String TAG = "DominandoAndroid";
    String CAMINHO_DADOS = "/dados";
    String MSG_NAVEGACAO = "/navegacao";
    String MSG_POSICAO   = "/posicao" ;
    String MSG_SAIR      = "/sair" ;

    String EXTRA_POSICAO_ATUAL = "posicaoAtual";
    String EXTRA_TOTAL_IMAGENS = "total";
    String EXTRA_IMAGEM        = "imagem";

    String ACAO_PROXIMO = "dominando.android.ACAO_PROXIMO";
    String ACAO_ANTERIOR = "dominando.android.ACAO_ANTERIOR";
    String ACAO_DADOS_ALTERADOS = "dominando.android.ACAO_DADOS_ALTERADOS";
    String ACAO_SAIR = "dominando.android.ACAO_SAIR";

    byte NAVEGACAO_PROXIMO = -2;
    byte NAVEGACAO_ANTERIOR = -4;

    int ID_NOTIFICACAO = 1201;
}
