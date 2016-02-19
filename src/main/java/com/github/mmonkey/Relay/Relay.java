package com.github.mmonkey.Relay;

import com.github.mmonkey.Relay.Commands.*;
import com.github.mmonkey.Relay.Events.PlayerChangeNameListener;
import com.github.mmonkey.Relay.Services.*;
import com.github.mmonkey.Relay.Utilities.EncryptionUtil;
import com.github.mmonkey.Relay.Utilities.FileUtils;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Plugin(id = Relay.ID, name = Relay.NAME, version = Relay.VERSION)
public class Relay {

    protected static final String ID = "Relay";
    protected static final String NAME = "Relay";
    protected static final String VERSION = "1.0.2-3.0.0";

    /**
     * Relay
     */
    private static Relay instance;

    @Inject
    private Game game;

    @Inject
    private static Logger logger;

    private DefaultConfigStorageService defaultConfigService;
    private GatewayStorageService gatewayStorageService;
    private ContactStorageService contactStorageService;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    private File templateDir;

    private String defaultTemplate;

    public Game getGame() {
        return this.game;
    }

    public static Logger getLogger() {
        return logger;
    }

    public DefaultConfigStorageService getDefaultConfigService() {
        return this.defaultConfigService;
    }

    public GatewayStorageService getGatewayStorageService() {
        return this.gatewayStorageService;
    }

    public ContactStorageService getContactStorageService() {
        return this.contactStorageService;
    }

    public File getConfigDir() {
        return this.configDir;
    }

    public File getTemplateDir() {
        return this.templateDir;
    }

