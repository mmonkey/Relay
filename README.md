# Relay
An email and sms messaging plugin and API for Minecraft servers running [Sponge](https://github.com/SpongePowered/Sponge).

## Relay Plugin
### Overview
Relay works by using javax.mail to send emails. Most cellphone companies provide an email address that can receive messages on, these show as sms messages. Relay saves all contacts internally and keeps all users contact information encrypted. **NOTE:** although Relay keeps your contact information encrypted, since this project is open source, it would not take much to break the encryption, so be careful.  
  
Once a user has registered a contact method, they will receive a verification email containing a 4 digit code. This code must be entered on the server in order to activate that users contact information. Once verified they can receive messages on that account.  
  
Relay stores informaiton per-server, so users will have to setup their contact information on any server that they would like to receive messages from. 

### Setup
For server owners, setup is a breeze:  

1. Download the [latest version](/releases/latest) of Relay.
2. Drop `Relay.jar` into your servers mod folder.
3. Run the server - this will generate the configuration files.
4. Open `confg/Relay/confg.conf` in a text editor
5. Enter your outgoing SMTP email information or Mandrill account information.

**Note:** It is recommended to use Mandrill for outgoing email, a free account can send upto 12k emails per month and uses an API key for authorization. No passwords are needed to send emails through Mandrill, unlike using just an email provider.

### Updating
1. Download the [latest version](/releases/latest) of Relay.
2. Stop the server.
3. Replace `Relay.jar` in your servers mod folder.
4. Start the server.

### Commands
* `/register email <address>` - Register an email address to recieve email messages.
* `/register phone <number>` - Register a phone number to recieve sms messages.
* `/register activate <code>` - Activate a contact method (email or sms) with the provided activation code.

* `/relay carriers` - View phone carriers that are compatible with Relay.
* `/relay group` - Manage contact groups.
* `/relay account` - See a list of contact methods you have registered.
* `/relay edit <contactMethod>` - View and edit details of a contact method.
* `/relay send [[-p] [player]] [[-g] [groupName]] [[-t] [template]] <message>` - Send an email or sms message to a player or all contacts in a group.
* `/relay sendall [[-g] [groupName]] [[-t] [template]] <message>` - Send an email or sms to all contacts or all contacts in a group.

* `/unregister [contactMethod]` - unreigster your account or a contact method on your account.
  
**Note:** Groups are currently not implemented
.
### Carriers
As of the current release, these are the available carriers. You may create an [issue](/issues) to request a new carrier get added.

* Alltel
* AT&T
* Cingular
* Claro
* Metro PCS
* Nextel
* O2
* Oi
* Orange
* Powertel
* Sprint
* SunCom
* T-Mobile
* Tim
* US Cellular
* Verizon
* Virgin Mobile
* Vivo

## Relay API
### Overview
Relay provides services and classes to create and send email and sms messages. For email messages, relay provides an EmailMessage class and TemplatingService that uses [Mustache](https://github.com/spullara/mustache.java) to create modual HTML email messages. The RelayService provides serveral methods for sending messages to one or many players. The RelayService allows players to passed in by name, UUID or the Player object. Relay tries to keep the most up-to-date player name by hooking into PlayerJoinEvent and PlayerUpdateEvent and checking wether the player's name has changed.

### Services
Relay provides two services that any plugin can hook into.

**RelayService**

**TemplatingService**

### Email Messages
**Creating an HTML email message**

**Creating a template**