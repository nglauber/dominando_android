package dominando.android.ex16_fragments;

import java.io.Serializable;

public class Hotel implements Serializable {
    public String nome;
    public String endereco;
    public float estrelas;

    public Hotel(String nome, String endereco, float estrelas) {
        this.nome = nome;
        this.endereco = endereco;
        this.estrelas = estrelas;
    }

    @Override
    public String toString() {
        return nome;
    }
}

