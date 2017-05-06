package ogiba.stylablesharedialog;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ogiba on 03.05.2017.
 */

public class StylableShareDialogApplication extends Application {
    @Override
    public void onCreate() {
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
        super.onCreate();
    }
}
