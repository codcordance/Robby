package fr.the4pe18.robby.deploy;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * @author 4PE18
 */
public interface PatchStepWorker<T> {
    T work(DeploymentContext context) throws Exception;
}