    public String getDefaultTemplate() {
        return this.defaultTemplate;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {

        Relay.instance = this;
        Relay.logger = LoggerFactory.getLogger(Relay.NAME);
        getLogger().info(String.format("Starting up %s v%s.", Relay.NAME, Relay.VERSION));

        if (!this.configDir.isDirectory()) {
            this.configDir.mkdirs();
        }

        this.templateDir = new File(this.configDir, "/templates");

        if (!this.templateDir.isDirectory()) {
            this.templateDir.mkdirs();
        }

        FileUtils.copyResourcesRecursively(this.getClass().getResource("/templates"), this.templateDir);

        this.defaultConfigService = new DefaultConfigStorageService(this, this.configDir);
        this.gatewayStorageService = new GatewayStorageService(this, this.configDir);
        this.contactStorageService = new ContactStorageService(this, this.configDir);

        this.defaultConfigService.load();
        this.gatewayStorageService.load();
        this.contactStorageService.load();

        this.loadDefaultTemplate();

        try {

            this.saveSensitiveData();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Listener
    public <T> void init(GameInitializationEvent event) {

        this.game.getServiceManager().setProvider(this, RelayService.class, new MessageRelayService<T>(this));
        this.game.getServiceManager().setProvider(this, TemplatingService.class, new HTMLTemplatingService());

        HashMap<List<String>, CommandSpec> registerSubcommands = new HashMap<List<String>, CommandSpec>();
        HashMap<List<String>, CommandSpec> relaySubcommands = new HashMap<List<String>, CommandSpec>();

        /**
         * /register email [[-a:accept] [-d:decline]] <emailAddress>
         */
        registerSubcommands.put(Collections.singletonList("email"), CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Register your email address."))
                .extendedDescription(Text.of("If registered, you can receive emails from this server."))
                .executor(new RegisterEmailSubcommand(this))
                .arguments(GenericArguments.flags().flag("a").flag("d").buildWith(GenericArguments.string(Text.of("emailAddress"))))
                .build());

        /**
         * /register phone [[-a:accept] [-d:decline]] <phoneNumber> [carrier]
         */
        registerSubcommands.put(Collections.singletonList("phone"), CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Register your phone number."))
                .extendedDescription(Text.of("If registered, you can receive text messages from this server."))
                .executor(new RegisterPhoneSubcommand(this))
                .arguments(GenericArguments.seq(
                        GenericArguments.flags().flag("a").flag("d").buildWith(GenericArguments.string(Text.of("phoneNumber"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("carrier")))))
                .build());

        /**
         * /register activate <code>
         */
        registerSubcommands.put(Collections.singletonList("activate"), CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Activate your contact method."))
                .extendedDescription(Text.of("Enter the code from your verification message to activate that contact method."))
                .executor(new RegisterActivateSubcommand(this))
                .arguments(GenericArguments.string(Text.of("code")))
                .build());

        /**
         * /register
         */
        CommandSpec registerCommand = CommandSpec.builder()
                .description(Text.of("Register your email or phone number."))
                .extendedDescription(Text.of("If registered, you can recieve emails or text messages from this server."))
                .executor(new RegisterCommand(this))
                .children(registerSubcommands)
                .build();

        /**
         * /relay carriers [[-s:select] [-u:update]] [name]
         */
        relaySubcommands.put(Collections.singletonList("carriers"), CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Supported Carriers"))
                .extendedDescription(Text.of("View a list of supported phone carriers for receiving SMS messages."))
                .executor(new RelayCarriersSubcommand(this))
                .arguments(GenericArguments.seq(
                        GenericArguments.optional(GenericArguments.integer(Text.of("page"))),
                        GenericArguments.flags().flag("s").flag("u").buildWith(GenericArguments.optional(GenericArguments.string(Text.of("phone"))))
                ))
                .build());

        /**
         * /relay account [page]
         */
        relaySubcommands.put(Collections.singletonList("account"), CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Manage your contact methods."))
                .extendedDescription(Text.of("View a list of your contact methods."))
                .executor(new RelayAccountSubcommand(this))
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
                .build());

        /**
         * /relay edit [-e][-p] <contactMethodId> [-c] [carrier]
         */
        relaySubcommands.put(Collections.singletonList("edit"), CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Edit a contact method."))
                .extendedDescription(Text.of("Edit contact method of the given contact method."))
                .executor(new RelayEditSubcommand(this))
                .arguments(GenericArguments.seq(
                        GenericArguments.flags().flag("e").flag("p").flag("r").buildWith(GenericArguments.string(Text.of("contactMethod"))),
                        GenericArguments.flags().flag("c").buildWith(GenericArguments.optional(GenericArguments.string(Text.of("carrier"))))
                ))
                .build());

        /**
         * /relay send [[-p] [player]] [[-t] [template]] <message>
         */
        relaySubcommands.put(Collections.singletonList("send"), CommandSpec.builder()
                .permission("relay.send")
                .description(Text.of("Send an email or sms message."))
                .extendedDescription(Text.of("If the player(s) have a relay account, send them an email or sms message."))
                .executor(new RelaySendSubcommand(this))
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.string(Text.of("player")), "p")
                                .valueFlag(GenericArguments.string(Text.of("template")), "t")
                                .buildWith(GenericArguments.remainingJoinedStrings(Text.of("message")))
                )
                .build());

        /**
         * /relay send [[-p] [player]] [[-t] [template]] <message>
         */
        relaySubcommands.put(Arrays.asList("sendall", "all"), CommandSpec.builder()
                .permission("relay.sendall")
                .description(Text.of("Send an email or sms message to all."))
                .extendedDescription(Text.of("Send all contacts an email or sms message."))
                .executor(new RelaySendSubcommand(this))
                .arguments(
                        GenericArguments.flags()
                                .valueFlag(GenericArguments.string(Text.of("template")), "t")
                                .buildWith(GenericArguments.remainingJoinedStrings(Text.of("message")))
                )
                .build());

        /**
         * /relay
         */
        CommandSpec relayCommand = CommandSpec.builder()
                .description(Text.of("Relay edit, carriers, account, send"))
                .extendedDescription(Text.of("Manage your relay account or send an email or sms message."))
                .executor(new RelayCommand(this))
                .children(relaySubcommands)
                .build();

        /**
         * /unregister
         */
        CommandSpec unregisterCommand = CommandSpec.builder()
                .permission("relay.register")
                .description(Text.of("Unregister your contact methods."))
                .extendedDescription(Text.of("Remove all contact information from this server."))
                .executor(new UnregisterCommand(this))
                .arguments(
                        GenericArguments.flags().flag("d").flag("c").buildWith(GenericArguments.optional(GenericArguments.string(Text.of("contactMethod"))))
                )
                .build();

        if (this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.SETTINGS, DefaultConfigStorageService.ENABLED).getBoolean()) {

            game.getCommandManager().register(this, registerCommand, "register");
            game.getCommandManager().register(this, relayCommand, "relay");
            game.getCommandManager().register(this, unregisterCommand, "unregister");

        }

        game.getEventManager().registerListeners(this, new PlayerChangeNameListener(this));

    }

    private void saveSensitiveData() throws Exception {

        CommentedConfigurationNode settingsConfig = this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.SETTINGS);
        CommentedConfigurationNode emailConfig = this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.EMAIL_ACCOUNT_INFO);
        CommentedConfigurationNode mandrillConfig = this.getDefaultConfigService().getConfig().getNode(DefaultConfigStorageService.MANDRILL_ACCOUNT_INFO);

