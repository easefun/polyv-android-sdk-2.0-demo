package com.easefun.polyvsdk.cast;

import static net.polyv.android.common.libs.lang.concurrent.ThreadsKt.postToMainThread;

import net.polyv.android.common.libs.lang.Duration;
import net.polyv.android.media.cast.model.vo.PLVMediaCastDevice;
import net.polyv.android.media.cast.model.vo.PLVMediaCastException;
import net.polyv.android.media.cast.model.vo.PLVMediaCastPlayState;
import net.polyv.android.media.cast.model.vo.PLVMediaCastResource;
import net.polyv.android.media.cast.rx.PLVMediaCastControllerAdapterRxJava;
import net.polyv.android.media.cast.rx.PLVMediaCastManagerAdapterRxJava;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class PolyvAllCast {

    private static final String TAG = PolyvAllCast.class.getSimpleName();

    private IPLVScreencastListener screencastPlayerListener;
    @Nullable
    private PLVMediaCastControllerAdapterRxJava castController = null;

    public PolyvAllCast() {
        PLVMediaCastManagerAdapterRxJava.getListenerRegistry().getCurrentPosition()
                .observe(new Function1<Duration, Unit>() {
                    @Override
                    public Unit invoke(Duration position) {
                        if (screencastPlayerListener != null) {
                            screencastPlayerListener.onPositionUpdate(position.toSeconds());
                        }
                        return Unit.INSTANCE;
                    }
                });
        PLVMediaCastManagerAdapterRxJava.getListenerRegistry().getOnPlayCompleted()
                .observe(new Function1<Duration, Unit>() {
                    @Override
                    public Unit invoke(Duration duration) {
                        if (screencastPlayerListener != null) {
                            screencastPlayerListener.onComplete();
                        }
                        return Unit.INSTANCE;
                    }
                });
        PLVMediaCastManagerAdapterRxJava.getListenerRegistry().getPlayState()
                .observe(new Function1<PLVMediaCastPlayState, Unit>() {
                    @Override
                    public Unit invoke(PLVMediaCastPlayState state) {
                        if (screencastPlayerListener != null) {
                            if (state == PLVMediaCastPlayState.PLAYING) {
                                screencastPlayerListener.onStart();
                            } else if (state == PLVMediaCastPlayState.PAUSED) {
                                screencastPlayerListener.onPause();
                            } else if (state == PLVMediaCastPlayState.STOPPED) {
                                screencastPlayerListener.onStop();
                            }
                        }
                        return Unit.INSTANCE;
                    }
                });
    }

    public void setPlayerListener(IPLVScreencastListener listener) {
        this.screencastPlayerListener = listener;
    }

    public void browse() {
        PLVMediaCastManagerAdapterRxJava.scanDevices(Duration.seconds(5)).subscribe(
                new Consumer<List<PLVMediaCastDevice>>() {
                    @Override
                    public void accept(List<PLVMediaCastDevice> devices) throws Exception {
                        if (screencastPlayerListener != null) {
                            screencastPlayerListener.onDeviceScanned(devices);
                        }
                    }
                }
        );
    }

    @Nullable
    public PLVMediaCastControllerAdapterRxJava getCastController() {
        return castController;
    }

    // <editor-fold defaultstate="collapsed" desc="播放相关方法">

    public void playNetMedia(PLVMediaCastDevice device, PLVMediaCastResource resource, int startSeconds) {
        PLVMediaCastManagerAdapterRxJava.startCast(device, resource)
                .subscribe(
                        new Consumer<PLVMediaCastControllerAdapterRxJava>() {
                            @Override
                            public void accept(PLVMediaCastControllerAdapterRxJava controller) throws Exception {
                                castController = controller;
                                if (startSeconds > 0) {
                                    // 延迟200ms避免远端不响应seek
                                    postToMainThread(Duration.millis(200), new Function0<Unit>() {
                                        @Override
                                        public Unit invoke() {
                                            seekTo(startSeconds);
                                            return null;
                                        }
                                    });
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (screencastPlayerListener != null) {
                                    int errCode = throwable instanceof PLVMediaCastException ? ((PLVMediaCastException) throwable).getCode() : 0;
                                    screencastPlayerListener.onError("startCast", errCode, throwable.getMessage());
                                }
                            }
                        }
                );
    }

    public void resume() {
        if (castController != null) {
            castController.play().subscribe(
                    new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {

                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (screencastPlayerListener != null) {
                                int errCode = throwable instanceof PLVMediaCastException ? ((PLVMediaCastException) throwable).getCode() : 0;
                                screencastPlayerListener.onError("play", errCode, throwable.getMessage());
                            }
                        }
                    }
            );
        }
    }

    public void pause() {
        if (castController != null) {
            castController.pause().subscribe(
                    new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {

                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (screencastPlayerListener != null) {
                                int errCode = throwable instanceof PLVMediaCastException ? ((PLVMediaCastException) throwable).getCode() : 0;
                                screencastPlayerListener.onError("pause", errCode, throwable.getMessage());
                            }
                        }
                    }
            );
        }
    }

    public void stopPlay() {
        if (castController != null) {
            castController.stop().subscribe(
                    new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {

                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (screencastPlayerListener != null) {
                                int errCode = throwable instanceof PLVMediaCastException ? ((PLVMediaCastException) throwable).getCode() : 0;
                                screencastPlayerListener.onError("stop", errCode, throwable.getMessage());
                            }
                        }
                    }
            );
        }
        castController = null;
    }

    public void seekTo(int position) {
        if (castController != null) {
            castController.seek(Duration.seconds(position)).subscribe(
                    new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {

                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (screencastPlayerListener != null) {
                                int errCode = throwable instanceof PLVMediaCastException ? ((PLVMediaCastException) throwable).getCode() : 0;
                                screencastPlayerListener.onError("seek", errCode, throwable.getMessage());
                            }
                        }
                    }
            );
        }
    }

    public void setVolume(int volume) {
        volume = Math.max(0, Math.min(volume, 100));
        if (castController != null) {
            castController.setVolume(volume).subscribe(
                    new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {

                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (screencastPlayerListener != null) {
                                int errCode = throwable instanceof PLVMediaCastException ? ((PLVMediaCastException) throwable).getCode() : 0;
                                screencastPlayerListener.onError("setVolume", errCode, throwable.getMessage());
                            }
                        }
                    }
            );
        }
    }

    public void volumeUp() {
        if (castController == null) {
            return;
        }
        int volume = castController.getListenerRegistry().getVolume().getValue();
        setVolume(volume + 5);
    }

    public void volumeDown() {
        if (castController == null) {
            return;
        }
        int volume = castController.getListenerRegistry().getVolume().getValue();
        setVolume(volume - 5);
    }

    // </editor-fold>

}
