package fr.the4pe18.robby;

import fr.the4pe18.robby.audit.AuditsManager;
import fr.the4pe18.robby.commands.*;
import fr.the4pe18.robby.deploy.DeploymentManager;
import fr.the4pe18.robby.events.ChatListener;
import fr.the4pe18.robby.events.ReactionListener;
import fr.the4pe18.robby.events.SpecialBlindTest;
import fr.the4pe18.robby.events.VocalListener;
import fr.the4pe18.robby.exceptions.PatchAlreadyLoadedException;
import fr.the4pe18.robby.exceptions.PatchAlreadyRegisteredException;
import fr.the4pe18.robby.exceptions.PatchNotRegisteredException;
import fr.the4pe18.robby.poll.PollManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

/**
 * @author 4PE18
 */
public class Robby {
  private CommandManager commandManager;
  private AuditsManager auditsManager;
  private DeploymentManager deploymentManager;
  private PollManager pollManager;

  private static JDA jdaInstance;
  private Connection connection;
  private PrivateChannel channel4pe18;

  public CommandManager getCommandManager() { return commandManager; }

  public void start(String[] args)
      throws LoginException, PatchAlreadyRegisteredException,
             PatchNotRegisteredException, PatchAlreadyLoadedException,
             SQLException {
    if (args.length < 2) {
      System.err.println(
          "Précissez le token du bot et le mot de passe mysql !");
      return;
    }

    JDABuilder builder = JDABuilder.createDefault(args[0]);
    builder.setBulkDeleteSplittingEnabled(false);
    builder.setCompression(Compression.NONE);
    builder.setMemberCachePolicy(MemberCachePolicy.ALL);
    builder.enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
    builder.setActivity(Activity.playing("!help"));

    this.auditsManager = new AuditsManager(this);
    this.commandManager = new CommandManager(this);
    this.deploymentManager = new DeploymentManager(this);
    this.pollManager = new PollManager(this);
    this.getDeploymentManager().registerDefaultPatches();
    // OldPatchManager patchManager = new OldPatchManager(this);

    // SpecialApril specialApril = new SpecialApril(this);
    // SpecialBlindTest specialBlindTest = new SpecialBlindTest(this);

    getCommandManager().addCommand(new PollCommand(this));
    getCommandManager().addCommand(new DebugCommand());
    getCommandManager().addCommand(new DeployCommand(this));
    getCommandManager().addCommand(new DevoirsCommand());
    getCommandManager().addCommand(new AuditCommand(getAuditsManager()));
    getCommandManager().addCommand(new ClearCommand());
    getCommandManager().addCommand(new LearnCommand());
    getCommandManager().addCommand(new GourceCommand());
    // getCommandManager().addCommand(new BlindTestCommand(specialBlindTest));
    getCommandManager().addCommand(new HelpCommand(this));
    getCommandManager().addCommand(new PresenceCommand());
    getCommandManager().addCommand(new MeCommand());

    // getCommandManager().addCommand(new AprilCommand(specialApril));

    builder.addEventListeners(new ChatListener(this));
    builder.addEventListeners(new ReactionListener(this));
    builder.addEventListeners(new VocalListener(this));

    // builder.addEventListeners(specialApril);
    builder.setAutoReconnect(true);
    jdaInstance = builder.build();
    System.out.println("Robby by 4PE18 is running.");
    channel4pe18 =
        jdaInstance.openPrivateChannelById(266208886204137472L).complete();
    getChannel4pe18().sendMessage("Running !").complete();
    try {
      openConnection(args[1]);
    } catch (SQLException e) {
      System.err.println("Can't open connection !");
    }
  }

  public void openConnection(String pass) throws SQLException {
    System.out.println("connecting to mysql database...");
    if (connection != null && !connection.isClosed())
      return;
    synchronized (this) {
      if (connection != null && !connection.isClosed())
        return;
      connection = DriverManager.getConnection(
          "jdbc:mysql://localhost:3306/robby?serverTimezone=UTC", "root", pass);
      System.out.println("connected!");
    }
  }

  public PrivateChannel getChannel4pe18() { return channel4pe18; }

  public AuditsManager getAuditsManager() { return auditsManager; }

  public DeploymentManager getDeploymentManager() { return deploymentManager; }

  public static JDA getJdaInstance() { return jdaInstance; }

  public Connection getConnection() { return connection; }

  public static void main(String[] args)
      throws LoginException, PatchAlreadyRegisteredException,
             PatchNotRegisteredException, PatchAlreadyLoadedException,
             SQLException {
    (new Robby()).start(args);
  }

  public PollManager getPollManager() { return pollManager; }
}
