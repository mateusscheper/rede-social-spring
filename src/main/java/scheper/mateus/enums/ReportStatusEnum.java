package scheper.mateus.enums;

public enum ReportStatusEnum {

    ABERTO("Aberto"),
    EM_AVALIACAO("Em avaliação"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    ReportStatusEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}