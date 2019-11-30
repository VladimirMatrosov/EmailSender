package sample.entity;

public class User {

    private String email;
    private String subject;
    private String name;
    private String code;
    private String rules;
    private String signature;

    public User(String email, String subject, String name, String code, String rules, String signature) {
        this.email = email;
        this.subject = subject;
        this.name = name;
        this.code = code;
        this.rules = rules;
        this.signature = signature;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getRules() {
        return rules;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return email + '\t' + code;
    }
}
