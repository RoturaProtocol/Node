package brs.http;

import brs.Asset;
import brs.assetexchange.AssetExchange;
import com.google.gson.JsonArray;

import java.util.Iterator;

abstract class AbstractAssetsRetrieval extends APIServlet.JsonRequestHandler {

  private final AssetExchange assetExchange;

  AbstractAssetsRetrieval(APITag[] apiTags, AssetExchange assetExchange, String... parameters) {
    super(apiTags, parameters);
    this.assetExchange = assetExchange;
  }

  JsonArray assetsToJson(Iterator<Asset> assets, long mininumQuantity) {
    final JsonArray assetsJsonArray = new JsonArray();

    while (assets.hasNext()) {
      final Asset asset = assets.next();

      int tradeCount = assetExchange.getTradeCount(asset.getId());
      int transferCount = assetExchange.getTransferCount(asset.getId());
      int accountsCount = assetExchange.getAssetAccountsCount(asset, mininumQuantity, true);
      long circulatingSupply = assetExchange.getAssetCirculatingSupply(asset, true);

      assetsJsonArray.add(JSONData.asset(asset, tradeCount, transferCount, accountsCount, circulatingSupply));
    }

    return assetsJsonArray;
  }
}
