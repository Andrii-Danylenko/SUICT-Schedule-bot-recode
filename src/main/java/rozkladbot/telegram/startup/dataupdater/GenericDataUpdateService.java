package rozkladbot.telegram.startup.dataupdater;

import lombok.RequiredArgsConstructor;
import rozkladbot.services.web.requestservice.WebRequestService;
import rozkladbot.services.web.urlbuilder.QueryBuilder;

@RequiredArgsConstructor
public abstract class GenericDataUpdateService {

  protected final WebRequestService webRequestService;
  protected final QueryBuilder queryBuilder;


  public abstract void updateData();
}
