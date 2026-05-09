package rozkladbot.telegram.factories;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.exceptions.NoSuchHandlerStrategyFoundException;
import rozkladbot.telegram.handlers.HandlerStrategy;

@Component
public class HandlerFactory {
  private final Map<String, HandlerStrategy> strategies;

  public HandlerFactory(List<HandlerStrategy> strategies) {
    this.strategies = strategies.stream().collect(Collectors.toMap(strategy ->
        strategy.getClass().getSimpleName().toLowerCase(), Function.identity()));
  }

  public HandlerStrategy getStrategy(String strategyName) {
    return Optional.ofNullable(strategies.get(strategyName)).orElseThrow(() -> new NoSuchHandlerStrategyFoundException(
        ErrorConstants.NO_SUCH_STRATEGY_FOUND.formatted(strategyName)));
  }
}
