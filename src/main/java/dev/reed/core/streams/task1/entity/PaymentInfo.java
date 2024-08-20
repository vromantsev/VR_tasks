package dev.reed.core.streams.task1.entity;

public class PaymentInfo {

    private CardType cardType;
    private String cardNumber;
    private String securityCode;

    public PaymentInfo() {}

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public enum CardType {
        VISA,
        MASTERCARD
    }
}
