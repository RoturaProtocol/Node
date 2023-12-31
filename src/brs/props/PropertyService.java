package brs.props;

import java.util.List;

import signum.net.NetworkParameters;

public interface PropertyService {

  Boolean getBoolean(String propName, boolean assume);

  Boolean getBoolean(Prop<Boolean> prop);

  int getInt(Prop<Integer> prop);

  String getString(Prop<String> name);

  List<String> getStringList(Prop<String> name);
  
  void setNetworkParameters(NetworkParameters params);
}
