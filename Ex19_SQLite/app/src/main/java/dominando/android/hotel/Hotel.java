package dominando.android.hotel;

import java.io.Serializable;

public class Hotel implements Serializable {
    public long id;
    public String nome;
    public String endereco;
    public float estrelas;

    public Hotel(long id, String nome, String endereco, float estrelas) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.estrelas = estrelas;
    }

    public Hotel(String nome, String endereco, float estrelas) {
        this(0, nome, endereco, estrelas);
    }

    @Override
    public String toString() {
        return nome;
    }
}

