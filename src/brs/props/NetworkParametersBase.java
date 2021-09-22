package brs.props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import brs.TransactionType;
import brs.fluxcapacitor.FluxValue;
import brs.fluxcapacitor.HistoricalMoments;
import brs.fluxcapacitor.FluxValue.ValueChange;
import brs.http.APITransactionManager;
import brs.http.APIServlet.HttpRequestHandler;
import brs.services.ParameterService;

public class NetworkParametersBase implements NetworkParameters {
  
  private final Properties properties = new Properties();
  protected ParameterService parameterService;
  protected APITransactionManager apiTransactionManager;
  
  @Override
  public void initialize(ParameterService parameterService, APITransactionManager apiTransactionManager) {
    this.parameterService = parameterService;
    this.apiTransactionManager = apiTransactionManager;
  }
  
  protected <T> void setProperty(Prop<T> prop, String value) {
    properties.setProperty(prop.getName(), value);
  }
  
  protected <T> void setFluxValue(FluxValue<T> fluxValue, HistoricalMoments moment, T value){
    ValueChange<T> valueChange = new FluxValue.ValueChange<T>(moment, value);
    List<ValueChange<T>> valueChages = new ArrayList<>();
    valueChages.add(valueChange);
    fluxValue.updateValueChanges(valueChages);
  }

  @Override
  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  @Override
  public Map<Byte, Map<Byte, TransactionType>> getExtraTransactionSubtypes() {
    return new HashMap<>();
  }

  @Override
  public Map<String, HttpRequestHandler> getExtraAPIs() {
    return new HashMap<>();
  }

  @Override
  public Map<Long, Integer> getBlockRewardDistribution(int height) {
    return null;
  }
  
}
