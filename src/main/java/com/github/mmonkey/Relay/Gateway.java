package com.github.mmonkey.Relay;

public class Gateway {

    private String name;
    private String emailAddress;
    private String username;
    private String password;
    private String host;
    private int port;
    private boolean ssl;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean sslEnabled() {
        return this.ssl;
    }

    public void sslEnabled(boolean enabled) {
        this.ssl = enabled;
    }

    public boolean isValid() {

        return (!(this.name == null || this.username == null || this.password == null || this.host == null || this.port == 0));

    }

    public Gateway() {
    }

}
