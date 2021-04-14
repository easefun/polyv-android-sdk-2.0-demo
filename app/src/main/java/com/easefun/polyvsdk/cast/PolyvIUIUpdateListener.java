package com.easefun.polyvsdk.cast;

public interface PolyvIUIUpdateListener {

    int STATE_SEARCH_SUCCESS = 1;
    int STATE_SEARCH_ERROR = 2;
    int STATE_SEARCH_NO_RESULT = 3;
    int STATE_CONNECT_SUCCESS = 10;
    int STATE_DISCONNECT = 11;// 连接断开
    int STATE_CONNECT_FAILURE = 12;// 连接失败
    int STATE_PLAY = 20;
    int STATE_PAUSE = 21;
    int STATE_COMPLETION = 22;
    int STATE_STOP = 23;
    int STATE_SEEK = 24;
    int STATE_POSITION_UPDATE = 25;
    int STATE_PLAY_ERROR = 26;
    int STATE_LOADING = 27;
    int STATE_INPUT_SCREENCODE = 28;
    int RELEVANCE_DATA_UNSUPPORT = 29;

    void onUpdateState(int state, Object object);

    void onUpdateText(String msg);

}