        String name = emailConfig.getNode(DefaultConfigStorageService.EMAIL_NAME).getString();
        String emailAddress = emailConfig.getNode(DefaultConfigStorageService.EMAIL_ADDRESS).getString();
        String username = emailConfig.getNode(DefaultConfigStorageService.EMAIL_USERNAME).getString();
        String password = emailConfig.getNode(DefaultConfigStorageService.EMAIL_PASSWORD).getString();
        String host = emailConfig.getNode(DefaultConfigStorageService.EMAIL_HOST).getString();
        int port = emailConfig.getNode(DefaultConfigStorageService.EMAIL_PORT).getInt();
        boolean ssl = emailConfig.getNode(DefaultConfigStorageService.EMAIL_SSL).getBoolean();

        String mandrillUsername = mandrillConfig.getNode(DefaultConfigStorageService.MANDRILL_USERNAME).getString();
        String mandrillPassword = mandrillConfig.getNode(DefaultConfigStorageService.MANDRILL_PASSWORD).getString();

        Gateway gateway = new Gateway();
        Gateway mandrill = new Gateway();

        EncryptionUtil encryptionUtil = new EncryptionUtil(settingsConfig.getNode(DefaultConfigStorageService.SECRET_KEY).getString());

        if (!name.equals("") && !username.equals("") && !password.equals("") && !host.equals("") && port != 0) {

            gateway.setName(name);
            gateway.setEmailAddress(encryptionUtil.encrypt(emailAddress));
            gateway.setUsername(encryptionUtil.encrypt(username));
            gateway.setPassword(encryptionUtil.encrypt(password));
            gateway.setHost(host);
            gateway.setPort(port);
            gateway.sslEnabled(ssl);

            if (gateway.isValid()) {
                this.gatewayStorageService.saveGateway(gateway);
            }

        }

        if (!mandrillUsername.equals("") && !mandrillPassword.equals("")) {

            mandrill.setName("Mandrill");
            mandrill.setEmailAddress(encryptionUtil.encrypt(""));
            mandrill.setUsername(encryptionUtil.encrypt(mandrillUsername));
            mandrill.setPassword(encryptionUtil.encrypt(mandrillPassword));
            mandrill.setHost("smtp.mandrillapp.com");
            mandrill.setPort(587);
            mandrill.sslEnabled(true);

            if (mandrill.isValid()) {
                this.gatewayStorageService.saveGateway(mandrill);
            }

        }

        this.defaultConfigService.clearSensitiveData();

    }

    private void loadDefaultTemplate() {

        this.defaultTemplate = this.getDefaultConfigService().getConfig()
                .getNode(DefaultConfigStorageService.MESSAGES, DefaultConfigStorageService.EMAIL_TEMPLATE).getString();

    }

}
