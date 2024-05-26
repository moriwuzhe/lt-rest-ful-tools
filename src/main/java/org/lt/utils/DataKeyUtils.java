package org.lt.utils;

import com.intellij.openapi.actionSystem.DataKey;
import org.lt.restful.navigation.action.RestServiceItem;

import java.util.List;

public class DataKeyUtils {

  public static final DataKey<List<RestServiceItem>> SERVICE_ITEMS = DataKey.create("SERVICE_ITEMS");

  private DataKeyUtils() {
  }
}
