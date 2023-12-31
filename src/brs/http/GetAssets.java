package brs.http;

import brs.Asset;
import brs.assetexchange.AssetExchange;
import brs.util.Convert;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;

import static brs.http.JSONResponses.INCORRECT_ASSET;
import static brs.http.JSONResponses.UNKNOWN_ASSET;
import static brs.http.common.Parameters.ASSETS_PARAMETER;
import static brs.http.common.Parameters.QUANTITY_MININUM_QNT_PARAMETER;
import static brs.http.common.ResultFields.ASSETS_RESPONSE;

public final class GetAssets extends APIServlet.JsonRequestHandler {

  private final AssetExchange assetExchange;

  public GetAssets(AssetExchange assetExchange) {
    super(new APITag[]{APITag.AE}, ASSETS_PARAMETER, ASSETS_PARAMETER, ASSETS_PARAMETER, QUANTITY_MININUM_QNT_PARAMETER); // limit to 3 for testing
    this.assetExchange = assetExchange;
  }

  @Override
  protected
  JsonElement processRequest(HttpServletRequest req) {

    String[] assets = req.getParameterValues(ASSETS_PARAMETER);
    
    long minimumQuantity = Convert.parseUnsignedLong(req.getParameter(QUANTITY_MININUM_QNT_PARAMETER));

    JsonObject response = new JsonObject();
    JsonArray assetsJsonArray = new JsonArray();
    response.add(ASSETS_RESPONSE, assetsJsonArray);
    for (String assetIdString : assets) {
      if (assetIdString == null || assetIdString.isEmpty()) {
        continue;
      }
      try {
        Asset asset = assetExchange.getAsset(Convert.parseUnsignedLong(assetIdString));
        if (asset == null) {
          return UNKNOWN_ASSET;
        }

        int tradeCount = assetExchange.getTradeCount(asset.getId());
        int transferCount = assetExchange.getTransferCount(asset.getId());
        int accountsCount = assetExchange.getAssetAccountsCount(asset, minimumQuantity, true);
        long circulatingSupply = assetExchange.getAssetCirculatingSupply(asset, true);

        assetsJsonArray.add(JSONData.asset(asset, tradeCount, transferCount, accountsCount, circulatingSupply));
      } catch (RuntimeException e) {
        return INCORRECT_ASSET;
      }
    }
    return response;
  }

}
