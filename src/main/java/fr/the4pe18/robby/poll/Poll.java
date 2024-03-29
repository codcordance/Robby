package fr.the4pe18.robby.poll;

import com.github.breadmoirai.Emoji;
import fr.the4pe18.robby.RobbyEmbed;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Poll {
    private Color GREEN, RED, YELLOW;

    private Member author;
    private Message message;
    private String question;
    private Integer yes;
    private Integer no;
    private MessageChannel channel;


    public Poll(MessageChannel channel, String question, Member author) {
        initColor();
        this.author = author;
        this.question = question;
        this.message = channel.sendMessage(new RobbyEmbed("Sondage", "génération").setColor(YELLOW).build()).complete();
        message.addReaction("yes_light:705019068154904617").complete();
        message.addReaction("no_light:705019039251955734").complete();
        this.channel = channel;
        this.yes = 0;
        this.no = 0;
    }
;
    private void initColor() {
        float[] hsb = new float[3];
        Color.RGBtoHSB(119, 178, 85, hsb);
        GREEN = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        Color.RGBtoHSB(221, 46, 68, hsb);
        RED = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
        Color.RGBtoHSB(255, 204, 77, hsb);
        YELLOW = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public void updateMessage() {
        RobbyEmbed embed = new RobbyEmbed("__**SONDAGE**__", "```"+question+"```");
        embed.setAuthor(author.getNickname(), author.getUser().getAvatarUrl());
        embed.setThumbnail("https://i.ibb.co/yBM0H2k/3e531d8e171629e9433db0bb431b2e12.png");
        embed.addField("Salon", channel.getName(), false);
        embed.addField("<:yes:705017799554236536>" + " Oui", yes.toString(), true);
        embed.addField("<:no:705017781556346950>" + " Non", no.toString(), true);
        embed.setImage(getPieUrl());
        embed.setColor(yes > no ? GREEN : yes < no ? RED : YELLOW);
        this.message.editMessage(embed.build()).complete();
    }

    public Message getMessage() {
        return message;
    }

    public void proceed(MessageReactionAddEvent event) {
        if (event.getReactionEmote().isEmote() && event.getReactionEmote().getEmote().getId().equalsIgnoreCase("705019068154904617")) {
            message.retrieveReactionUsers("no_light:705019039251955734").queue(users -> {
                if (users.contains(event.getUser())) {
                    message.removeReaction("no_light:705019039251955734", event.getUser()).queue();
                }
            });
            yes++;
            updateMessage();
        } else if (event.getReactionEmote().isEmote() && event.getReactionEmote().getEmote().getId().equalsIgnoreCase("705019039251955734")) {
            message.retrieveReactionUsers("yes_light:705019068154904617").queue(users -> {
                if (users.contains(event.getUser())) {
                    message.removeReaction("yes_light:705019068154904617", event.getUser()).queue();
                }
            });
            no++;
            updateMessage();
        } else {
            if (event.getReactionEmote().isEmote()) message.removeReaction(event.getReactionEmote().getEmote(), Objects.requireNonNull(event.getUser())).complete();
            else message.removeReaction(event.getReactionEmote().getEmoji(), Objects.requireNonNull(event.getUser())).complete();
        }
    }

    public void proceed(MessageReactionRemoveEvent event) {
        if (event.getReactionEmote().isEmote() && event.getReactionEmote().getEmote().getId().equalsIgnoreCase("705019068154904617")) {
            yes--;
            updateMessage();
        } else if (event.getReactionEmote().isEmote() && event.getReactionEmote().getEmote().getId().equalsIgnoreCase("705019039251955734")) {
            no--;
            updateMessage();
        }
    }

    private String getPieUrl() {
        return "https://api.4pe18.fr/graph/pieyesno/" + yes.toString() + "-" + no.toString();
    }
}
