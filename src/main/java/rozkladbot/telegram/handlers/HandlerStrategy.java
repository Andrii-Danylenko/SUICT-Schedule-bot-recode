package rozkladbot.telegram.handlers;

import rozkladbot.entities.HandlerContext;

public interface HandlerStrategy {
  void handleRequest(HandlerContext ctx);
}
