package com.orgware.cloudnarisample.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.util.List;

/**
 * Created by nandagopal on 12/9/16.
 */
public class CommonUtils {

  public static boolean isAvailable(Context ctx, Intent intent) {
    final PackageManager mgr = ctx.getPackageManager();
    List<ResolveInfo> list =
        mgr.queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
  }

}
