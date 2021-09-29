package scheper.mateus.enums;

public enum ReacaoEnum {

    CURTIDA("Curtida", "assets/like.svg", "assets/like2.svg"),
    AMAR("Amar", "assets/heart.svg", "assets/heart2.svg");

    private final String nome;

    private final String icone;

    private final String iconeMarcado;

    ReacaoEnum(String nome, String pathIcone, String iconeMarcado) {
        this.nome = nome;
        this.icone = pathIcone;
        this.iconeMarcado = iconeMarcado;
    }

    public static ReacaoEnum parse(String tipo) {
        for (ReacaoEnum reacaoEnum : values()) {
            if (reacaoEnum.getNome().equals(tipo))
                return reacaoEnum;
        }
        return null;
    }

    public String getNome() {
        return nome;
    }

    public String getIcone() {
        return icone;
    }

    public String getIconeMarcado() {
        return iconeMarcado;
    }
}