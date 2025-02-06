package rozkladbot.entities;

import rozkladbot.enums.Command;

import java.util.Objects;

public class DelayedCommand {
    // Basically just a Time-To-Live counter
    private byte TTL = 50;
    private Command command;

    public DelayedCommand(Command command) {
        this.command = command;
    }


    public byte getTTL() {
        return TTL;
    }

    public void setTTL(byte TTL) {
        this.TTL = TTL;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void decrementAndSet() {
        TTL -= 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelayedCommand that = (DelayedCommand) o;
        return command == that.command;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(command);
    }
}
