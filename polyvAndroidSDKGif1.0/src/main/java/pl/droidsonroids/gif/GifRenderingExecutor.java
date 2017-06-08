package pl.droidsonroids.gif;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Default executor for rendering tasks - {@link ScheduledThreadPoolExecutor}
 * with 1 worker thread and {@link DiscardPolicy}.
 */
final class GifRenderingExecutor extends ScheduledThreadPoolExecutor {

    private GifRenderingExecutor() {
        super(1, new DiscardPolicy());
    }

    @SuppressWarnings("StaticNonFinalField") //double-checked singleton initialization
    private static volatile GifRenderingExecutor instance = null;

    public static GifRenderingExecutor getInstance() {
        if (instance == null) {
            synchronized (GifRenderingExecutor.class) {
                if (instance == null) {
                    instance = new GifRenderingExecutor();
                }
            }
        }
        return instance;
    }
}
