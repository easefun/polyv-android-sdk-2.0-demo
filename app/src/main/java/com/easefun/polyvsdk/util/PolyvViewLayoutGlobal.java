package com.easefun.polyvsdk.util;

import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.marquee.PolyvMarqueeView;
import com.easefun.polyvsdk.player.PolyvPlayerAnswerView;
import com.easefun.polyvsdk.player.PolyvPlayerAudioCoverView;
import com.easefun.polyvsdk.player.PolyvPlayerAuditionView;
import com.easefun.polyvsdk.player.PolyvPlayerAuxiliaryView;
import com.easefun.polyvsdk.player.PolyvPlayerLightView;
import com.easefun.polyvsdk.player.PolyvPlayerMediaController;
import com.easefun.polyvsdk.player.PolyvPlayerPreviewView;
import com.easefun.polyvsdk.player.PolyvPlayerProgressView;
import com.easefun.polyvsdk.player.PolyvPlayerVolumeView;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.auxiliary.PolyvAuxiliaryVideoView;

/**
 *
 */
public class PolyvViewLayoutGlobal {
    private static PolyvViewLayoutGlobal instance = null;

    private PolyvViewLayoutGlobal() {

    }

    public synchronized static PolyvViewLayoutGlobal getInstance() {
        if (instance == null) {
            instance = new PolyvViewLayoutGlobal();
        }

        return instance;
    }

    private RelativeLayout videoLayout = null;

    public RelativeLayout getVideoLayout() {
        return videoLayout;
    }

    public void setVideoLayout(RelativeLayout videoLayout) {
        this.videoLayout = videoLayout;
    }

    public void videoLayoutDestroy() {
        if (videoLayout != null) {
            ViewGroup lastParent = (ViewGroup) videoLayout.getParent();
            if (lastParent != null) {
                lastParent.removeView(lastParent);
            }

            PolyvVideoView videoView = (PolyvVideoView) videoLayout.findViewById(R.id.polyv_video_view);
            PolyvPlayerMediaController mediaController = (PolyvPlayerMediaController) videoLayout.findViewById(R.id.polyv_player_media_controller);
            PolyvPlayerAnswerView questionView = (PolyvPlayerAnswerView) videoLayout.findViewById(R.id.polyv_player_question_view);
            PolyvPlayerAuditionView auditionView = (PolyvPlayerAuditionView) videoLayout.findViewById(R.id.polyv_player_audition_view);
            PolyvPlayerAuxiliaryView auxiliaryView = (PolyvPlayerAuxiliaryView) videoLayout.findViewById(R.id.polyv_player_auxiliary_view);
            PolyvPlayerPreviewView firstStartView = (PolyvPlayerPreviewView) videoLayout.findViewById(R.id.polyv_player_first_start_view);
            PolyvPlayerAudioCoverView coverView = (PolyvPlayerAudioCoverView) videoLayout.findViewById(R.id.polyv_cover_view);

            if (videoView != null) {
                videoView.destroy();
            }

            if (questionView != null) {
                questionView.hide();
            }

            if (auditionView != null) {
                auditionView.hide();
            }

            if (auxiliaryView != null) {
                auxiliaryView.hide();
            }

            if (firstStartView != null) {
                firstStartView.hide();
            }

            if (coverView != null) {
                coverView.hide();
            }

            if (mediaController != null) {
                mediaController.disable();
            }

            videoLayout = null;
        }
    }
}
