package scheper.mateus.enums;

public enum StatusAmizadeEnum {

    PENDENTE_ACEITE("Pendente de aceite"),
    PENDENTE_RESPOSTA("Pendente de resposta"),
    AMIGOS("Amigos");

    private final String status;

    StatusAmizadeEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}