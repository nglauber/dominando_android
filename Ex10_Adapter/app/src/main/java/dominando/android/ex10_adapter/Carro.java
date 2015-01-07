package dominando.android.ex10_adapter;

public class Carro  {
    public String modelo;
    public int ano;
    public int fabricante; // 0=VW;1=GM;2=Fiat;3=Ford
    public boolean gasolina;
    public boolean etanol;

    public Carro(String modelo, int ano, int fabricante,
                 boolean gasolina, boolean etanol) {

        this.modelo = modelo;
        this.ano = ano;
        this.fabricante = fabricante;
        this.gasolina = gasolina;
        this.etanol = etanol;
    }
}
