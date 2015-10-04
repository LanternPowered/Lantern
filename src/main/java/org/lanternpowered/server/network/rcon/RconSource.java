package org.lanternpowered.server.network.rcon;

import java.util.Optional;

import org.lanternpowered.server.permission.SubjectBase;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.command.CommandSource;

public class RconSource extends SubjectBase implements org.spongepowered.api.util.command.source.RconSource {

    private final StringBuffer buffer = new StringBuffer();
    private final RconConnection connection;

    // Whether the rcon source is logged in
    private volatile boolean loggedIn;

    public RconSource(RconConnection connection) {
        this.connection = connection;
    }

    @Override
    public String getName() {
        return "Rcon{ " + this.connection.getAddress() + "}";
    }

    @Override
    public void sendMessage(Text... messages) {
        for (Text message : messages) {
            this.buffer.append(Texts.toPlain(message)).append('\n');
        }
    }

    @Override
    public void sendMessage(Iterable<Text> messages) {
        for (Text message : messages) {
            this.buffer.append(Texts.toPlain(message)).append('\n');
        }
    }

    @Override
    public String getIdentifier() {
        return this.getName();
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(this);
    }

    @Override
    public boolean getLoggedIn() {
        return this.loggedIn;
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public RconConnection getConnection() {
        return this.connection;
    }

    @Override
    public String getSubjectCollectionIdentifier() {
        return PermissionService.SUBJECTS_SYSTEM;
    }

    @Override
    public Tristate getPermissionDefault(String permission) {
        return Tristate.TRUE;
    }

    public String flush() {
        String result = this.buffer.toString();
        this.buffer.setLength(0);
        return result;
    }

    @Override
    public MessageSink getMessageSink() {
        return null;
    }

    @Override
    public void setMessageSink(MessageSink sink) {
    }

}
